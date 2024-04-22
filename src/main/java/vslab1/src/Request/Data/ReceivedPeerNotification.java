package vslab1.src.Request.Data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;

/**
 * Received peer message from peers in network to this newly joined peer. This peer will update its
 * peer list with received file list and set online state of the peer to online.
 */
public record ReceivedPeerNotification(Peer sender, Peer receiver, JSONArray sender_files) implements Requestable {

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
    public void execute(SendingQueue sendingQueue) {
        Map<String, String> filesMap = new HashMap<String, String>();
        sender_files.forEach((file) -> {
            filesMap.put(file.toString(), "");
        });
        FileReaderWriter.updatePeer(new Peer(sender.ipAddress(), sender.port(), filesMap, EOnlineState.Online));
    }
}