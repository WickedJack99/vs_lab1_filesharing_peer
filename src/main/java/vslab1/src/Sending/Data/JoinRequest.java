package vslab1.src.Sending.Data;

import vslab1.src.Peers.Peer;

public record JoinRequest(Peer sender, Peer receiver) implements Sendable {

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
    public byte[] getMessage() {
        String request =
            "{\"join\":\"\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return request.getBytes(); 
    }
}
