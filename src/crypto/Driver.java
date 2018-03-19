package crypto;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver {

    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());
    static final Level ENV_LOGGER_LEVEL = Level.OFF;

    static {
        LOGGER.setLevel(ENV_LOGGER_LEVEL);
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%4$s %2$s]\t %5$s%6$s%n");
        LOGGER.log(Level.INFO, "args[]= " + Arrays.toString(args));

        final int keyLength = args.length > 0 ? Integer.parseInt(args[0]) : 9;
        final int msgLength = args.length > 1 ? Integer.parseInt(args[1]) : 12;
        final int ITERATIONS = args.length > 2 ? Integer.parseInt(args[2]) : 4;
        final String PUT_FILE_DIR = args.length > 3 ? args[3] : "./src/resources/";
        final String IN_FILE = args.length > 4 ? args[4] : "input.txt";
        final String OUT1_FILE = args.length > 5 ? args[5] : "output.1.txt";
        final String OUT2_FILE = args.length > 6 ? args[6] : "output.2.txt";
        final String SBOX1_PATH = args.length > 7 ? args[7] : PUT_FILE_DIR + "s_box.1.txt";
        final String SBOX2_PATH = args.length > 8 ? args[8] : PUT_FILE_DIR + "s_box.2.txt";

        long[] inputValues = FileWorker.readFile(PUT_FILE_DIR + IN_FILE);

        BabyDES babyDES = new BabyDES(keyLength, msgLength, SBOX1_PATH, SBOX2_PATH, ITERATIONS);
        String encryption = babyDES.encode(inputValues);
        FileWorker.writeFile(encryption, msgLength, PUT_FILE_DIR + OUT1_FILE);

        long privateKey = babyDES.getPrivateKey();
        long[] inputValues2 = FileWorker.readFile(PUT_FILE_DIR + OUT1_FILE);
        String decryption = babyDES.decode(privateKey, inputValues2);
        FileWorker.writeFile(decryption, msgLength, PUT_FILE_DIR + OUT2_FILE);
    }
}
