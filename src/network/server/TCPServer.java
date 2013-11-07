package network.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class TCPServer {

    private static final int PORT = 4768;
    private static final String DEFAULT_RECEIVED_FILE = System.getProperty("user.dir") +
                                                        File.separator
                                                        + "src" + File.separator + "network" +
                                                        File.separator + "server" + File.separator +
                                                        "ReceivedFile.txt";
    private Object receivedObj = null;
    private String receivedFile = null;

    @SuppressWarnings("resource")
    public void runServer () {
        try {

            ServerSocket serverS = new ServerSocket(PORT, 10);

            while (true) {

                Socket clientSocket = serverS.accept();

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String inType = (String) in.readObject();
                Object inObj = in.readObject();
                dealWithObjectReceived(inType, inObj);

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject("Hi Client, this is server. Your information has been received");
                out.flush();
                out.close();

                clientSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private void dealWithObjectReceived (String inType, Object inObj) {
        if (inType.equals("textfile")) {
            writeReceivedFile(inObj);
        }
        else {
            Class c = null;
            try {
                c = Class.forName(inType);
            }
            catch (ClassNotFoundException e) {
                System.out.println("Client's object type is not found...");
                return;
            }
            receivedObj = c.cast(inObj);
            System.out.println("I received object \"" + c.cast(inObj) + "\" from the client!");

            // do whatever you want to do with the objects here
        }
    }

    @SuppressWarnings("unchecked")
    private void writeReceivedFile (Object inObj) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(DEFAULT_RECEIVED_FILE));

            List<String> fileLines = (List<String>) inObj;
            for (String s : fileLines) {
                out.write(s + "\n");
            }

            out.close();
            receivedFile = DEFAULT_RECEIVED_FILE;
        }
        catch (Exception e) {
            System.out.println("Error reading client's file input or writing it to a file...");
            return;
        }

        System.out.println("I received file \"" + DEFAULT_RECEIVED_FILE + "\" from the client!");
    }

    public Object getMostRecentObject () {
        return receivedObj;
    }

    public String getMostRecentFileName () {
        return receivedFile;
    }
}
