package NewBaby;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by trez_ on 3/18/2018.
 */
public class FileWorker {

    public static void writeFile(char[][] message, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < message.length; i++) {
                for (int j = 0; j < message[i].length; j++) {
                    writer.write(message[i][j]+48);
                }
                writer.write("\n");
            }
        } catch (IOException e) {
        }
    }

    public static char[][] readFile(String filePath) {
        ArrayList<char[]> message = new ArrayList<>();
        try (Scanner s = new Scanner(new FileReader(filePath))) {
            for (int c = 0; s.hasNext(); c++) {
                String number = s.next();
                char[] temp=new char[number.length()];
                for (int j = 0; j < number.length(); j++) {
                    temp[j] = (char) (number.charAt(j) - 48);
                }
                message.add(temp);
            }
        } catch (FileNotFoundException e) {
        }
        return message.toArray(new char[message.size()][]);
    }

    public static char[][][] getSboxes(String sbox1Path, String sbox2Path) {
        char[][][] sboxes = new char[2][16][3];
        try {
            Scanner s = new Scanner(new FileReader(sbox1Path));
            for (int i = 0; s.hasNext(); i++) {
                String number = s.next();
                for (int j = 0; j < number.length(); j++) {
                    sboxes[0][i][j] = (char) (number.charAt(j) - 48);
                }
            }
            s = new Scanner(new FileReader(sbox2Path));
            for (int i = 0; s.hasNext(); i++) {
                String number = s.next();
                for (int j = 0; j < number.length(); j++) {
                    sboxes[1][i][j] = (char) (number.charAt(j) - 48);
                }
            }
        } catch (FileNotFoundException e) {
        }
        return sboxes;
    }
}
