package vslab1.src.Request.Data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import vslab1.src.Constants;
import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public record PublishFileNameNotificationRequest(Peer sender, Peer receiver, String fileName) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.PublishFileNameNotification;
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
        Map<String, String> fileToAdd = new HashMap<String, String>();
        // Filepath is empty since this peer doesn't know paths only file names of other peers.
        fileToAdd.put(fileName, "");
        Peer peerToUpdate = new Peer(sender.ipAddress(), sender.port(), fileToAdd, EOnlineState.Online);
        FileReaderWriter.updatePeer(peerToUpdate);
    }
    
}
