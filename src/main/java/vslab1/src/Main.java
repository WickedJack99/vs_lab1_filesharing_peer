/**
 * @author Aaron Moser
 */

package vslab1.src;

import java.net.DatagramSocket;
import java.net.SocketException;
import vslab1.src.Input.InputThread;
import vslab1.src.Receiving.ReceiverThread;
import vslab1.src.Receiving.ReceivingQueue;
import vslab1.src.Request.RequestExecuterThread;
import vslab1.src.Sending.SenderThread;
import vslab1.src.Sending.SendingQueue;

public class Main {
    public static void main(String[] args) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(5000);

            SendingQueue sendingQueue = new SendingQueue();
            ReceivingQueue receivingQueue = new ReceivingQueue();

            RequestExecuterThread requestExecuterThread = new RequestExecuterThread(sendingQueue, receivingQueue);
            requestExecuterThread.start();
            ReceiverThread receiverThread = new ReceiverThread(datagramSocket, receivingQueue);
            receiverThread.start();
            SenderThread senderThread = new SenderThread(datagramSocket, sendingQueue);
            senderThread.start();
            InputThread inputThread = new InputThread(senderThread, receiverThread, requestExecuterThread, sendingQueue);
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
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}