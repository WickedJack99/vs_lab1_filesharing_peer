/**
 * @author Aaron Moser
 */

package vslab1.src.Input;

import vslab1.src.Terminatable;
import vslab1.src.Peers.PeerOrganizer;
import vslab1.src.Sending.SendingQueue;

import java.util.Scanner;

public class InputThread extends Thread implements Terminatable {

    private Scanner inputScanner = null;

    private Terminatable receiverThread;
    private Terminatable senderThread;
    private Terminatable requestExecuterThread;

    private SendingQueue sendingQueue;

    private PeerOrganizer peerOrganizer = new PeerOrganizer();

    private boolean inputThreadRunning = true;

    public InputThread(Scanner inputScanner, Terminatable senderThread, Terminatable receiverThread, Terminatable requestExecuterThread, SendingQueue sendingQueue) {
        this.inputScanner = inputScanner;
        
        this.senderThread = senderThread;
        this.receiverThread = receiverThread;
        this.requestExecuterThread = requestExecuterThread;
        this.sendingQueue = sendingQueue;
    }

    @Override
    public void run() {
        System.out.println("This peer is online and ready to take commands.\n");
        while (inputThreadRunning) {
            try {
                System.out.println("Enter command:");
                String userInput = inputScanner.nextLine();
                String[] inputArgs = userInput.split(":");
                
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
                            this.terminate();
                        }break;
                        case "ShowNodes": {
                            // TODO send onlineStateRequests to all peers in json file
                            // Start timeouts for all requests
                            // Update online states according to received, timeouted messages
                        }break;
                        case "ShowFiles": {
                            // Display own files information of jsons
                        }break;
                        case "PublishFile": {
                            if (inputArgs.length < 2) {
                                System.err.println("Too few arguments.");
                            } else if (inputArgs.length > 2) {
                                System.err.println("Too many arguments.");
                            } else {
                                // get list of online peers from json
                                // for each peer online send publish file name
                                //sendingQueue.add(new PublishFileNameNotification(peerOrganizer.getLocalPeer(), null, command));
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
