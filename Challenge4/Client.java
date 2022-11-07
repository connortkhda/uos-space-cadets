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
        System.out.println("Please enter a username before you enter the chat: ");
        Client client = new Client();
        client.username = input.nextLine();
        client.startClient();
    }

    public void startClient() {
        try {
            Socket clientSocket = new Socket("localhost", 5000);
            PrintWriter clientOutput = new PrintWriter(clientSocket.getOutputStream(),true);

            String clientMessage = "";
            RunClient activeClient = new RunClient(clientSocket);
            activeClient.start();

            while (!clientMessage.equals("EXIT")) {
                clientMessage = input.nextLine();
                if (clientMessage.equals("EXIT")) {
                    clientOutput.println("EXIT");
                    break;
                } else {
                    clientOutput.println(username + ":" + clientMessage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RunClient extends Thread {
        private BufferedReader clientInput;
        public RunClient(Socket clientSocket) throws IOException {
            this.clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
        }

        public void run() {
            try {
                while (true) {
                    System.out.println(clientInput.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                clientInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }


    }
}