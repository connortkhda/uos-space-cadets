import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Challenge3 {
    public static void main(String[] args) {  
        HashMap<String, Integer> variables = new HashMap<>();
        //run(readFile(args[0]));
        //works if launched from console
        run(readFile("D:/Space Cadets/Challenge2/test2.txt"), variables);
        //readFile(filePath) to read from text file

        //prints final set of variables (for logging)
        for (String variable : variables.keySet()) {
            System.out.println(variable + ": " + variables.get(variable));
        }
        System.exit(0);
    }

    //reads all text from file, cutting out any indentation and ;'s. extended version could have error checking that ensures the ;
    //delimiter is present on every line, else errors
    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> program = new ArrayList<String>();
        try {
            File codeFile = new File(fileName);
            Scanner fileReader = new Scanner(codeFile);
            while (fileReader.hasNextLine()) {
                program.add(fileReader.nextLine().trim().replaceAll(";", ""));
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error");
            e.printStackTrace();
        }
        return program;
    }

    //executes commands and stores variable values in hashmap "variables"
    public static void run(ArrayList<String> codeBlock, HashMap<String, Integer> variables) {
        int index = 0;
        String[] lineArray;

        for (String line : codeBlock) {
            if (index > codeBlock.size() - 1) {
                break;
            }

            //prints out variable values for each loop of run for a code block (for logging)
            for (String key : variables.keySet()) {
                System.out.println(key + ": " + variables.get(key));
            }

            lineArray = codeBlock.get(index).split(" ");

            switch (lineArray[0]) {
                case "incr":
                    incr(lineArray[1], variables);
                    break;
                case "decr":
                    decr(lineArray[1], variables);
                    break;
                case "clear":
                    clear(lineArray[1], variables);
                    break;
                case "while":
                    if (!variables.containsKey(lineArray[1])) {
                        clear(lineArray[1], variables);
                    }
                    index = whileLoop(lineArray[1], Integer.parseInt(lineArray[3]), index, codeBlock, variables);
                    break;
                default:
                    break;
            }
            index++;
        }
    }

    //ASSUMING ALL VARIABLES START WITH A VALUE OF 0
    public static void incr(String variable, HashMap<String, Integer> variables) {
        if (variables.containsKey(variable)) {
            variables.put(variable, variables.get(variable).intValue() + 1);
        } else {
            variables.put(variable, 1);
        }
    }

    public static void decr(String variable, HashMap<String, Integer> variables) {
        if (variables.containsKey(variable) && variables.get(variable) != 0) {
            variables.put(variable, variables.get(variable).intValue() - 1);
        } else {
            System.err.println("variable " + variable + " cannot be negative. exiting");
            System.exit(1);
        }
    }

    public static void clear(String variable, HashMap<String, Integer> variables) {
        variables.put(variable, 0);
    }

    //while var not x do;
    public static int whileLoop(String variable, int condition, int startIndex, ArrayList<String> codeBlock, HashMap<String, Integer> variables) {
        int nested = 0;
        int index = startIndex+1;
        String[] line;
        while (true) {
            if (index > codeBlock.size()) {
                System.err.println("end; expected. exiting");
                System.exit(1);
            }

            //ensures the correct while loop is looped through, for every "while", an "end" is ignored
            line = codeBlock.get(index).split(" ");
            if (line[0].equals("while")) {
                nested++;
            } else if (line[0].equals("end") && nested == 0) {
                break;
            } else if (line[0].equals("end")) {
                nested--;
            }
            index++;            
        }

        //creates the code block that is to be called to loop through, only the statments inside of the "while" and "end"
        ArrayList<String> recurse = new ArrayList<String>();
        for(int i = startIndex + 1; i < index; i++) {
            recurse.add(codeBlock.get(i));
        }

        //outputs a values loop to reach (for logging)
        System.out.println(variable + " must reach " + condition);

        //runs run until the codeblock satisfies the condition, runs until stack overflow error if not reached
        //could be handled in extension
        while (variables.get(variable).intValue() != condition) {
            run(recurse, variables);
        }

        return index;
    }

}
