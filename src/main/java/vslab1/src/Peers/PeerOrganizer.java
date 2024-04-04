package vslab1.src.Peers;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PeerOrganizer {
    private Peer localPeer;

    public Peer getLocalPeer() {
        return this.localPeer;
    }

    public List<Peer> getPeers() {
        List<Peer> peersAsList = new LinkedList<Peer>();

        try (FileReader reader = new FileReader("./src/main/resources/peers.json")) {
            // Parse JSON file into a JSONObject
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            
            JSONArray peers = jsonObject.getJSONArray("peers");

            peers.forEach((peer) -> {
                
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
