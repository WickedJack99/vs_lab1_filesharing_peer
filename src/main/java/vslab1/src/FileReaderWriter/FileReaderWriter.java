package vslab1.src.FileReaderWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import vslab1.src.Constants;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;

public class FileReaderWriter {

    public enum EUpdated {
        NotUpdated,
        Updated
    }

    private static Peer thisPeerBuffer = null;

    /**
     * Creates directories at given path and file where info about this and other peers is getting stored.
     * @param filesPath the path where to find or create the info files.
     */
    public static void createInfoFilesIfNotExisting(String filesPath) {
        Path path = Paths.get(filesPath);
        boolean validPath = true;
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Directories created successfully.");
            } catch (Exception e) {
                System.out.println("Failed to create directories: " + e.getMessage());
                validPath = false;
            }
        }
        if (validPath) {
            String appendix = null;
            if (filesPath.endsWith(File.separator)) {
                appendix = "";
            } else {
                appendix = File.separator;
            }
            Path peerFilePath = Paths.get(filesPath + appendix + Constants.PEERCONFIGFILENAME);
            if (!Files.exists(peerFilePath)) {
                try {
                    Files.createFile(peerFilePath);
                    System.out.println("Created " + Constants.PEERCONFIGFILENAME + " file.");
                } catch (IOException e) {
                    System.out.println("Failed to create " + Constants.PEERCONFIGFILENAME + " file.");
                    e.printStackTrace();
                }
            }
        }
    }

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
