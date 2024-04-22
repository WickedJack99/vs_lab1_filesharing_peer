package vslab1.src.Request.Data;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;
import vslab1.src.Sending.Data.PeerJoinedNotification;
import vslab1.src.Sending.Data.PeerNotification;

/**
 * Received join request will trigger an update of peer that wants to join this 
 * friends peer network and this friend will send peerJoined to each peer in the network.
 */
public record ReceivedJoinRequest(Peer sender, Peer receiver) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.JoinRequest;
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
        Peer newPeer = new Peer(sender.ipAddress(), sender.port(), null, EOnlineState.Online);
        FileReaderWriter.updatePeer(newPeer);
        // Send own information back to new peer.
        sendingQueue.add(new PeerNotification(receiver, newPeer));
        // Notify other peers except the new peer about the new peer, which joined the network.
        FileReaderWriter.getPeers().forEach((peer) -> {
            // Except the new peer
            if (!peer.equals(newPeer)) {
                sendingQueue.add(new PeerJoinedNotification(newPeer, peer));
            }
        });
    }
    
}
