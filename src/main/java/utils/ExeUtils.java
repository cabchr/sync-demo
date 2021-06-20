package utils;

import java.io.*;
import java.util.Random;

public class ExeUtils {

    public static void replaceBytes(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = bufferedInputStream.readAllBytes();

        byte[] replaceBytes = generateReplacementBytes();
        String appendString = generateString(97, 122, 22);
        for (int i = 0; i < bytes.length-4; i++) {
            if (bytes[i] == (byte) 0x63 &&
                bytes[i + 1] == (byte) 0x64 &&
                bytes[i + 2] == (byte) 0x63 &&
                bytes[i + 3] == (byte) 0x5f) {

                System.out.println("replacing bytes with " +  new String(replaceBytes) + " and " + appendString);
                bytes[i] = replaceBytes[0];
                bytes[i + 1] = replaceBytes[1];
                bytes[i + 2] = replaceBytes[2];
                bytes[i + 3] = 0x5f;

                for (int j = 0; j < appendString.length(); j++) {
                    bytes[i + 4 + j] = (byte)appendString.toCharArray()[j];
                }
            }
        }

        bufferedInputStream.close();

        File outFile = new File(filePath.replace(".exe", "_patched.exe"));

        if (outFile.exists()) {
            outFile.delete();
        }

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));

        bufferedOutputStream.write(bytes);

        bufferedOutputStream.close();
    }

    private static byte[] generateReplacementBytes() {
        byte[] bytes = new byte[3];
        int min = 97;
        int max = 122;

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (min + (int)(Math.random() * ((max - min) + 1)));
        }
        return bytes;
    }

    private static String generateString(int min, int max, int length) {
        Random random = new Random();
        return random.ints(min, max + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
