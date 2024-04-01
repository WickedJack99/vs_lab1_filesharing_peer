package java.src.Sending.Data;

import java.src.Peers.Peer;

public record OnlineStateRequest(Peer sender, Peer receiver) implements Sendable {

    @Override
    public EDataType getType() {
        return EDataType.OnlineStateRequest;
    }

    @Override
    public Peer getSender() {
        return this.sender;
    }

    @Override
    public Peer getReceiver() {
        return this.receiver;
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"onlineState\":\"\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return request.getBytes();
    }
    
}
