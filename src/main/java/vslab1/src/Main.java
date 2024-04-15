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
import vslab1.src.Timeout.JobList;
import vslab1.src.Timeout.TimeoutThread;
import vslab1.src.View.MainFrame;

public class Main {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        FileReaderWriter.createInfoFilesIfNotExisting(Constants.PEERCONFIGFILEPATH);

        MainFrame frame = new MainFrame();

        DatagramSocket datagramSocket = NetworkController.getSocket(inputScanner);

        SendingQueue sendingQueue = new SendingQueue();
        ReceivingQueue receivingQueue = new ReceivingQueue();

        JobList jobList = new JobList();

        TimeoutThread timeoutThread = new TimeoutThread(jobList);
        timeoutThread.start();
        RequestExecuterThread requestExecuterThread = new RequestExecuterThread(sendingQueue, receivingQueue, jobList);
        requestExecuterThread.start();
        ReceiverThread receiverThread = new ReceiverThread(datagramSocket, receivingQueue);
        receiverThread.start();
        SenderThread senderThread = new SenderThread(datagramSocket, sendingQueue);
        senderThread.start();
        InputThread inputThread = new InputThread(inputScanner, senderThread, receiverThread, requestExecuterThread, timeoutThread, sendingQueue, jobList);
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
}