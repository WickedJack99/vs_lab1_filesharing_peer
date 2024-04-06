package vslab1.src.Request.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
            try {
                //Write content to String and send data via sendingqueue to requesting peer
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                String fileContent = stringBuilder.toString();
                sendingQueue.add(new SendFileReply(thisPeer, sender, fileContent, fileName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
