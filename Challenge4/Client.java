// [1] https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html 
// [2] https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Client {
    private static Scanner input = new Scanner(System.in);
    private String username;

    public static void main(String[] args) {
        Client client = new Client();
        System.out.println("Please enter a username before you enter the chat: ");
        client.setUsername(input.nextLine());
        client.startClient();
        System.exit(0);
        // if EXIT command is receieved, program will resume line 20 and System.exit(0) will be called for clean exit
    }

    public void startClient() {
        // connects client to server and then deals with sending messages to the server
        try {
            Socket clientSocket = new Socket("localhost", 5555);
            // connects client to server
            PrintWriter clientOutput = new PrintWriter(clientSocket.getOutputStream(),true);
            // allows client to output data to server 
            String clientMessage = "";
            RunClient activeClient = new RunClient(clientSocket);
            // thread needed so that a client is able to receieve messages from the server whilst also typing/sending their own messages.
            // as RunClient extends thread, RunClient creates a new thread that can run in the background separate of the main class
            // chose for RunClient to extend thread rather than implement Runnable as it does not need to extend any other class
            activeClient.start();
            // runs code in run() block of target class

            while (true) {
                clientMessage = input.nextLine();
                if (clientMessage.equals("EXIT")) {
                    // if client sends exit command, send "EXIT" to server, notifies it to remove the relevant server from the activeserver list
                    clientOutput.println("EXIT");
                    break;
                } else {
                    clientOutput.println(username + ": " + clientMessage);
                    // else send the message in the format "username: message", this will be sent to the server to send onto all other clients
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        // allows a user to set the username variable of a client class
        this.username = username;
    }

    public class RunClient extends Thread {
        private BufferedReader clientInput;
        public RunClient(Socket clientSocket) throws IOException {
            this.clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            // allows client to receieve data from the server
        }

        public void run() {
            try {
                while (true) {
                    System.out.println(clientInput.readLine());
                    // waits for server to send data, then prints it to console
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }


    }
}