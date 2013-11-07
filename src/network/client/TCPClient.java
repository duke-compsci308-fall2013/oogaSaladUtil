package network.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TCPClient {

    private final String HOSTIP;
    private final int PORT;
    private final int TIMEOUT;
    private Socket s;

    public TCPClient (String hostIp, int portNum, int timeOut) {
        HOSTIP = hostIp;
        PORT = portNum;
        TIMEOUT = timeOut;
    }

    public void sendObjectToServer (String outType, Object outObj) {
        
        try {
            createSocketAndSend(outType, outObj);
        }
        catch (Exception e) {
            System.out.println("Something went wrong trying to send the object...");
            e.printStackTrace();
        }
        
    }
    
    public void sendFileToServer (String filePath) {

        try {
            Scanner fin = new Scanner(new FileInputStream(filePath));
            List<String> fileText = new ArrayList<String>();
            while (fin.hasNext()) {
                fileText.add(fin.nextLine());
            }
            fin.close();
            
            createSocketAndSend("textfile", fileText);
        }
        catch (Exception e) {
            System.out.println("Something went wrong trying to send the file...");
            e.printStackTrace();
        }

    }

    private void createSocketAndSend (String outType, Object outObj) throws IOException,
                                                                    ClassNotFoundException {

        s = new Socket();
        s.connect(new InetSocketAddress(HOSTIP, PORT), TIMEOUT);

        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        out.writeObject(outType);
        out.writeObject(outObj);
        out.flush();

        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        String message = (String) in.readObject();
        System.out.println(message);

        s.close();
    }
}
