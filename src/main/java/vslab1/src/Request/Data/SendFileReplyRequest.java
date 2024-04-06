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
        System.out.println(getFirstAndLast20Byte(fileContent));
    }
    
    private String getFirstAndLast20Byte(String content) {
        if (content.length() > 40) {
            String first20Byte = content.substring(0, 20);
            String last20Byte = content.substring(content.length() - 20, content.length());
            return first20Byte + "...." + last20Byte;
        } else {
            return content;
        }
    }
}
