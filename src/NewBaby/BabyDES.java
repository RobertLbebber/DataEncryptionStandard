package NewBaby;

/**
 * Created by trez_ on 3/29/2018.
 */
public class BabyDES {
    private int keyLength;
    private int msgLength;
    private char[] privateKey;
    private char[][] sbox1;
    private char[][] sbox2;
    private int iterations;

    public BabyDES(int keyLength, int msgLength, char[] key, char[][] sbox1, char[][] sbox2, int iterations) {
        this.keyLength = keyLength;
        this.msgLength = msgLength;
        this.privateKey = key;
        this.sbox1 = sbox1;
        this.sbox2 = sbox2;
        this.iterations = iterations;
    }

    public char[][] encode(char[][] messages,boolean chaining) {
        char[][] encrypted = new char[messages.length][messages[0].length];

        for (int i = 0; i < messages.length; i++) {
            if(chaining){
                if(i>0){
                    xor(messages[i],encrypted[i-1],messages[0].length);
                }else{//                       0 1 2 3 4 5 6 7 8 9 0 1
                    xor(messages[i],new char[]{1,1,1,1,1,1,1,1,1,1,1,1},messages[0].length);
                }
            }
            char[][] temp = split(messages[i]);
            char[] left = temp[0];
            char[] right = temp[1];

            char[] key = Driver.cloneArray(privateKey);
/*
            System.out.print("-------------------------------------------\n" +
                    "Message = ");
            Driver.printChars(messages[i]);
            System.out.print("Key = ");
            Driver.printChars(key);*/

            for (int j = 0; j < iterations; j++) {
                /*System.out.print("Left = ");
                Driver.printChars(left);*/
                /*System.out.print("Right = ");
                Driver.printChars(right);*/
                char[] expandR = expander(right);
                /*System.out.print("expandR = ");
                Driver.printChars(expandR);*/
                char[] xeR = xor(expandR, key, key.length - 1);
                /*System.out.print("xeR = ");
                Driver.printChars(xeR);*/
                temp = split(xeR);
                char[] s1xeR = getSboxValue(temp[0], true);
                /*System.out.print("s1xeR = ");
                Driver.printChars(s1xeR);*/
                char[] s2xeR = getSboxValue(temp[1], false);
                /*System.out.print("s2xeR = ");
                Driver.printChars(s2xeR);*/
                char[] sxeR = new char[s1xeR.length + s2xeR.length];
                for (int k = 0; k < sxeR.length / 2; k++) {
                    sxeR[k] = s1xeR[k];
                    sxeR[k + sxeR.length / 2] = s2xeR[k];
                }
                /*System.out.print("sxeR = ");
                Driver.printChars(sxeR);*/
                char[] xlsxeR = xor(sxeR, left, left.length);
                /*System.out.print("xlsxeR = ");
                Driver.printChars(xlsxeR);*/

                left = right;
                /*System.out.print("Left = ");
                Driver.printChars(left);*/
                right = xlsxeR;
                key = keyRotate(key, true);
                /*System.out.print("Key = ");
                Driver.printChars(key);*/
//                System.out.println();
            }
            for (int j = 0; j < msgLength / 2; j++) {
                encrypted[i][j] = left[j];
                encrypted[i][j + msgLength / 2] = right[j];
            }
        }
        return encrypted;
    }

