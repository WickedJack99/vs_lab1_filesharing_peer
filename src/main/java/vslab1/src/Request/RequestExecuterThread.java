/**
 * @author Aaron Moser
 */

package vslab1.src.Request;

import vslab1.src.Terminatable;
import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.FileReaderWriter.FileReaderWriter.EUpdated;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Receiving.ReceivedData;
import vslab1.src.Receiving.ReceivingQueue;
import vslab1.src.Request.Data.OnlineStateNotificationRequest;
import vslab1.src.Request.Data.OnlineStateRequestRequest;
import vslab1.src.Request.Data.PublishFileNameNotificationRequest;
import vslab1.src.Request.Data.PullFileListRequestRequest;
import vslab1.src.Request.Data.PullFileRequestRequest;
import vslab1.src.Request.Data.Requestable;
import vslab1.src.Request.Data.SendFileReplyRequest;
import vslab1.src.Sending.SendingQueue;

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
        this.interrupt();
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
