package vslab1.src.FileReaderWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import vslab1.src.Constants;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;

public class FileReaderWriter {

    public enum EUpdateFlag {
        Update,
        DoNotUpdate
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
                    writeInitialInfoToConfigFile();
                } catch (IOException e) {
                    System.out.println("Failed to create " + Constants.PEERCONFIGFILENAME + " file.");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeInitialInfoToConfigFile() {
        try {
            FileWriter writer = new FileWriter(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
            writer.write(Constants.PEERCONFIGFILEINITIALCONTENT);
            writer.close();
            System.out.println("Initialized config file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Peer getThisPeer(EUpdateFlag update) {
        if (update == EUpdateFlag.Update) {
            try {
                FileReader reader = new FileReader(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
                JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
                reader.close();
                JSONObject thisPeer = peerFileAsJSONObject.getJSONObject("peer");
                String ipAddress = thisPeer.getString("ipAddress");
                int port = thisPeer.getInt("port");
                thisPeerBuffer = new Peer(ipAddress, port, null, EOnlineState.Online);
                return thisPeerBuffer;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return thisPeerBuffer;
        }
    }

    public static List<Peer> getPeers() {
        try {
            List<Peer> peersList = new LinkedList<Peer>();
            FileReader reader = new FileReader(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
            JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
            reader.close();
            JSONArray peersJSONArray = peerFileAsJSONObject.getJSONArray("peers");

            peersJSONArray.forEach((peer) -> {
                JSONObject peerJSONObject = (JSONObject)peer;
                String peerIPAddress = peerJSONObject.getString("ipAddress");
                int peerPort = peerJSONObject.getInt("port");
                List<String> peerFilesList = new LinkedList<String>();
                JSONArray peerFiles = peerJSONObject.getJSONArray("files");
                peerFiles.forEach((file) -> {
                    peerFilesList.add((String)file);
                });
                String onlineState = peerJSONObject.getString("onlineState");
                if (onlineState.equals("online")) {
                    peersList.add(new Peer(peerIPAddress, peerPort, peerFilesList, EOnlineState.Online));
                } else if (onlineState.equals("offline")) {
                    peersList.add(new Peer(peerIPAddress, peerPort, peerFilesList, EOnlineState.Offline));
                } else {
                    peersList.add(new Peer(peerIPAddress, peerPort, peerFilesList, EOnlineState.Unknown));
                }
            });
            return peersList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void updatePeerList(Peer updatedPeer) {
        // TODO update peer list
    }
}
