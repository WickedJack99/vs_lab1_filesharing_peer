package vslab1.src.Request.Data;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;
import vslab1.src.Sending.Data.PeerNotification;

/**
 * Received peerJoined notification from friend of peer that joined. Updates information of received 
 * new peer to online and triggers peer response to peer that joined.
 */
public record ReceivedPeerJoinedNotification(Peer sender, Peer receiver) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.PeerJoinedNotification;
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
        FileReaderWriter.updatePeer(sender);
        sendingQueue.add(new PeerNotification(receiver, sender));
    }
    
}
