package vslab1.src.FileReaderWriter;

import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;

public class FileReaderWriter {

    public enum EUpdated {
        NotUpdated,
        Updated
    }

    private static Peer thisPeerBuffer = null;

    public static Peer getThisPeer(EUpdated updateState) {
        switch (updateState) {
            case NotUpdated: {
                if (thisPeerBuffer == null) {
                    return getThisPeer(EUpdated.Updated);
                } else {
                    return thisPeerBuffer;
                }   
            }
            case Updated: {
                return updateThisPeerBufferFromFile();
            }
            default: {
                System.err.println("Unknown peer update type.");
                return null;
            }
        }  
    }

    private static synchronized Peer updateThisPeerBufferFromFile() {
        // TODO unimplemented
        // read from file this peers data
        
        String ipAddress = null;
        int port = 5000;
        thisPeerBuffer = new Peer(ipAddress, port, null, EOnlineState.Online); // change to valid data once implemented
        return thisPeerBuffer;
    }

    public static synchronized void updatePeerList(Peer updatedPeer) {
        // TODO update peer list
    }
}
