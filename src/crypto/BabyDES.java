package crypto;

import java.util.logging.Level;
import java.util.logging.Logger;

import static crypto.Driver.ENV_LOGGER_LEVEL;

/**
 * Created by trez_ on 3/18/2018.
 */
public class BabyDES {
    private static final Logger LOGGER = Logger.getLogger(BabyDES.class.getName());
    private final String SBOX_PATH;
    private final int ITERATIONS;

    static {
        LOGGER.setLevel(ENV_LOGGER_LEVEL);
    }

    private int keyLength;
    private int msgLength;

    public BabyDES(int keyLength, int msgLength, String sboxPath, int iter) {
        this.keyLength = keyLength;
        this.msgLength = msgLength;
        ITERATIONS = iter;
        SBOX_PATH = sboxPath;
        LOGGER.info("keyLength: " + keyLength + " msgLength: " + msgLength);
    }

    public String encode(long[] inputValues) {
        StringBuilder sb = new StringBuilder();
        long key = inputValues[0];

        for (int i = 1; i < inputValues.length; i++) {
            long message = inputValues[i];
            long lCurr = 0L;
            long rCurr = 0L;
            for (int j = 0; j < ITERATIONS; j++) {
                message = inputValues[i];
                lCurr = message >>> (msgLength / 2);
                rCurr = message & ((1 << (msgLength / 2)) - 1);
                LOGGER.log(Level.FINE, "key: " + key + " message: " + Long.toBinaryString(message) + " lCurr: " + Long.toBinaryString(lCurr) + " rCurr:" + Long.toBinaryString(rCurr));

                long rexCurr = expander(rCurr) ^ key;
                LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr) + " key: " + Long.toBinaryString(key));

                rexCurr = sbox(rexCurr) ^ lCurr;//reCurr is being used as a temp right here

                lCurr = rCurr;
                rCurr = rexCurr;
            }
            sb.append(Long.toBinaryString(lCurr) + "" + Long.toBinaryString(rCurr )+ " \n");
        }
        return sb.toString();
    }

    public String decode(long[] inputValues) {
        StringBuilder sb = new StringBuilder();
        long key = inputValues[0];
/*
        for (int i = 1; i < inputValues.length; i++) {
            long message = inputValues[i];
            long lCurr = 0L;
            long rCurr = 0L;
            for (int j = 0; j < ITERATIONS; j++) {
                message = inputValues[i];
                lCurr = message >>> (msgLength / 2);
                rCurr = message & ((1 << (msgLength / 2)) - 1);
                LOGGER.log(Level.FINE, "key: " + key + " message: " + Long.toBinaryString(message) + " lCurr: " + Long.toBinaryString(lCurr) + " rCurr:" + Long.toBinaryString(rCurr));

                long rexCurr = expander(rCurr) ^ key;
                LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr) + " key: " + Long.toBinaryString(key));

                rexCurr = sbox(rexCurr) ^ lCurr;//reCurr is being used as a temp right here

                lCurr = rCurr;
                rCurr = rexCurr;
            }
            sb.append(rCurr + " ");
        }
        */
        return sb.toString();
    }

    private long sbox(long rexCurr) {
        long[] svalues = FileWorker.readFile(SBOX_PATH);
        long toS1 = rexCurr >>> ((keyLength - 1) / 2);
        long toS2 = rexCurr & ((1 << ((keyLength - 1) / 2)) - 1);
        if (rexCurr > Integer.MAX_VALUE) {
            LOGGER.severe("Value of Expanded Submessage is Too Large For Java Primitive Arrays");
        }
        LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr) + " toS1: " + Long.toBinaryString(toS1) + " toS2: " + Long.toBinaryString(toS2));
        rexCurr = svalues[(int) toS1];
        rexCurr <<= (msgLength) / 4;
        rexCurr += svalues[(int) (toS2 + (keyLength - 1) * 2)];

        LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr));
        return rexCurr;
    }

    private long keyRotate(long key) {
        byte bit = (byte) (key >>> (keyLength - 1));
        key <<= 1;
        key += bit;
        return key;
    }

    private long expander(long saveMessage) {
        long subMessage = saveMessage;
        long msgExpanded = 0L;
        int position = 2;
        msgExpanded = (subMessage & 0b11);
        subMessage >>>= position;
        byte bits = (byte) (subMessage & 0b11);
        for (; position < msgLength / 2; subMessage >>>= 1) {
            msgExpanded += (bits >>> 1) << position;
            position++;
            msgExpanded += (bits % 2) << position;
            position++;
        }
        msgExpanded += (subMessage & 0b11) << position;
        LOGGER.log(Level.FINE, "saveMessage: " + Long.toBinaryString(saveMessage) + " msgExpanded: " + Long.toBinaryString(msgExpanded));
        return msgExpanded;
    }

}
