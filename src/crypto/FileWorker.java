package crypto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
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

    static void writeFile(String filePath, int msgLength, String toWrite) {
        try (Scanner s = new Scanner(filePath); FileWriter writer = new FileWriter(toWrite)) {
            while (s.hasNext()) {
//                String result = Long.toBinaryString(s.nextLong());
                String result = String.format("%0" + msgLength + "d", s.nextLong());
                writer.write(result + "\n");
            }
        } catch (IOException e) {
            LOGGER.warning("File Not Found: " + e.getMessage());
        }
    }


    static long[] readFile(String filePath) {

        List<Long> lines = new ArrayList<>();
        try (Scanner s = new Scanner(new FileReader(filePath))) {
            while (s.hasNext()) {
                long temp = s.nextLong(2);
                LOGGER.log(Level.FINE, "Current Long: " + temp);
                lines.add(temp);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warning("File Not Found: " + e.getMessage());
        }
        LOGGER.log(Level.FINE, "Read from: " + filePath + " Elements= " + lines.size());
        long[] temp = new long[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            temp[i] = lines.get(i);
        }

        return temp;
    }


}
