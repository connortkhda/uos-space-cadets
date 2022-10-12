import java.util.Scanner;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Challenge1 {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        String id;
        while (true) {
            System.out.println("Input id of name to fetch. (Blank input to close): ");
            id = userInput.nextLine();

            if (!id.isEmpty()) {
                try {
                    URL url = new URL("https://www.ecs.soton.ac.uk/people/"+id); //https://www.southampton.ac.uk/people/+id does not work
                    BufferedReader HTMLLine = new BufferedReader(new InputStreamReader(url.openStream())); //thank you https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html 
                    String outputString;
                    Boolean found = false;
                    
                    while((outputString = HTMLLine.readLine()) != null) {
                        if (outputString.contains("og:title")) { //"og:title" appears only on the line where the user's name is
                            System.out.println(outputString.substring(35, outputString.length()-4));
                            //list of people ECS searchable: https://www.ecs.soton.ac.uk/people/id (without anything after people/ it defaults to https://www.southampton.ac.uk/people/)
                            //found that sometimes people's IDs are not their email starters, but instead in the redirect URL to https://www.southampton.ac.uk/people/(theirID)
                            found = true;
                            HTMLLine.close();
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("User was not found/does not exist (id separate from email?)");
                        HTMLLine.close();
                    }
                } catch (Exception e) {
                    System.out.println(e); //mostly thrown if any of the readers are not closed, otherwise sometimes network related.
                }
            } else {
                break;
            }
        }
        userInput.close();
    }
}