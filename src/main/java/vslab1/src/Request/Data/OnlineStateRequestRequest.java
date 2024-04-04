package vslab1.src.Request.Data;

import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;
import vslab1.src.Sending.Data.OnlineStateNotification;

public record OnlineStateRequestRequest(Peer sender, Peer receiver) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.OnlineStateRequest;
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
        // This is receiver and wants to notify the sender about its online state.
        sendingQueue.add(new OnlineStateNotification(receiver, sender));
    }
    
}
