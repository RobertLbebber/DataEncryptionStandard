package crypto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import static crypto.Driver.ENV_LOGGER_LEVEL;

/**
 * Created by trez_ on 3/18/2018.
 */
public class FileWorker {

    private static final Logger LOGGER = Logger.getLogger(FileWorker.class.getName());

    static {
        LOGGER.setLevel(ENV_LOGGER_LEVEL);
    }

    public static void writeFile(String filePath, int msgLength, String toWrite) {
        try (Scanner s = new Scanner(filePath); FileWriter writer = new FileWriter(toWrite)) {
            while (s.hasNext()) {
                writer.write(s.next() + "\n");
            }
        } catch (IOException e) {
            LOGGER.warning("File Not Found: " + e.getMessage());
        }
    }

    public static long[] readFile(String filePath) {

        List<Long> lines = new ArrayList<>();
        try (Scanner s = new Scanner(new FileReader(filePath))) {
            while (s.hasNext()) {
                long temp = s.nextLong(2);
                lines.add(temp);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warning("File Not Found: " + e.getMessage());
        }
        long[] temp = new long[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            temp[i] = lines.get(i);
        }
        return temp;
    }

    public static char[][][] getSboxes(String sbox1Path, String sbox2Path) {
        char[][][] sboxes = new char[2][16][3];
        try {
            Scanner s = new Scanner(new FileReader(sbox1Path));
            for (int i = 0; s.hasNext(); i++) {
                String number = s.next();
                for (int j = 0; j < number.length(); j++) {
                    sboxes[0][i][j] = number.charAt(j);
                }
            }
            s = new Scanner(new FileReader(sbox2Path));
            for (int i = 0; s.hasNext(); i++) {
                String number = s.next();
                for (int j = 0; j < number.length(); j++) {
                    sboxes[1][i][j] = number.charAt(j);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.warning("File Not Found: " + e.getMessage());
        }
        return sboxes;
    }
}
