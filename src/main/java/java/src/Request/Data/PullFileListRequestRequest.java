package java.src.Request.Data;

import java.src.Peers.Peer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.EDataType;
import java.src.Sending.Data.PullFileListRequest;

public record PullFileListRequestRequest(Peer sender, Peer receiver) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.PullFileListRequest;
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
        // Do nothing since functionality is not necessary.
    }
    
}
