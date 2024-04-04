package vslab1.src.Sending.Data;

import vslab1.src.Peers.Peer;

/**
 * Is never getting sent because szenario is, that all peers come online and stay online forever.
 */
public record PullFileListRequest(Peer sender, Peer receiver) implements Sendable {

    @Override
    public EDataType getType() {
        return EDataType.PullFileListRequest;
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
            "{\"pullFileList\":\"\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"}";
        return response.getBytes();
    }
    
}
