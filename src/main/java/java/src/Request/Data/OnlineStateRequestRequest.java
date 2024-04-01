package java.src.Request.Data;

import java.src.Peers.Peer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.EDataType;
import java.src.Sending.Data.OnlineStateNotification;

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
