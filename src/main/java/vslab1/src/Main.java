/**
 * @author Aaron Moser
 */

package vslab1.src;

import java.net.DatagramSocket;

import java.util.Scanner;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Input.InputThread;
import vslab1.src.Input.NetworkController;
import vslab1.src.Receiving.ReceiverThread;
import vslab1.src.Receiving.ReceivingQueue;
import vslab1.src.Request.RequestExecuterThread;
import vslab1.src.Sending.SenderThread;
import vslab1.src.Sending.SendingQueue;

public class Main {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        FileReaderWriter.createInfoFilesIfNotExisting(Constants.PEERCONFIGFILEPATH);

        DatagramSocket datagramSocket = getSocket(inputScanner);

        SendingQueue sendingQueue = new SendingQueue();
        ReceivingQueue receivingQueue = new ReceivingQueue();

        RequestExecuterThread requestExecuterThread = new RequestExecuterThread(sendingQueue, receivingQueue);
        requestExecuterThread.start();
        ReceiverThread receiverThread = new ReceiverThread(datagramSocket, receivingQueue);
        receiverThread.start();
        SenderThread senderThread = new SenderThread(datagramSocket, sendingQueue);
        senderThread.start();
        InputThread inputThread = new InputThread(inputScanner, senderThread, receiverThread, requestExecuterThread, sendingQueue);
        inputThread.start();
        
        try {
            requestExecuterThread.join();
            receiverThread.join();
            senderThread.join();
            inputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        datagramSocket.close();
        inputScanner.close();
    }

    /**
     * Asks user to enter ip and port until those are valid and applicable and creates a DatagramSocket with given values.
     * @return a DatagramSocket with ip and port specified by user input.
     */
    public static DatagramSocket getSocket(Scanner inputScanner) {
        DatagramSocket datagramSocket = null;
        boolean receivedValidIP = false;
        boolean receivedValidAndFreePort = false;
        String localIpAddress = null;
        int localPort = -1;
        // User has to enter a valid ip.
        while (!receivedValidIP) {
            System.out.println("Enter ip:");
            localIpAddress = inputScanner.nextLine();
            receivedValidIP = NetworkController.hasNetworkInterfaceWithIP(localIpAddress);
            if (!receivedValidIP) {
                System.err.println("Error: IP is not valid for this system. Please re-enter.");
            }
        }
        // User has to enter a valid port.
        while (!receivedValidAndFreePort) {
            System.out.println("Enter port:");
            String localPortAsString = inputScanner.nextLine();
            localPort = Integer.valueOf(localPortAsString);
            datagramSocket = NetworkController.tryBindToPort(localIpAddress, localPort);
            if (datagramSocket == null) {
                System.err.println("Error: Port is not available for this ip. Please re-enter.");
            } else {
                receivedValidAndFreePort = true;
            }
        }
        return datagramSocket;
    }
}