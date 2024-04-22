package vslab1.src.Sending.Data;

import vslab1.src.Peers.Peer;

/**
 * Sent by friend of peer which sent a join request to all peers in the network.
 * Logic, that sent to all peers realized in ReceivedJoinRequest.
 */
public record PeerJoinedNotification(Peer sender, Peer receiver) implements Sendable {

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
    public byte[] getMessage() {
        String request =
            "{\"peerJoined\":\"\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return request.getBytes(); 
    }
    
}
