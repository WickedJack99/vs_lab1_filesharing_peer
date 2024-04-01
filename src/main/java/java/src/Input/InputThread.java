/**
 * @author Aaron Moser
 */

package java.src.Input;

import java.src.Terminatable;
import java.src.Peers.Peer;
import java.src.Peers.PeerOrganizer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.PublishFileNameNotification;
import java.util.Scanner;

public class InputThread extends Thread implements Terminatable {

    private Terminatable receiverThread;
    private Terminatable senderThread;

    private SendingQueue sendingQueue;

    private PeerOrganizer peerOrganizer = new PeerOrganizer();

    private boolean inputThreadRunning = true;

    private boolean receivedValidIP = false;
    private boolean receivedValidAndFreePort = false;

    public InputThread(Terminatable senderThread, Terminatable receiverThread, SendingQueue sendingQueue) {
        this.senderThread = senderThread;
        this.receiverThread = receiverThread;
        this.sendingQueue = sendingQueue;
    }

    @Override
    public void run() {

        String localIpAddress = null;
        int localPort = -1;

        Scanner scanner = new Scanner(System.in);
        while (!receivedValidIP) {
            System.out.println("Enter ip:");

            localIpAddress = scanner.nextLine();

            receivedValidIP = NetworkController.hasNetworkInterfaceWithIP(localIpAddress);
        }

        while (!receivedValidAndFreePort) {
            System.out.println("Enter port:");

            String localPortAsString = scanner.nextLine();

            localPort = Integer.valueOf(localPortAsString);

            receivedValidAndFreePort = NetworkController.isPortFree(localPort);
        }

        while (inputThreadRunning) {
            
            System.out.println("This peer is online.\n");

            System.out.println("Enter command:");

            String userInput = scanner.nextLine();

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
        }
        scanner.close();
    }

    public void terminate() {
        inputThreadRunning = false;
    }
}
