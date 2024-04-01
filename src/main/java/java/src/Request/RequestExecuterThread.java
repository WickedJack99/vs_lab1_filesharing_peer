/**
 * @author Aaron Moser
 */

package java.src.Request;

import java.src.Terminatable;
import java.src.FileReaderWriter.FileReaderWriter;
import java.src.FileReaderWriter.FileReaderWriter.EUpdated;
import java.src.Peers.EOnlineState;
import java.src.Peers.Peer;
import java.src.Receiving.ReceivedData;
import java.src.Receiving.ReceivingQueue;
import java.src.Request.Data.OnlineStateNotificationRequest;
import java.src.Request.Data.OnlineStateRequestRequest;
import java.src.Request.Data.PublishFileNameNotificationRequest;
import java.src.Request.Data.PullFileListRequestRequest;
import java.src.Request.Data.PullFileRequestRequest;
import java.src.Request.Data.Requestable;
import java.src.Request.Data.SendFileReplyRequest;
import java.src.Sending.SendingQueue;

import org.json.JSONObject;

public class RequestExecuterThread extends Thread implements Terminatable {
    
    private boolean requestExecuterThreadRunning = true;

    private SendingQueue sendingQueue = null;
    private ReceivingQueue receivingQueue = null;

    public RequestExecuterThread(SendingQueue sendingQueue, ReceivingQueue receivingQueue) {
        this.sendingQueue = sendingQueue;
        this.receivingQueue = receivingQueue;
    }
    
    @Override
    public void run() {
        while (requestExecuterThreadRunning) {
            try {
                ReceivedData receivedData = receivingQueue.take();

                JSONObject receivedDataAsJSONObject = new JSONObject(receivedData.data());

                Requestable request = toRequestable(receivedDataAsJSONObject);
                if (request != null) {
                    request.execute(sendingQueue);
                }

            } catch (InterruptedException e) {
                System.out.println("Error, receiving queue was interrupted. Terminating (this) request executer thread..");
                this.terminate();
            }
        }
    }

    public void terminate() {
        requestExecuterThreadRunning = false;
    }

    private static Requestable toRequestable(JSONObject receivedData) {
        if (receivedData.has("onlineState")) {
            String onlineState = receivedData.getString("onlineState");
            switch (onlineState) {
                case "": {
                    return new OnlineStateNotificationRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdated.NotUpdated));
                }
                case "online": {
                    return new OnlineStateRequestRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdated.NotUpdated));
                }
                default: {
                    System.err.println("Unknown online state.");
                    return null;
                }
            }
        }
        if (receivedData.has("pullFileList")) {
            return new PullFileListRequestRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdated.Updated));
        }
        if (receivedData.has("publishFileName")) {
            String fileName = receivedData.getString("publishFileName");
            return new PublishFileNameNotificationRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdated.NotUpdated), fileName);
        }
        if (receivedData.has("pullFile")) {
            String fileName = receivedData.getString("pullFile");
            return new PullFileRequestRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdated.NotUpdated), fileName);
        }
        if (receivedData.has("sendFile")) {
            String fileContent = receivedData.getString("sendFile");
            String fileName = receivedData.getString("fileName");

            return new SendFileReplyRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdated.NotUpdated), fileName, fileContent);
        }
        return null;
    }

    private static Peer parseIPPortField(JSONObject receivedData) {
        String ipPort = receivedData.getString("ipPort");
        String[] ipPortParts = ipPort.split(":");
        return new Peer(ipPortParts[0], Integer.parseInt(ipPortParts[1]), null, EOnlineState.Online);
    } 
}
