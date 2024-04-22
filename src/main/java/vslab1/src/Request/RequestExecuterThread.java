/**
 * @author Aaron Moser
 */

package vslab1.src.Request;

import vslab1.src.Terminatable;
import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.FileReaderWriter.FileReaderWriter.EUpdateFlag;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Receiving.ReceivedData;
import vslab1.src.Receiving.ReceivingQueue;
import vslab1.src.Request.Data.ReceivedJoinRequest;
import vslab1.src.Request.Data.ReceivedLeaveNotification;
import vslab1.src.Request.Data.ReceivedOnlineStateNotification;
import vslab1.src.Request.Data.OnlineStateRequestRequest;
import vslab1.src.Request.Data.ReceivedPeerJoinedNotification;
import vslab1.src.Request.Data.ReceivedPeerResponse;
import vslab1.src.Request.Data.PublishFileNameNotificationRequest;
import vslab1.src.Request.Data.PullFileListRequestRequest;
import vslab1.src.Request.Data.PullFileRequestRequest;
import vslab1.src.Request.Data.Requestable;
import vslab1.src.Request.Data.SendFileReplyRequest;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Timeout.JobList;

import org.json.JSONArray;
import org.json.JSONObject;

public class RequestExecuterThread extends Thread implements Terminatable {
    
    private boolean requestExecuterThreadRunning = true;

    private SendingQueue sendingQueue = null;
    private ReceivingQueue receivingQueue = null;

    private JobList jobList = null;

    public RequestExecuterThread(SendingQueue sendingQueue, ReceivingQueue receivingQueue, JobList jobList) {
        this.sendingQueue = sendingQueue;
        this.receivingQueue = receivingQueue;

        this.jobList = jobList;
    }
    
    @Override
    public void run() {
        while (requestExecuterThreadRunning) {
            try {
                ReceivedData receivedData = receivingQueue.take();

                Requestable request = toRequestable(receivedData.interpretAsJSONObject(), jobList);
                if (request != null) {
                    request.execute(sendingQueue);
                }

            } catch (InterruptedException e) {
                System.err.println("Error, receiving queue was interrupted. Terminating (this) request executer thread..");
                this.terminate();
            }
        }
    }

    public void terminate() {
        requestExecuterThreadRunning = false;
        this.interrupt();
    }

    private static Requestable toRequestable(JSONObject receivedData, JobList jobList) {
        if (receivedData != null) {
            if (receivedData.has("onlineState")) {
                String onlineState = receivedData.getString("onlineState");
                switch (onlineState) {
                    case "": {
                        return new ReceivedOnlineStateNotification(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), jobList);
                    }
                    case "online": {
                        return new OnlineStateRequestRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate));
                    }
                    default: {
                        System.err.println("Unknown online state.");
                        return null;
                    }
                }
            }
            if (receivedData.has("join")) {
                return new ReceivedJoinRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.Update));
            }
            if (receivedData.has("peerJoined")) {
                return new ReceivedPeerJoinedNotification(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.Update));
            }
            if (receivedData.has("peer")) {
                JSONArray files = receivedData.getJSONArray("files");
                return new ReceivedPeerResponse(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), files);
            }
            if (receivedData.has("leave")) {
                return new ReceivedLeaveNotification(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate));
            }
            if (receivedData.has("pullFileList")) {
                return new PullFileListRequestRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.Update));
            }
            if (receivedData.has("publishFileName")) {
                String fileName = receivedData.getString("publishFileName");
                return new PublishFileNameNotificationRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), fileName);
            }
            if (receivedData.has("pullFile")) {
                String fileName = receivedData.getString("pullFile");
                return new PullFileRequestRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), fileName);
            }
            if (receivedData.has("sendFile")) {
                String fileContent = receivedData.getString("sendFile");
                String fileName = receivedData.getString("fileName");
    
                return new SendFileReplyRequest(parseIPPortField(receivedData), FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate), fileName, fileContent);
            }
        }
        return null;
    }

    private static Peer parseIPPortField(JSONObject receivedData) {
        String ipPort = receivedData.getString("ipPort");
        String[] ipPortParts = ipPort.split(":");
        return new Peer(ipPortParts[0], Integer.parseInt(ipPortParts[1]), null, EOnlineState.Online);
    } 
}
