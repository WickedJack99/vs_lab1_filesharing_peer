/**
 * @author Aaron Moser
 */
package vslab1.src.Request.Data;

import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;

public interface Requestable {
    public EDataType getType();
    public Peer getSender();
    public Peer getReceiver();
    public void execute(SendingQueue sendingQueue);
}
