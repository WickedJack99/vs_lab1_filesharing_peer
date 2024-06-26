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
import vslab1.src.Sending.Data.JoinRequest;
import vslab1.src.Sending.Data.LeaveNotification;
import vslab1.src.Sending.Data.OnlineStateRequest;
import vslab1.src.Sending.Data.PublishFileNameNotification;
import vslab1.src.Sending.Data.PullFileRequest;
import vslab1.src.Timeout.JobList;
import vslab1.src.Timeout.TimeoutJob;

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
        System.out.println("-----------------------------------------------");
        System.out.println("This peer is online and ready to take commands.");
        System.out.println("-----------------------------------------------");
        while (inputThreadRunning) {
            try {
                System.out.println("Enter command:");
                String userInput = inputScanner.nextLine();
                String[] inputArgs = userInput.split(" ");
                
                if (inputArgs.length >= 1) {
                    String command = inputArgs[0];
                    switch (command) {
                        case "Exit": {
                            List<Peer> peers = FileReaderWriter.getPeers();
                            peers.forEach((peer) -> {
                                if (peer.onlineState() == EOnlineState.Online) {
                                    sendingQueue.add(new LeaveNotification(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), peer));
                                }
                            });
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
                        case "Join": {
                            if (inputArgs.length < 3) {
                                System.err.println("Too few arguments.");
                            } else if (inputArgs.length > 3) {
                                System.err.println("Too many arguments.");
                            } else {
                                String ip = inputArgs[1];
                                try {
                                    int port = Integer.parseInt(inputArgs[2]);
                                    Peer friend = new Peer(ip, port, null, EOnlineState.Unknown);
                                    sendingQueue.add(new JoinRequest(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), friend));
                                    jobList.add(new TimeoutJob(System.currentTimeMillis(), friend));
                                } catch (Exception e) {
                                    System.err.println("Exception parsing port. Please reenter command.");
                                }
                            }
                        }break;
                        case "Leave": {
                            if (inputArgs.length < 1) {
                                System.err.println("Too few arguments.");
                            } else if (inputArgs.length > 1) {
                                System.err.println("Too many arguments.");
                            } else {
                                List<Peer> peers = FileReaderWriter.getPeers();
                                peers.forEach((peer) -> {
                                    if (peer.onlineState() == EOnlineState.Online) {
                                        sendingQueue.add(new LeaveNotification(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), peer));
                                    }
                                });
                            }
                        }break;
                        case "ShowNodes": {
                            List<Peer> peers = FileReaderWriter.getPeers();
                            peers.forEach((peer) -> {
                                if (peer.onlineState() != EOnlineState.Online) {
                                    sendingQueue.add(new OnlineStateRequest(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), peer));
                                    // Start timeout which after 3 seconds sets status of specific peer to offline.
                                    jobList.add(new TimeoutJob(System.currentTimeMillis(), peer));
                                } else {
                                    System.out.println("Peer @ " + peer.ipAddress() + ":" + peer.port() + " online");
                                }
                            });   
                        }break;
                        case "ShowFiles": {
                            Peer thisPeer = FileReaderWriter.getThisPeer(EUpdateFlag.Update);
                            System.out.println("Files on system: " + thisPeer.ipAddress() + ":" + thisPeer.port());
                            thisPeer.filesMap().forEach((fileName, filePath) -> {
                                System.out.println("-> " + fileName + " @ path:" + filePath);
                            });
                            List<Peer> peers = FileReaderWriter.getPeers();
                            peers.forEach((peer) -> {
                                System.out.println("Files on system: " + peer.ipAddress() + ":" + peer.port());
                                peer.filesMap().forEach((fileName, filePath) -> {
                                    System.out.println("-> " + fileName + " @ path:" + filePath);
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
                                    String[] pathParts = inputArgs[1].split(FileReaderWriter.getFileSeparatorRegex());
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
                                            sendingQueue.add(new PublishFileNameNotification(FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), peerObject, fileName));
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
                        case "AddPeer": {
                            if (inputArgs.length < 3) {
                                System.err.println("Too few arguments.");
                            } else if (inputArgs.length > 3) {
                                System.err.println("Too many arguments.");
                            } else {
                                String ipAddress = inputArgs[1];
                                int port = Integer.valueOf(inputArgs[2]);

                                FileReaderWriter.updatePeer(new Peer(ipAddress, port, null, EOnlineState.Unknown));
                                FileReaderWriter.getThisPeer(EUpdateFlag.Update);
                            }
                        }break;
                        default: {
                            System.err.println("Unknown command.");
                        }break;
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
            }   
            System.out.println("-----------------------------------------------");         
        }
    }

    public void terminate() {
        inputThreadRunning = false;
        this.interrupt();
    }

   
}
