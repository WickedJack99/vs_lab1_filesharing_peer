/**
 * @author Aaron Moser
 */

package java.src;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.src.Input.InputThread;
import java.src.Receiving.ReceiverThread;
import java.src.Receiving.ReceivingQueue;
import java.src.Sending.SenderThread;
import java.src.Sending.SendingQueue;

public class Main {
    public static void main(String[] args) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(5000);

            SendingQueue sendingQueue = new SendingQueue();
            ReceivingQueue receivingQueue = new ReceivingQueue();

            ReceiverThread receiverThread = new ReceiverThread(datagramSocket, receivingQueue);
            receiverThread.start();
            SenderThread senderThread = new SenderThread(datagramSocket, sendingQueue);
            senderThread.start();
            InputThread inputThread = new InputThread(senderThread, receiverThread, sendingQueue);
            inputThread.start();
            
            try {
                receiverThread.join();
                senderThread.join();
                inputThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            datagramSocket.close();  
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}