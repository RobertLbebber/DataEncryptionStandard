package crypto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by trez_ on 3/18/2018.
 */
public class FileWorker {

    private static final Logger LOGGER = Logger.getLogger(FileWorker.class.getName());

    static void writeFile(String filePath) {

        LOGGER.log(Level.INFO, filePath);
    }


    static long[] readFile(String filePath) {
        ArrayList<Long> lines = new ArrayList<>();
        try (Scanner s = new Scanner(new FileReader(filePath))) {
            lines.add(Long.parseLong(s.next()));
        } catch (FileNotFoundException e) {

        }
        LOGGER.log(Level.INFO, "Reading to: " + filePath);
        return lines.stream().mapToLong(Long::longValue).toArray();
    }
}
