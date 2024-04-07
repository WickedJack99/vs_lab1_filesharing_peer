/**
 * @author Aaron Moser
 */
package vslab1.src.Request.Data;

import vslab1.src.FileReaderWriter.FileReaderWriter;
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
        System.out.println(FileReaderWriter.getFirstAndLast20Byte(fileContent));
    }
}
