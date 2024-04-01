package java.src.Request.Data;

import java.src.Peers.Peer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.EDataType;

public interface Requestable {
    public EDataType getType();
    public Peer getSender();
    public Peer getReceiver();
    public void execute(SendingQueue sendingQueue);
}
