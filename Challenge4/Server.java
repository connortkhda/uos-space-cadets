// [1] https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html 
// [2] https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<RunServer> activeServers = new ArrayList<RunServer>(); 
    // [2] used this as it seemed like an effective way to distribute a message to all users connected to the server socket
    // could have used a hashmap with a client's username and their server thread so that it would be easier to use that information
    // serverside 

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(5555);
            System.out.println("Server initialised on port: 5555");
            while (true) {
                clientSocket = serverSocket.accept();
                RunServer activeServer = new RunServer(clientSocket, activeServers);
                // as RunServer extends thread, RunServer creates a new thread that can run in the background separate of the main class
                // chose for RunServer to extend thread rather than implement Runnable as it does not need to extend any other class
                activeServers.add(activeServer);
                activeServer.start();
                // runs code in run() block of target class
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class RunServer extends Thread {
        private BufferedReader clientInput;
        private PrintWriter serverOutput;
        private Socket clientSocket;
        private ArrayList<RunServer> activeServers;

        public RunServer(Socket clientSocket, ArrayList<RunServer> activeServers) {
            this.clientSocket = clientSocket;
            this.activeServers = activeServers;
            System.out.println("Connection established with a client");
        }

        public void run() {
            // code here is run when thread is started
            try {
                clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // allows server to get input from a client socket
                serverOutput = new PrintWriter(clientSocket.getOutputStream(),true);
                // allows server to output data to a client socket
                // true needed so that it automatically flushes :)

                while (true) {
                    String message = clientInput.readLine();
                    // constantly listens for data from the client
                    System.out.println("Server recieved> " + message);

                    if (message.equals("EXIT")) {
                        activeServers.remove(this);
                        break;
                        // if exit command is received, this RunServer object is removed from the active servers and this thread is terminated
                        // as it progresses to the end of run()
                    } else {
                        for (RunServer activeServer : activeServers) {
                            // if message is not exit command, send it on to all clients other than the client of this thread (the one who sent it)
                            if (activeServer != this) {
                                activeServer.serverOutput.println(message);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}