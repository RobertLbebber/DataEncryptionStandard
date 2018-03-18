package crypto;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by trez_ on 3/18/2018.
 */
public class BabyDES {
    private int keyLength;
    private int msgLength;

    private static final Logger LOGGER = Logger.getLogger(FileWorker.class.getName());

    public BabyDES(int keyLength, int msgLength) {
        this.keyLength=keyLength;
        this.msgLength=msgLength;
    }

    public String encode(long[] inputValues) {
        long key = inputValues[0];

        for (int i = 1; i < inputValues.length; i++) {
            long message = inputValues[i];
            long lCurr = message >>> (msgLength/2);
            long rCurr = message & (1<<(msgLength/2)-1);
            LOGGER.log(Level.INFO,"key: "+key+" message: "+ message+" lCurr: "+lCurr+" rCurr:"+rCurr);

        }
        return "";
    }

}
