/**
 * @author Aaron Moser
 */

package vslab1.src.Input;

import vslab1.src.Terminatable;
import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.FileReaderWriter.FileReaderWriter.EUpdateFlag;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.OnlineStateRequest;
import vslab1.src.Sending.Data.PublishFileNameNotification;
import vslab1.src.Sending.Data.PullFileRequest;
import vslab1.src.Timeout.JobList;
import vslab1.src.Timeout.TimeoutJob;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class InputThread extends Thread implements Terminatable {

    private Scanner inputScanner = null;

    private Terminatable receiverThread;
    private Terminatable senderThread;
    private Terminatable requestExecuterThread;
    private Terminatable timeoutThread;

    private SendingQueue sendingQueue;

    private boolean inputThreadRunning = true;

    private JobList jobList = null;

    public InputThread(Scanner inputScanner, Terminatable senderThread, Terminatable receiverThread, Terminatable requestExecuterThread, Terminatable timeoutThread, SendingQueue sendingQueue, JobList jobList) {
        this.inputScanner = inputScanner;
        
        this.senderThread = senderThread;
        this.receiverThread = receiverThread;
        this.requestExecuterThread = requestExecuterThread;
        this.timeoutThread = timeoutThread;

        this.sendingQueue = sendingQueue;

        this.jobList = jobList;
    }

    @Override
    public void run() {
        System.out.println("This peer is online and ready to take commands.\n");
        while (inputThreadRunning) {
            try {
                System.out.println("Enter command:");
                String userInput = inputScanner.nextLine();
                String[] inputArgs = userInput.split(" ");
                
                if (inputArgs.length >= 1) {
                    String command = inputArgs[0];

                    System.out.println(command);

                    switch (command) {
                        case "exit": {
                            // Terminates sender thread, receiver thread and this thread.
                            if (senderThread != null) {
                                senderThread.terminate();
                            }
                            if (receiverThread != null) {
                                receiverThread.terminate();
                            }
                            if (requestExecuterThread != null) {
                                requestExecuterThread.terminate();
                            }
                            if (timeoutThread != null) {
                                timeoutThread.terminate();
                            }
                            this.terminate();
                        }break;
                        case "ShowNodes": {
                            List<Peer> peers = FileReaderWriter.getPeers();
                            peers.forEach((peer) -> {
                                sendingQueue.add(new OnlineStateRequest(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), peer));
                                // Start timeout which after 3 seconds sets status of specific peer to offline.
                                jobList.add(new TimeoutJob(System.currentTimeMillis(), peer));
                            });
                        }break;
                        case "ShowFiles": {
                            Peer thisPeer = FileReaderWriter.getThisPeer(EUpdateFlag.Update);
                            System.out.println("Files on system: " + thisPeer.ipAddress() + ":" + thisPeer.port());
                            thisPeer.filesMap().forEach((fileName, filePath) -> {
                                System.out.println(fileName + " @ path:" + filePath);
                            });
                            List<Peer> peers = FileReaderWriter.getPeers();
                            peers.forEach((peer) -> {
                                System.out.println("Files on system: " + peer.ipAddress() + ":" + peer.port());
                                peer.filesMap().forEach((fileName, filePath) -> {
                                    System.out.println(fileName + " @ path:" + filePath);
                                });
                            });
                        }break;
                        case "PublishFile": {
                            if (inputArgs.length < 2) {
                                System.err.println("Too few arguments.");
                            } else if (inputArgs.length > 2) {
                                System.err.println("Too many arguments.");
                            } else {
                                // Check if file path exists.
                                Path peerFilePath = Paths.get(inputArgs[1]);
                                if (!Files.exists(peerFilePath)) {
                                    System.err.println("File path doesn't exist.");
                                } else {
                                    // If path exists, extract last element, which will be the file name.
                                    String[] pathParts = inputArgs[1].split(File.separator);
                                    String fileName = pathParts[pathParts.length-1];

                                    if (FileReaderWriter.hasFile(fileName) == null) {
                                        // Add file to own list.
                                        Peer thisPeer = FileReaderWriter.getThisPeer(EUpdateFlag.Update);
                                        thisPeer.addFile(fileName, inputArgs[1]);
                                        FileReaderWriter.updatePeer(thisPeer);

                                        // Notify all peers about file.
                                        List<Peer> peers = FileReaderWriter.getPeers();
                                        peers.forEach((peer) -> {
                                            Peer peerObject = (Peer)peer;
                                            if (peerObject.onlineState() == EOnlineState.Online) {
                                                sendingQueue.add(new PublishFileNameNotification(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), peerObject, fileName));
                                            }
                                        });
                                    } else {
                                        System.err.println("Error, file already exists.");
                                    }                                    
                                }
                            }
                        }break;
                        case "GetFile": {
                            if (inputArgs.length < 2) {
                                System.err.println("Too few arguments.");
                            } else if (inputArgs.length > 2) {
                                System.err.println("Too many arguments.");
                            } else {
                                // check which peer has file
                                // send get message to that peer
                                // if own file, just print
                                Peer thisPeer = FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate);
                                Peer peerThatHasFile = FileReaderWriter.hasFile(inputArgs[1]);

                                if (thisPeer.equals(peerThatHasFile)) {
                                    System.out.println("File name: " + inputArgs[1]);
                                    System.out.println("File content: " + FileReaderWriter.getFirstAndLast20Byte(FileReaderWriter.readFile(thisPeer.filesMap().get(inputArgs[1]))));
                                } else if (peerThatHasFile == null) {
                                    System.err.println("File not found.");
                                } else {
                                    sendingQueue.add(new PullFileRequest(thisPeer, peerThatHasFile, inputArgs[1]));
                                }
                            }
                        }break;
                        default: {
                            System.out.println("Unknown command.");
                        }break;
                    }
                }
            } catch (Exception e) {

            }            
        }
    }

    public void terminate() {
        inputThreadRunning = false;
        this.interrupt();
    }

   
}
