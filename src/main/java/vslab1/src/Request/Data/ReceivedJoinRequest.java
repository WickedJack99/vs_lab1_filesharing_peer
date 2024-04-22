package vslab1.src.Request.Data;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.FileReaderWriter.FileReaderWriter.EUpdateFlag;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;
import vslab1.src.Sending.Data.PeerJoinedNotification;

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
        FileReaderWriter.updatePeer(new Peer(sender.ipAddress(), sender.port(), null, EOnlineState.Online));

        FileReaderWriter.getPeers().forEach((peer) -> {
            sendingQueue.add(new PeerJoinedNotification(peer, FileReaderWriter.getThisPeer(EUpdateFlag.DoNotUpdate)));
        });
    }
    
}
