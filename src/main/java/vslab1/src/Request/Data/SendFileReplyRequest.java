package vslab1.src.Request.Data;

import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;

public record SendFileReplyRequest(Peer sender, Peer receiver, String fileName, String fileContent) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.SendFileReply;
    }

    @Override
    public Peer getSender() {
        return sender;
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }

    @Override
    public void execute(SendingQueue sendingQueue) {
        System.out.println(fileName);
        // TODO method that takes string and returns first 20byte....last 20byte
    }
    
}
