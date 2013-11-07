package network.client;

import java.io.File;
import java.util.ArrayList;


public class ClientExample {

    private static class clientThread implements Runnable {

        @Override
        public void run () {

            //TCPClient exampleClient = new TCPClient("10.181.16.255", 4768, 2000);
            TCPClient exampleClient = new TCPClient("10.190.254.44", 4768, 2000);

            ArrayList<Integer> outObj = new ArrayList<Integer>();
            String outType = "java.util.ArrayList";
            outObj.add(0);
            outObj.add(2);
            outObj.add(7);
            outObj.add(10);
            outObj.add(99);

            exampleClient.sendObjectToServer(outType, outObj);

            exampleClient.sendFileToServer(System.getProperty("user.dir") + File.separator
                                           + "src" + File.separator
                                           + "network" + File.separator
                                           + "client" + File.separator
                                           + "testSendFile.txt");
        }

    }

    public static void main (String[] args) {

        new Thread(new clientThread()).start();

    }
}
