package network.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServer {

    private static final int PORT = 4768;
    private Object receivedObj = null;

    @SuppressWarnings("resource")
    public void runServer () {
        try {

            ServerSocket serverS = new ServerSocket(PORT, 10);

            while (true) {

                Socket clientSocket = serverS.accept();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                Object inObj = in.readObject();
                String inType = (String) in.readObject();
                dealWithObjectReceived(inObj, inType);

                // short reply message to the client
                String outStr = "Hi Client, this is server. Your information has been received";
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(outStr);
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
    private void dealWithObjectReceived (Object inObj, String inType) {
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
    
    public Object getMostRecentObject(){
        return receivedObj;
    }
}
