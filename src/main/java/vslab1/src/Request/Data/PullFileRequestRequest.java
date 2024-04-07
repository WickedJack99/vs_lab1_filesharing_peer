package vslab1.src.Request.Data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.FileReaderWriter.FileReaderWriter.EUpdateFlag;
import vslab1.src.Peers.Peer;
import vslab1.src.Sending.SendingQueue;
import vslab1.src.Sending.Data.EDataType;
import vslab1.src.Sending.Data.SendFileReply;

public record PullFileRequestRequest(Peer sender, Peer receiver, String fileName) implements Requestable {

    @Override
    public EDataType getType() {
        return EDataType.PullFileRequest;
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
        Peer thisPeer = FileReaderWriter.getThisPeer(EUpdateFlag.Update);

        // fileName is key to filePath
        String filePath = thisPeer.filesMap().get(fileName);
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.err.println("File path doesn't exist.");
        } else {
            //Write content to String and send data via sendingqueue to requesting peer
            String fileContent = FileReaderWriter.readFile(filePath);
            sendingQueue.add(new SendFileReply(thisPeer, sender, fileContent, fileName));
        }
    }
}
