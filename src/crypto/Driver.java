package crypto;

        import java.util.Arrays;
        import java.util.Scanner;
        import java.util.logging.Level;
        import java.util.logging.Logger;

public class Driver {

    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());
    static final Level ENV_LOGGER_LEVEL = Level.ALL;

    static {
        LOGGER.setLevel(ENV_LOGGER_LEVEL);
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%4$s %2$s]\t %5$s%6$s%n");
        LOGGER.log(Level.INFO, "args[]= " + Arrays.toString(args));

        final int keyLength;
        final int msgLength = 12;
        final int ITERATIONS = 4;
        final String PUT_FILE_DIR = "./src/resources/";
        final String inputFile;
        final String outputFile;
        long privateKey;
        boolean isEncrypt;
        String encryptionMode;
        try (Scanner s = new Scanner(System.in)) {
            System.out.print("Input File Name : ");
            inputFile = s.next();
            System.out.print("Key : ");
            String temp = s.next();
            keyLength = temp.length();
            privateKey = Long.parseLong(temp, 2);
            System.out.print("Encrypt Type? (ecb/cbc) : ");
            encryptionMode=s.next();
            System.out.print("Encrypting? (true/false) : ");
            isEncrypt=s.nextBoolean();
        }
        final String SBOX1_PATH = PUT_FILE_DIR + "s_box.1.txt";
        final String SBOX2_PATH = PUT_FILE_DIR + "s_box.2.txt";

        long[] inputValues = FileWorker.readFile(PUT_FILE_DIR + inputFile);
        BabyDES babyDES = new BabyDES(privateKey, keyLength, msgLength, SBOX1_PATH, SBOX2_PATH, ITERATIONS);
        if(encryptionMode.equals("cbc")) {
            if (isEncrypt) {
                outputFile=inputFile.replace(".","_enc.");
                String encryption = babyDES.encode(inputValues, false);
                FileWorker.writeFile(encryption, msgLength, PUT_FILE_DIR + outputFile);
                System.out.println("Private Key: "+Long.toBinaryString(babyDES.getPrivateKey()));
            } else {
                outputFile=inputFile.replace(".","_dec.");
                long[] inputValues2 = FileWorker.readFile(PUT_FILE_DIR + inputFile);
                String decryption = babyDES.decode(babyDES.getPrivateKey(), inputValues2, false);
                FileWorker.writeFile(decryption, msgLength, PUT_FILE_DIR + outputFile);
            }
        }else if(encryptionMode.equals("ecb")){
            if (isEncrypt) {
                outputFile=inputFile.replace(".","_enc.");
                String encryption = babyDES.encode(inputValues, true);
                FileWorker.writeFile(encryption, msgLength, PUT_FILE_DIR + outputFile);
                System.out.println("Private Key: "+Long.toBinaryString(babyDES.getPrivateKey()));
            } else {
                outputFile=inputFile.replace(".","_dec.");
                long[] inputValues2 = FileWorker.readFile(PUT_FILE_DIR + inputFile);
                String decryption = babyDES.decode(babyDES.getPrivateKey(), inputValues2, true);
                FileWorker.writeFile(decryption, msgLength, PUT_FILE_DIR + outputFile);
            }
        }else{
            System.err.println("");
        }
    }
}