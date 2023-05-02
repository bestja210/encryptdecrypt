package encryptdecrypt;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

// Abstract class ensures that all classes which extend CypherMethods contain a function to perform encoding or decoding
abstract class CypherMethods {
    public abstract void algorithm(String alg, String data, String outFile, int key);
}
class Encoding extends CypherMethods {
    // function returns input as an array of characters.
    public char [] toArray(String data) {
        return data.toCharArray();
    }
    // function returns an empty array of length arr.length
    public char [] productArray(String data) {
        char [] arr = toArray(data);
        return new char[arr.length];
    }
    // Overrides the abstract base class' method to encoded a messages.
    @Override
    public void algorithm(String alg, String data, String outFile, int key) {
        char [] messageArray = toArray(data);
        char [] encArray = productArray(data);
        int myShift = key % 26 + 26;
        if (alg.equals("unicode")) {
            for (int i = 0; i <= messageArray.length - 1; ++i) {
                encArray[i] = (char) ((int) messageArray[i] + key);
            }
        } else {
            for (int i = 0; i <= messageArray.length - 1; ++i) {
                if (Character.isLetter(messageArray[i])) {
                    if (Character.isUpperCase(messageArray[i])) {
                        encArray[i] = (char) ('A' + (messageArray[i] - 'A' + myShift) % 26);
                    } else {
                        encArray[i] = (char) ('a' + (messageArray[i] - 'a' + myShift) % 26);
                    }
                } else {
                    encArray[i] = messageArray[i];
                }
            }
        }
        Main.outputFile(outFile, encArray);
    }
}
class Decoding extends CypherMethods {
    public char [] toArray(String data) {
        return data.toCharArray();
    }
    public char [] productArray(String data) {
        char [] arr = toArray(data);
        return new char[arr.length];
    }
    @Override
    public void algorithm(String alg, String data, String outFile, int key) {
        char [] decArray = toArray(data);
        char [] messageArray = productArray(data);
        int myShift = 26 - (key % 26);
        if (alg.equals("unicode")) {
            for (int i = 0; i <= decArray.length - 1; ++i) {
                messageArray[i] = (char) ((int) decArray[i] - key);
            }
        } else {
            for (int i = 0; i <= decArray.length - 1; ++i) {
                if (Character.isLetter(decArray[i])) {
                    if (Character.isUpperCase(decArray[i])) {
                        messageArray[i] = (char) ('A' + (((decArray[i] - 'A') + myShift) % 26));
                    } else {
                        messageArray[i] = (char) ('a' + (((decArray[i] - 'a') + myShift) % 26));
                    }
                } else {
                    messageArray[i] = decArray[i];
                }
            }
        }
        Main.outputFile(outFile, messageArray);
    }
}
class AlgorithmApplicator {
    private CypherMethods method;
    public void setMethod(CypherMethods method) {
        this.method = method;
    }
    public void algorithm(String alg, String data, String outFile, int key) {
        this.method.algorithm(alg, data, outFile, key);
    }
}
public class Main {
    public static String readFileInput(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void outputFile(String outputFile, char[] messageArray) {
        if (!(outputFile.equals(""))) {
            File file = new File(outputFile);
            try(FileWriter writer = new FileWriter(file)) {
                writer.write(new String(messageArray));
            } catch (IOException e) {
                System.out.printf("Error: %s", e.getMessage());
            }
        } else {
            System.out.printf("%s", new String(messageArray));
        }
    }

    public static void main(String[] args) {
        String alg = "shift";
        String mode = "enc";
        String data = "";
        String inFile = "";
        String outFile = "";
        int key = 0;

        for (int i = 0; i <= args.length - 1; i += 2) {
            switch (args[i]) {
                case "-alg" -> {
                    if (args[i + 1].equals("unicode")) {
                        alg = args[i+1];
                    }
                }
                case "-mode" -> {
                    if ((args[i + 1].equals("enc")) || (args[i + 1].equals("dec"))) {
                        mode = args[i + 1];
                    }
                }
                case "-key" -> key = Integer.parseInt(args[i + 1]);
                case "-data" -> data = args[i + 1];
                case "-in" -> {
                    try {
                        inFile = readFileInput(args[i + 1]);
                    } catch (IOException e) {
                        System.out.printf("Error: %s", e.getMessage());
                    }
                }
                case "-out" -> outFile = args[i + 1];
                default -> {
                }
            }
        }

        String eodData = data.equals("") ? inFile : data;

        AlgorithmApplicator applicator = new AlgorithmApplicator();
        switch (mode) {
            case "enc" -> {
                applicator.setMethod(new Encoding());
                applicator.algorithm(alg, eodData, outFile, key);
            }
            case "dec" -> {
                applicator.setMethod(new Decoding());
                applicator.algorithm(alg, eodData, outFile, key);
            }
            default -> System.out.println("You entered an invalid response.");
        }
    }
}
