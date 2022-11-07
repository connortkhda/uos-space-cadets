import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ArrayList<RunServer> activeServers = new ArrayList<RunServer>();

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(5000);
            while (true) {
                clientSocket = serverSocket.accept();
                RunServer activeServer = new RunServer(clientSocket, activeServers);
                activeServers.add(activeServer);
                activeServer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class RunServer extends Thread {
        BufferedReader clientInput;
        PrintWriter serverOutput;
        Socket clientSocket;
        ArrayList<RunServer> activeServers;

        public RunServer(Socket clientSocket, ArrayList<RunServer> activeServers) {
            this.clientSocket = clientSocket;
            this.activeServers = activeServers;
        }

        public void run() {
            try {
                clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                serverOutput = new PrintWriter(clientSocket.getOutputStream(),true);

                while (true) {
                    String message = clientInput.readLine();
                    if (message.equals("EXIT")) {
                        break;
                    } else {
                        System.out.println("SERVER: " + message);
                        for (RunServer server : activeServers) {
                            server.serverOutput.println(message);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}