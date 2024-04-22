/**
 * @author Aaron Moser
 */

package vslab1.src.Peers;

import java.util.Map;

public record Peer(String ipAddress, int port, Map<String, String> filesMap, EOnlineState onlineState) {

    public void addFile(String fileName, String filePath) {
        if (!filesMap.containsKey(fileName)) {
            filesMap.put(fileName, filePath);
        }
    }

    public boolean equals(Peer other) {
        return this.ipAddress.equals(other.ipAddress) && (this.port == other.port);
    }

    public String onlineStateToString() {
        switch (onlineState) {
            case Online: {
                return "online";
            }
            case Offline: {
                return "offline";
            }
            case Unknown: {
                return "unknown";
            }
            default: {
                return "undefined";
            }
        }
    }
}
