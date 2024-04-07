/**
 * @author Aaron Moser
 */

package vslab1.src.Sending;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import vslab1.src.Terminatable;
import vslab1.src.Sending.Data.Sendable;

public class SenderThread extends Thread implements Terminatable {

    private DatagramSocket ds = null;
    private SendingQueue sendingQueue = null;

    public SenderThread(DatagramSocket ds, SendingQueue sendingQueue) {
        this.ds = ds;
        this.sendingQueue = sendingQueue;
    }

    private boolean senderThreadRunning = true;

    @Override
    public void run() {
        while (senderThreadRunning) {
            try {
                Sendable dataToSent = sendingQueue.take();

                byte[] fileData = dataToSent.getMessage();

                if (fileData != null) {
                    String receiverIpAddress = dataToSent.getReceiver().ipAddress();
                    int receiverPort = dataToSent.getReceiver().port();
                    int fileDataLength = fileData.length;

                    DatagramPacket dp =
                        new DatagramPacket(fileData, fileDataLength, InetAddress.getByName(receiverIpAddress), receiverPort);
                    ds.send(dp);
                }
            } catch (UnknownHostException e) {
                System.err.println("Error, host is unknown or unreachable.");
            } catch (IOException e) {
                System.err.println("Error, wasn't able to send data, because of problem with socket.");
            } catch (InterruptedException e) {
                System.err.println("Error, sending queue was interrupted. Terminating (this) sending thread..");
                this.terminate();
            }
        }
    }

    public void terminate() {
        senderThreadRunning = false;
        this.interrupt();
    }
}

