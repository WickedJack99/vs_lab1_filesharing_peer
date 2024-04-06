package vslab1.src.FileReaderWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
                JSONArray filesArray = thisPeer.getJSONArray("files");
                Map<String, String> filesMap = new HashMap<String, String>();
                filesArray.forEach((file) -> {
                    JSONObject fileInfo = (JSONObject)file;
                    String fileName = fileInfo.getString("fileName");
                    String filePath = fileInfo.getString("filePath");
                    filesMap.put(fileName, filePath);
                });
                thisPeerBuffer = new Peer(ipAddress, port, filesMap, EOnlineState.Online);
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
                Map<String, String> peerFilesList = new HashMap<String, String>();
                JSONArray peerFiles = peerJSONObject.getJSONArray("files");
                peerFiles.forEach((file) -> {
                    JSONObject fileInfo = (JSONObject)file;
                    String fileName = fileInfo.getString("fileName");
                    String filePath = fileInfo.getString("filePath");
                    peerFilesList.put(fileName, filePath);
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

    public static synchronized void updatePeer(Peer peerToUpdate) {
        try {
            FileReader reader = new FileReader(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
            JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
            reader.close();

            JSONObject peerJSONObject = peerFileAsJSONObject.getJSONObject("peer");
            JSONArray peersJSONArray = peerFileAsJSONObject.getJSONArray("peers");

            if (peerJSONObject.getString("ipAddress").equals(peerToUpdate.ipAddress()) && (peerJSONObject.getInt("port") == peerToUpdate.port())) {
                if (peerToUpdate.onlineState() == EOnlineState.Online) {
                    peerJSONObject.put("onlineStatus", "online");
                } else if (peerToUpdate.onlineState() == EOnlineState.Offline) {
                    peerJSONObject.put("onlineStatus", "offline");
                } else {
                    peerJSONObject.put("onlineStatus", "unknown");
                }
                
                if (peerToUpdate.filesMap() != null) {
                    peerToUpdate.filesMap().forEach((fileName, filePath) -> {
                        boolean fileExists = false;
                        
                        for (Object file : peerJSONObject.getJSONArray("files")) {
                            JSONObject fileObject = (JSONObject)file;
                            if (fileObject.getString("fileName") == fileName) {
                                fileObject.put("filePath", filePath);
                                fileExists = true;
                                break;
                            }
                        }
    
                        if (fileExists == false) {
                            JSONObject fileInfo = new JSONObject();
                            fileInfo.put("fileName", fileName);
                            fileInfo.put("filePath", filePath);
                            peerJSONObject.append("files", fileInfo);
                        }
                    });
                }

                peerFileAsJSONObject.put("peer", peerJSONObject);
                FileWriter writer = new FileWriter(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
                // Writes the modified object back to the file.
                writer.write(peerFileAsJSONObject.toString());
                writer.close();
                getThisPeer(EUpdateFlag.Update);
                return;
            } else if (peerJSONObject.getString("ipAddress").equals("") && (peerJSONObject.getInt("port") == 0)){
                peerJSONObject.put("ipAddress", peerToUpdate.ipAddress());
                peerJSONObject.put("port", peerToUpdate.port());

                if (peerToUpdate.onlineState() == EOnlineState.Online) {
                    peerJSONObject.put("onlineStatus", "online");
                } else if (peerToUpdate.onlineState() == EOnlineState.Offline) {
                    peerJSONObject.put("onlineStatus", "offline");
                } else {
                    peerJSONObject.put("onlineStatus", "unknown");
                }
                
                if (peerToUpdate.filesMap() != null) {
                    peerToUpdate.filesMap().forEach((fileName, filePath) -> {
                        boolean fileExists = false;
                        
                        for (Object file : peerJSONObject.getJSONArray("files")) {
                            JSONObject fileObject = (JSONObject)file;
                            if (fileObject.getString("fileName") == fileName) {
                                fileObject.put("filePath", filePath);
                                fileExists = true;
                                break;
                            }
                        }
    
                        if (fileExists == false) {
                            JSONObject fileInfo = new JSONObject();
                            fileInfo.put("fileName", fileName);
                            fileInfo.put("filePath", filePath);
                            peerJSONObject.append("files", fileInfo);
                        }
                    });
                }
                
                peerFileAsJSONObject.put("peer", peerJSONObject);
                FileWriter writer = new FileWriter(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
                // Writes the modified object back to the file.
                writer.write(peerFileAsJSONObject.toString());
                writer.close();
                getThisPeer(EUpdateFlag.Update);
                return;
            } else {
                peersJSONArray.forEach((peer) -> {
                    JSONObject peerObject = (JSONObject)peer;
                    if (peerObject.getString("ipAddress").equals(peerToUpdate.ipAddress()) && (peerObject.getInt("port") == peerToUpdate.port())) {
                        if (peerToUpdate.onlineState() == EOnlineState.Online) {
                            peerObject.put("onlineStatus", "online");
                        } else if (peerToUpdate.onlineState() == EOnlineState.Offline) {
                            peerObject.put("onlineStatus", "offline");
                        } else {
                            peerObject.put("onlineStatus", "unknown");
                        }

                        peerToUpdate.filesMap().forEach((fileName, filePath) -> {
                            boolean fileExists = false;
                            
                            for (Object file : peerObject.getJSONArray("files")) {
                                JSONObject fileObject = (JSONObject)file;
                                if (fileObject.getString("fileName") == fileName) {
                                    fileObject.put("filePath", filePath);
                                    fileExists = true;
                                    break;
                                }
                            }
        
                            if (fileExists == false) {
                                JSONObject fileInfo = new JSONObject();
                                fileInfo.put("fileName", fileName);
                                fileInfo.put("filePath", filePath);
                                peerObject.append("files", fileInfo);
                            }
                        });
                    }
                });

                peerFileAsJSONObject.put("peers", peersJSONArray);
                FileWriter writer = new FileWriter(Constants.PEERCONFIGFILEPATH + File.separator + Constants.PEERCONFIGFILENAME);
                // Writes the modified object back to the file.
                writer.write(peerFileAsJSONObject.toString());
                writer.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Peer hasFile(String fileName) {
        Peer thisPeer = getThisPeer(EUpdateFlag.Update);
        if (thisPeer.filesMap().containsKey(fileName)) {
            return thisPeer;
        } else {
            List<Peer> peers = getPeers();
            for (Peer peer : peers) {
                if (peer.filesMap().containsKey(fileName)) {
                    return peer;
                }
            }
        }
        return null;
    }

    public static String readFile(String filePath) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFirstAndLast20Byte(String content) {
        if (content.length() > 40) {
            String first20Byte = content.substring(0, 20);
            String last20Byte = content.substring(content.length() - 20, content.length());
            return first20Byte + "...." + last20Byte;
        } else {
            return content;
        }
    }
}
