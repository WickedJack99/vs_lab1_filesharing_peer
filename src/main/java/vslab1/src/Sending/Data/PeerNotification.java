package vslab1.src.Sending.Data;

import java.util.Map;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.FileReaderWriter.FileReaderWriter.EUpdateFlag;
import vslab1.src.Peers.Peer;

/**
 * Sent to peer that sent a join request to a friend in the network.
 * The friend who received the join request, sends peerJoined to the
 * other peers and they respond with this message, informing about their 
 * presence and file list.
 */ 
public record PeerNotification(Peer sender, Peer receiver) implements Sendable {

    @Override
    public EDataType getType() {
        return EDataType.PeerNotification;
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
        String fileList = "";
        Map<String, String> files = FileReaderWriter.getThisPeer(EUpdateFlag.Update).filesMap();
        for (Map.Entry<String, String> file : files.entrySet()) {
            fileList += file.getKey() + ",";
        }

        // Remove last comma if fileList contains at least one file that has name of one char
        if (fileList.length() >= 2) {
            fileList = fileList.substring(0, fileList.length() - 1);
        }

        String response = 
            "{\"peer\":\"\"" +
            "," + 
            "\"ipPort\":\"" + sender.ipAddress() + ":" + sender.port() + "\"" +
            "," +
            "\"files\":[" +
            fileList +
            "]}";
        return response.getBytes();
    }
    
}
