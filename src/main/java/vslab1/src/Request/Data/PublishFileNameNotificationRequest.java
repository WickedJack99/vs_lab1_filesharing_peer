package vslab1.src.Request.Data;

import java.io.FileReader;
import java.io.FileWriter;
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
        String senderIpPort = sender.ipAddress() + ":" + sender.port();

        try {
            FileReader reader = new FileReader("./src/main/resources/peers.json");
            // Parse JSON file into a JSONObject
            JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
            reader.close();
            JSONArray peers = peerFileAsJSONObject.getJSONArray("peers");
            // Goes through json array and inserts new file at element that matches ip and port.
            peers.forEach((peer) -> {
                JSONObject peerAsJSONObject = (JSONObject)peer;
                if ((peerAsJSONObject).getString("ipPort").equals(senderIpPort)) {
                    peerAsJSONObject.append("files", fileName);
                }
            });

            FileWriter writer = new FileWriter("./src/main/resources/peers.json");
            // Writes the modified object back to the file.
            writer.write(peerFileAsJSONObject.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
