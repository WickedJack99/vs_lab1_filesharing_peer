/**
 * @author Aaron Moser
 */

package vslab1.src.Receiving;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import vslab1.src.Terminatable;

/**
 * An instance of a ReceiverThread is handling incoming traffic by putting data into an internal
 * ReceivingQueue. Which is then processed by the RequestExecuterThread.
 */
public class ReceiverThread extends Thread implements Terminatable {
    private boolean receiverThreadRunning = false;

    private DatagramSocket datagramSocket = null;
    private ReceivingQueue receivingQueue = null;

    public ReceiverThread(DatagramSocket datagramSocket, ReceivingQueue receivingQueue) {
        this.datagramSocket = datagramSocket;
        this.receivingQueue = receivingQueue;
        this.receiverThreadRunning = true;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[65535];
        DatagramPacket datagramPacket = null;

        while (receiverThreadRunning) {
            datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                datagramSocket.receive(datagramPacket);
                if (receiveBuffer != null) {
                    receivingQueue.add(new ReceivedData(data(receiveBuffer).toString()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Terminates this thread by setting its running boolean variable to false.
     */
    public void terminate() {
        receiverThreadRunning = false;
        this.interrupt();
    }

    /**
     * Source: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
     * @param a the byte array to build a String out of it.
     * @return a StringBuilder containing the data to build a String.
     */
    public static StringBuilder data(byte[] a) { 
        if (a == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret; 
    } 
}
