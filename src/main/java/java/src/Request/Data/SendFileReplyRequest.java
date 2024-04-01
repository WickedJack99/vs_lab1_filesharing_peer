package java.src.Request.Data;

import java.src.Peers.Peer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.EDataType;

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
