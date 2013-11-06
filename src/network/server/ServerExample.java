package network.server;

import java.util.List;

public class ServerExample {

    private static class serverThread implements Runnable {
        
        private TCPServer exampleServer;
        
        public serverThread(){
            exampleServer = new TCPServer();
        }
        
        @Override
        public void run () {
            exampleServer.runServer();
        }
        
        public TCPServer getServer(){
            return exampleServer;
        }

    }

    @SuppressWarnings("unchecked")
    public static void main (String[] args) {
        
        List<Integer> myList = null;
        
        serverThread exampleServer = new serverThread();
        new Thread(exampleServer).start();
        
        while(true){
            myList = (List<Integer>) exampleServer.getServer().getMostRecentObject();
            System.out.println("My list contains " + myList);
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
