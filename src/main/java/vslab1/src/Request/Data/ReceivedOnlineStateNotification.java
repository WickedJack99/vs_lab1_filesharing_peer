/**
 * @author Aaron Moser
 */
package vslab1.src.Request.Data;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;
import vslab1.src.Sending.Data.OnlineStateNotification;
import vslab1.src.Timeout.JobList;

public record ReceivedOnlineStateNotification(Peer sender, Peer receiver, JobList jobList) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.OnlineStateNotification;
    }

    @Override
    public Peer getSender() {
        return sender;
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }

    @Override
    public void execute(SendingQueue sendingQueue) {
        FileReaderWriter.updatePeer(new Peer(sender.ipAddress(), sender.port(), null, EOnlineState.Online));
        sendingQueue.add(new OnlineStateNotification(receiver, sender));
    }
    
}
