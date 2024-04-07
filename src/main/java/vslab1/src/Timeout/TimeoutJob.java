/**
 * @author Aaron Moser
 */
package vslab1.src.Timeout;

import vslab1.src.Peers.Peer;

public record TimeoutJob(long currentSystemTimeMillis, Peer peerToWaitFor) {
    
}
