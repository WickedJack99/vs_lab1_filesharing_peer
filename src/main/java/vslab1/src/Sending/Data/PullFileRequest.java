/**
 * @author Aaron Moser
 */
package vslab1.src.Sending.Data;

import vslab1.src.Peers.Peer;

public record PullFileRequest(Peer sender, Peer receiver, String fileName) implements Sendable {
    
    @Override
    public EDataType getType() {
        return EDataType.PullFileRequest;
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
        String response =
            "{\"pullFile\":\"" + fileName + "\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return response.getBytes();
    }

}
