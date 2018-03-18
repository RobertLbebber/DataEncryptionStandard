package crypto;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver {

    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());
    static final Level ENV_LOGGER_LEVEL = Level.ALL;

    static {
        LOGGER.setLevel(ENV_LOGGER_LEVEL);
    }

    private static final String PUT_FILE_DIR = "./src/resources/";
    private static final String IN_FILE = "input.txt";
    private static final String OUT_FILE = "output.txt";
    private static final String SOBX_PATH = PUT_FILE_DIR + "s_box.txt";
    private static final int ITERATIONS = 4;

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT %4$s %2$s]\t %5$s%6$s%n");
        LOGGER.log(Level.INFO, "args[]= " + Arrays.toString(args));
        int keyLength = args.length == 2 ? Integer.parseInt(args[0]) : 9;
        int msgLength = args.length == 2 ? Integer.parseInt(args[1]) : 12;

        long[] inputValues = FileWorker.readFile(PUT_FILE_DIR + IN_FILE);

        BabyDES babyDES = new BabyDES(keyLength, msgLength, SOBX_PATH, ITERATIONS);
        String encryption = babyDES.encode(inputValues);
        FileWorker.writeFile(encryption, msgLength, PUT_FILE_DIR + OUT_FILE);
        babyDES.decode(inputValues);
    }
}
