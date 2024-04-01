package java.src.Sending.Data;

import java.src.Peers.Peer;

public record OnlineStateNotification(Peer sender, Peer receiver) implements Sendable {

    @Override
    public EDataType getType() {
        return EDataType.OnlineStateNotification;
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
            "{\"onlineState\":\"online\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return request.getBytes();
    }
    
}
