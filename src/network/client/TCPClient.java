package network.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class TCPClient {

    private final String HOSTIP;
    private final int PORT;
    private final int TIMEOUT;
    
    public TCPClient(String hostIp, int portNum, int timeOut){
        HOSTIP = hostIp;
        PORT = portNum;
        TIMEOUT = timeOut;
    }

    public void sendObjectToServer(Object outObj, String outType) {

        Socket s = new Socket();
        
        try {
            s.connect(new InetSocketAddress(HOSTIP, PORT), TIMEOUT);

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(outObj);
            out.writeObject(outType);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            String message = (String) in.readObject();
            System.out.println(message);

            s.close();
        }
        catch (Exception e) {
            System.out.println("Something went wrong trying to send the message...");
            e.printStackTrace();
        }
    }
}
