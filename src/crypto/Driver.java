package crypto;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver {

    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    private static final String PUT_FILE_DIR = "src/resource/";
    private static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT %4$s %2$s] %5$s%6$s%n");
        LOGGER.log(Level.INFO, "args[]= " + Arrays.toString(args));
        int keyLength = args.length == 2 ? Integer.parseInt(args[0]) : 9;
        int msgLength = args.length == 2 ? Integer.parseInt(args[1]) : 12;

        long[] inputValues = FileWorker.readFile(PUT_FILE_DIR + FILE_NAME);
        BabyDES method = new BabyDES(keyLength, msgLength);
        method.encode(inputValues);

    }
}
