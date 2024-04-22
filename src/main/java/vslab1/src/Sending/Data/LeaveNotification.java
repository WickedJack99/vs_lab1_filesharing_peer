package vslab1.src.Sending.Data;

import vslab1.src.Peers.Peer;

/**
 * Sent to all online peers if this peer goes offline.
 */
public record LeaveNotification(Peer sender, Peer receiver) implements Sendable {

    @Override
    public EDataType getType() {
        return EDataType.LeaveNotification;
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
            "{\"leave\":\"\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return request.getBytes();
    }
}
