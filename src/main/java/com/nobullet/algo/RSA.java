package com.nobullet.algo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * RSA (Rivest-Shamir-Adleman): academic example.
 */
public final class RSA {

    private static final Random random = new SecureRandom();

    private final BigInteger p; // Must be prime
    private final BigInteger q; // Must be prime
    private final BigInteger n;
    private final BigInteger phi;

    public RSA(int keyBitSize) {
        this(BigInteger.probablePrime(keyBitSize, random), BigInteger.probablePrime(keyBitSize, random));
    }

    public RSA(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        this.phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // Or N - p - q -1
    }

    // Academic example.
    public RSA() {
        this(new BigInteger("7919"), new BigInteger("1009"));
    }

    public KeyPair generatePredictableKeyPair() {
        // 1 < e < phi(n)
        BigInteger privateKey = new BigInteger("2");
        for (; privateKey.compareTo(phi) < 0; privateKey = privateKey.add(BigInteger.ONE)) {
            if (privateKey.gcd(phi).equals(BigInteger.ONE)) {
                break;
            }
        }
        BigInteger publicKey = privateKey.modInverse(phi);
        return new KeyPair(privateKey, publicKey, n);
    }

    public KeyPair generateRandomizedKeyPair() {
        // 1 < e < phi(n)
        BigInteger privateKey;
        int i = 0;
        do {
            privateKey = new BigInteger(phi.bitLength(), random);
            i++;
        } while (privateKey.compareTo(phi) >= 0 || !privateKey.gcd(phi).equals(BigInteger.ONE));
        System.out.println(" === Iterations: " + i);

        BigInteger publicKey = privateKey.modInverse(phi);
        return new KeyPair(privateKey, publicKey, n);
    }

    public BigInteger encryptAcademic(BigInteger message, BigInteger publicKey) {
        // In practice: ISO standard
        // random x < N should be generated
        // y = x^publicKey mod n
        // cipher = EncryptSymmetric(HASH(x), message)
        // return (y, cipher)
        return message.modPow(publicKey, n);
    }

    public BigInteger decryptAcademic(BigInteger cipher, BigInteger privateKey) {
        // (y, cipher) from above:
        // x = y^privateKey mod n
        // message = DecryptSymmetric(HASH(x), message)
        // return message
        return cipher.modPow(privateKey, n);
    }

    // Key pair
    public static record KeyPair(BigInteger privateKey, BigInteger publicKey, BigInteger n) {
    };
}