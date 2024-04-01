package java.src.Request.Data;

import java.src.FileReaderWriter.FileReaderWriter;
import java.src.Peers.EOnlineState;
import java.src.Peers.Peer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.EDataType;

public record OnlineStateNotificationRequest(Peer sender, Peer receiver) implements Requestable {

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
        FileReaderWriter.updatePeerList(new Peer(sender.ipAddress(), sender.port(), null, EOnlineState.Online));
    }
    
}
