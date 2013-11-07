package network.server;

public class ServerExample {

    private static class serverThread implements Runnable {

        private TCPServer exampleServer;

        public serverThread () {
            exampleServer = new TCPServer();
        }

        @Override
        public void run () {
            exampleServer.runServer();
        }

        public TCPServer getServer () {
            return exampleServer;
        }

    }

    public static void main (String[] args) {

        serverThread exampleServer = new serverThread();
        new Thread(exampleServer).start();

        while (true) {
            System.out.println("Most recently received object is " +
                               exampleServer.getServer().getMostRecentObject());
            System.out.println("Most recently received file is " +
                               exampleServer.getServer().getMostRecentFileName());
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
