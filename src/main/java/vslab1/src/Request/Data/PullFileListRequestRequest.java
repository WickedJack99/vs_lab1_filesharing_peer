package vslab1.src.Request.Data;

import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;

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
