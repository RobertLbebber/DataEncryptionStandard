package NewBaby;

import java.util.Scanner;

/**
 * Created by trez_ on 3/29/2018.
 */
public class Driver {
    public static void main(String[] args) {
        int iterations = 4;
        int keyLength = 9;
        int msgLength = 12;
        String resources = "./src/resources/";
        String PUT_FILE_DIR = "./src/output/";
        String sbox1 = resources + "s_box.1.txt";
        String sbox2 = resources + "s_box.2.txt";
        char[][][] sboxes = FileWorker.getSboxes(sbox1, sbox2);

        Scanner s = new Scanner(System.in);
        System.out.print("Input File Name: ");
        String inputFile = s.next();
        System.out.print("Input Key: ");
        String temp = s.next();
        char[] key = new char[9];
        for (int i = 0; i < temp.length(); i++) {
            key[i] = temp.charAt(i);
        }
        System.out.print("Encrypting? (true/false): ");
        boolean isEncryption = s.nextBoolean();
        System.out.print("Input Encryption Mode(ecb/cbc): ");
        boolean isECB = s.next().equals("ecb");
        s.close();

        char[][] inputValues = FileWorker.readFile(PUT_FILE_DIR + inputFile);

        BabyDES babyDES = new BabyDES(keyLength, msgLength, key, sboxes[0], sboxes[1], iterations);
        String outputFile;
        if (isECB) {
            if (isEncryption) {
                char[][] encryption = babyDES.encode(inputValues, false);
                outputFile = inputFile.replace(".", "_enc.");
                FileWorker.writeFile(encryption, PUT_FILE_DIR + outputFile);
            } else {
                char[][] inputValues2 = FileWorker.readFile(PUT_FILE_DIR + inputFile);
                char[][] decryption = babyDES.decode(key, inputValues2, false);
                outputFile = inputFile.replace(".", "_dec.");
                FileWorker.writeFile(decryption, PUT_FILE_DIR + outputFile);
            }
        } else {
            if (isEncryption) {
                char[][] encryption = babyDES.encode(inputValues, true);
                outputFile = inputFile.replace(".", "_enc.");
                FileWorker.writeFile(encryption, PUT_FILE_DIR + outputFile);
            } else {
                char[][] inputValues2 = FileWorker.readFile(PUT_FILE_DIR + inputFile);
                char[][] decryption = babyDES.decode(key, inputValues2, true);
                outputFile = inputFile.replace(".", "_dec.");
                FileWorker.writeFile(decryption, PUT_FILE_DIR + outputFile);
            }
        }


    }

    public static void printChars(char[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print((char) (array[i] + 48));
        }
        System.out.println();
    }

    public static char[] cloneArray(char[] protect) {
        char[] nevv = new char[protect.length];
        for (int i = 0; i < protect.length; i++) {
            nevv[i] = protect[i];
        }
        return nevv;
    }

}
