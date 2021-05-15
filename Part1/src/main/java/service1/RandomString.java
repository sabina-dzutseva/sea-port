package service1;

import java.util.Random;

public class RandomString {

    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int maxLength = 10;

    public static String nextString() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = random.nextInt(maxLength - 3) + 3;

        for (int i = 0; i < length; i++) {
            int j = random.nextInt(alphabet.length());
            sb.append(alphabet.toCharArray()[j]);
        }

        return sb.toString();
    }

}
