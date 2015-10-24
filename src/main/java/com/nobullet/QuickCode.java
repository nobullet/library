package com.nobullet;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick code, scratch pad.
 */
public class QuickCode {

    public static void main(String[] args) {
        List<String> s = new ArrayList<String>() {
            {
                add("1");
                add("2");
                add("3");
            }
        };
        System.out.println(s.getClass().getName());

        String input = "a ? b ? c ? ";
        int bits = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '?') {
                bits++;
            }
        }
        long max = 1L << bits;
        for (long i = 0; i < max; i++) {
            System.out.println(printfbin(input, i));
        }
    }

    // up to 64 bits.
    public static String printfbin(String pattern, long mask) {
        StringBuilder sb = new StringBuilder();
        short bit = 0;
        for (int z = pattern.length() - 1; z >= 0; z--) {
            char c = pattern.charAt(z);
            if (c == '?') {
                char appendChar = (mask & (1L << bit)) > 0 ? '1' : '0';
                sb.append(appendChar);
                bit++;
            } else {
                sb.append(c);
            }
        }
        return sb.reverse().toString();
    }

    public static void main31(String[] args) {
        System.out.println(pow(3, 30));
    }

    public static long pow(long number, long pow) {
        long powc = 1, result = number, resultPrev = number;
        do {
            resultPrev = result;
            result *= result;
            powc *= 2;
        } while (powc < pow);
        if (powc != pow) {
            powc /= 2;
            for (long i = powc; i < pow; i++) {
                resultPrev *= number;
            }
        } else {
            resultPrev = result;
        }
        return resultPrev;
    }
}
