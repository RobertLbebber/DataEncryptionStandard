package crypto;

import java.util.logging.Level;
import java.util.logging.Logger;

import static crypto.Driver.ENV_LOGGER_LEVEL;

/**
 * Created by trez_ on 3/18/2018.
 */
public class BabyDES {
    private static final Logger LOGGER = Logger.getLogger(BabyDES.class.getName());
    private final String SBOX1_PATH;
    private final String SBOX2_PATH;
    private final int ITERATIONS;

    private long privateKey;

    static {
        LOGGER.setLevel(ENV_LOGGER_LEVEL);
    }

    private int keyLength;
    private int msgLength;

    public BabyDES(long privateKey, int keyLength, int msgLength, String sbox1Path, String sbox2Path, int iter) {
        this.privateKey = privateKey;
        this.keyLength = keyLength;
        this.msgLength = msgLength;
        ITERATIONS = iter;
        SBOX1_PATH = sbox1Path;
        SBOX2_PATH = sbox2Path;
        LOGGER.info("keyLength: " + keyLength + " msgLength: " + msgLength);
    }

    long getPrivateKey() {
        return privateKey;
    }

    public String encode(long[] inputValues, boolean resetKey) {
        StringBuilder sb = new StringBuilder();
        long key = privateKey;

        for (int i = 0; i < inputValues.length; i++) {
            if (resetKey) {
                key = privateKey;
            }
            long message = inputValues[i];
            long lCurr = message >>> (msgLength / 2);
            long rCurr = message % (1 << msgLength / 2);
            for (int j = 0; j < ITERATIONS; j++) {
                LOGGER.log(Level.INFO, "key: " + key + " message: " + Long.toBinaryString(message) + " lCurr: " + Long.toBinaryString(lCurr) + " rCurr:" + Long.toBinaryString(rCurr));

                long rexCurr = expander(rCurr) ^ (key >>> 1);
                LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr) + " key: " + Long.toBinaryString(key));

                rexCurr = sbox(rexCurr) ^ lCurr;
                LOGGER.log(Level.INFO, "rexCurr2: " + Long.toBinaryString(rexCurr) + " key: " + Long.toBinaryString(key));

                lCurr = rCurr;
                rCurr = rexCurr;
                key = keyRotate(key, true);
                LOGGER.log(Level.INFO, "message: " + Long.toBinaryString(message) + " lCurr: " + Long.toBinaryString(lCurr) + " rCurr: " + Long.toBinaryString(rCurr) + " key: " + Long.toBinaryString(key));
            }
            lCurr <<= msgLength / 2;
            lCurr += rCurr;
            padding(sb, Long.toBinaryString(lCurr), msgLength);
            LOGGER.log(Level.INFO, "finalCurr: " + Long.toBinaryString(lCurr));
        }
        privateKey = keyRotate(key, false);
        return sb.toString();
    }

    private void padding(StringBuilder sb, String asLong, int length) {
        for (int i = asLong.length(); i < length; i++) {
            sb.append("0");
        }
        sb.append(asLong + "\n");
    }

    public String decode(long key, long[] inputValues, boolean resetKey) {
        StringBuilder sb = new StringBuilder();
        privateKey = key;
        for (int i = inputValues.length - 1; i >= 0; i--) {
            if (resetKey) {
                key = privateKey;
            }
            long message = inputValues[i];
            long _rCurr = message >>> (msgLength / 2);
            long _lCurr = message % (1 << msgLength / 2);
            for (int j = 0; j < ITERATIONS; j++) {
                LOGGER.log(Level.INFO, "key: " + key + " message: " + Long.toBinaryString(message) + " lCurr: " + Long.toBinaryString(_lCurr) + " rCurr:" + Long.toBinaryString(_rCurr));

                long _rexCurr = expander(_rCurr) ^ (key >>> 1);
                LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(_rexCurr) + " key: " + Long.toBinaryString(key));

                _rexCurr = sbox(_rexCurr) ^ _lCurr;//reCurr is being used as a temp right here

                _lCurr = _rCurr;
                _rCurr = _rexCurr;
                key = keyRotate(key, false);
                LOGGER.log(Level.INFO, "message: " + Long.toBinaryString(message) + " lCurr: " + Long.toBinaryString(_lCurr) + " rCurr: " + Long.toBinaryString(_rCurr) + " key: " + Long.toBinaryString(key));
            }
            padding(sb, Long.toBinaryString(_rCurr) + "" + Long.toBinaryString(_lCurr), msgLength);
            LOGGER.log(Level.INFO, "finalCurr: " + Long.toBinaryString(_rCurr) + "" + Long.toBinaryString(_lCurr));
        }
        return sb.toString();
    }

    private long sbox(long rexCurr) {
        long[] s1values = FileWorker.readFile(SBOX1_PATH);
        long[] s2values = FileWorker.readFile(SBOX2_PATH);
        long toS1 = rexCurr >>> (keyLength / 2);
        long toS2 = rexCurr % (1 << (keyLength / 2));
        if (rexCurr > Integer.MAX_VALUE) {
            LOGGER.severe("Value of Expanded Submessage is Too Large For Java Primitive Arrays");
        }
        LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr) + " toS1: " + Long.toBinaryString(toS1) + " toS2: " + Long.toBinaryString(toS2));
        rexCurr = s1values[(int) toS1];
        rexCurr <<= msgLength / 4;
        rexCurr += s2values[(int) toS2];

        LOGGER.log(Level.INFO, "rexCurr: " + Long.toBinaryString(rexCurr));
        return rexCurr;
    }

    private long keyRotate(long key, boolean leftward) {
        if (leftward) {
            byte bit = (byte) (key >>> (keyLength - 1));
            key = key & ~(1 << (keyLength - 1));
            key <<= 1;
            key += bit;
        } else {
            byte bit = (byte) (key & 1);
            key = key | (bit << keyLength);
            key >>= 1;
        }
        return key;
    }

    private long expander(long saveMessage) {
        long subMessage = saveMessage;
        long msgExpanded = 0L;
        int position = 2;
        msgExpanded = (subMessage & 0b11);
        subMessage >>>= position;
        byte bits = (byte) (subMessage & 0b11);
        if (msgLength % 4 == 0) {
            for (; position < msgLength / 2; subMessage >>>= 1) {
                msgExpanded += (bits >>> 1) << position;
                position++;
                msgExpanded += (bits % 2) << position;
                position++;
            }
            msgExpanded += (subMessage & 0b11) << position;
        } else {
            msgExpanded ^= msgExpanded << 2;
        }
        LOGGER.log(Level.INFO, "saveMessage: " + Long.toBinaryString(saveMessage) + " msgExpanded: " + Long.toBinaryString(msgExpanded));
        return msgExpanded;
    }

}
