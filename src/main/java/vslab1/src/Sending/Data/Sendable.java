/**
 * @author Aaron Moser
 */

package vslab1.src.Sending.Data;

import vslab1.src.Peers.Peer;

/**
 * An instance of this class represents data to send to another peer, by also providing information
 * about this peer and the receiving peer.
 */
public interface Sendable {
    public EDataType getType();
    public Peer getSender();
    public Peer getReceiver();
    public byte[] getMessage();
}