    public char[][] decode(char[] safeKey, char[][] messages, boolean chaining) {
        char[][] decryption = new char[messages.length][messages[0].length];
//        privateKey = key;

        for (int i = 0; i < messages.length; i++) {
            char[][] temp = split(messages[i]);
            char[] left = temp[1];
            char[] right = temp[0];

            char[] key = Driver.cloneArray(safeKey);
            /*
            System.out.print("-------------------------------------------\n" +
                    "Message = ");
            Driver.printChars(messages[i]);
            System.out.print("Key = ");
            Driver.printChars(key);*/

            for (int j = 0; j < iterations; j++) {
                /*System.out.print("Right = ");
                Driver.printChars(left);*/
                /*System.out.print("Left = ");
                Driver.printChars(right);*/
                char[] expandR = expander(right);
                /*System.out.print("expandL = ");
                Driver.printChars(expandR);*/
                char[] xeR = xor(expandR, key, key.length - 1);
                /*System.out.print("xeL = ");
                Driver.printChars(xeR);*/
                temp = split(xeR);
                char[] s1xeR = getSboxValue(temp[0], true);
                /*System.out.print("s1xeL = ");
                Driver.printChars(s1xeR);*/
                char[] s2xeR = getSboxValue(temp[1], false);
                /*System.out.print("s2xeL = ");
                Driver.printChars(s2xeR);*/
                char[] sxeR = new char[s1xeR.length + s2xeR.length];
                for (int k = 0; k < sxeR.length / 2; k++) {
                    sxeR[k] = s1xeR[k];
                    sxeR[k + sxeR.length / 2] = s2xeR[k];
                }
                /*System.out.print("sxeL = ");
                Driver.printChars(sxeR);*/
                char[] xlsxeR = xor(sxeR, left, left.length);
                /*System.out.print("xlsxeL = ");
                Driver.printChars(xlsxeR);*/

                left = right;
                /*System.out.print("Left = ");
                Driver.printChars(left);*/
                right = xlsxeR;
                key = keyRotate(key, false);
                /*System.out.print("Key = ");
                Driver.printChars(key);*/
//                System.out.println();
            }
            for (int j = 0; j < msgLength / 2; j++) {
                decryption[i][j] = right[j];
                decryption[i][j + msgLength / 2] = left[j];
            }
            if(chaining){
                if(i>0){
                    xor(decryption[i],decryption[i-1],decryption[i].length);
                }else{//                         0 1 2 3 4 5 6 7 8 9 0 1
                    xor(decryption[i],new char[]{1,1,1,1,1,1,1,1,1,1,1,1},decryption[i].length);
                }
            }
//            Driver.printChars(decryption[i]);
        }
        return decryption;
    }

    private char[][] split(char[] originalMessage) {
        char[][] result = new char[2][msgLength / 2];
        for (int j = 0; j < originalMessage.length / 2; j++) {
            result[0][j] = originalMessage[j];
            result[1][j] = originalMessage[j + originalMessage.length / 2];
        }
        return result;
    }

    private char[] xor(char[] a, char[] b, int length) {
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = (char) (a[i] ^ b[i]);
        }
        return result;
    }

    private char[] getSboxValue(char[] partialMessage, boolean isSBox1) {
        int value = 0;
        if (isSBox1) {
            for (int i = 0; i < 4; i++) {
                value += partialMessage[i] << (3 - i);
            }
            return sbox1[value];
        } else {
            for (int i = 0; i < 4; i++) {
                value += partialMessage[i] << (3 - i);
            }
            return sbox2[value];
        }
    }

    private char[] keyRotate(char[] key, boolean leftward) {
        if (leftward) {
            char carry = key[0];
            for (int i = 0; i < key.length - 1; i++) {
                key[i] = key[i + 1];
            }
            key[key.length - 1] = carry;
        } else {
            char carry = key[key.length - 1];
            for (int i = key.length - 2; i >= 0; i--) {
                key[i + 1] = key[i];
            }
            key[0] = carry;
        }

        return key;
    }

    private char[] expander(char[] originalMessage) {
        char[] msgExpanded = new char[4 + (originalMessage.length - 4) * 2];
        msgExpanded[0] = originalMessage[0];
        msgExpanded[1] = originalMessage[1];

        msgExpanded[2] = originalMessage[3];
        msgExpanded[3] = originalMessage[2];
        msgExpanded[4] = originalMessage[3];
        msgExpanded[5] = originalMessage[2];

        msgExpanded[msgExpanded.length - 2] = originalMessage[originalMessage.length - 2];
        msgExpanded[msgExpanded.length - 1] = originalMessage[originalMessage.length - 1];
        return msgExpanded;
    }

}
