/**
 * @author Aaron Moser
 */

package vslab1.src.Peers;

import java.util.List;

public record Peer(String ipAddress, int port, List<String> fileList, EOnlineState onlineState) {

    public void addFile(String fileName) {
        if (!fileList.contains(fileName)) {
            fileList.add(fileName);
        }
    }
}
