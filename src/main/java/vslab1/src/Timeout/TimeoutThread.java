package vslab1.src.Timeout;

import vslab1.src.Terminatable;
import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;

public class TimeoutThread extends Thread implements Terminatable {

    private boolean timeoutThreadRunning = true;

    private JobList jobQueue = null;

    public TimeoutThread(JobList jobQueue) {
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (timeoutThreadRunning) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (TimeoutJob timeoutJob : jobQueue) {
                if (System.currentTimeMillis() - timeoutJob.currentSystemTimeMillis() >= 3000) {
                    String ipAddress = timeoutJob.peerToWaitFor().ipAddress();
                    int port = timeoutJob.peerToWaitFor().port();
                    Peer unreachablePeer = new Peer(ipAddress, port, null, EOnlineState.Offline);
                    FileReaderWriter.updatePeer(unreachablePeer);
                }
            }
        }
    }

    @Override
    public void terminate() {
        timeoutThreadRunning = false;
        this.interrupt();
    }
}
