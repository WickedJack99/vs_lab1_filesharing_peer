package java.src.Request.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.src.Peers.Peer;
import java.src.Sending.SendingQueue;
import java.src.Sending.Data.EDataType;

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
        // TODO read filelist with paths and read file at path.
        // Write content to String and send data via sendingqueue to requesting peer
        // StringBuilder stringBuilder = new StringBuilder();
        // try (BufferedReader reader = new BufferedReader(new FileReader())) {
        //     String line;
        //     while ((line = reader.readLine()) != null) {
        //         stringBuilder.append(line).append("\n");
        //     }
        // }
        // return stringBuilder.toString();
    }
    
}
