package com.nobullet.algo;

import com.nobullet.algo.RSA;
import java.math.BigInteger;
import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link Sorts}.
 */
public class RSATest {

    @Test
    public void generateKeyPairEncryptDecrypt() {
        RSA rsa = new RSA();

        RSA.KeyPair keyPair = rsa.generatePredictableKeyPair();

        var message = new BigInteger("123");
        BigInteger cipher = rsa.encryptAcademic(message, keyPair.publicKey());
        BigInteger decrypted = rsa.decryptAcademic(cipher, keyPair.privateKey());

        System.out.println("RSA            test: plain text: " + message + "/ cipher: " +
                cipher + "/ pk: " + keyPair.publicKey() + " / sk: " + 
                keyPair.privateKey() + " / n: " + keyPair.n());

        assertEquals(message, decrypted);
    }

    @Test
    public void generateKeyPairEncryptDecrypt_randomized() {
        RSA rsa = new RSA();

        RSA.KeyPair keyPair = rsa.generateRandomizedKeyPair();

        var message = new BigInteger("123456");
        BigInteger cipher = rsa.encryptAcademic(message, keyPair.publicKey());
        BigInteger decrypted = rsa.decryptAcademic(cipher, keyPair.privateKey());

        System.out.println("RSA RANDOMIZED test: plain text: " + message + "/ cipher: " +
                cipher + "/ pk: " + keyPair.publicKey() + " / sk: " + 
                keyPair.privateKey() + " / n: " + keyPair.n());

        assertEquals(message, decrypted);
    }

    @Test
    public void generateKeyPairEncryptDecrypt_randomized_2() {
        RSA rsa = new RSA();

        RSA.KeyPair keyPair = rsa.generateRandomizedKeyPair();

        var message = new BigInteger("123456");
        BigInteger cipher = rsa.encryptAcademic(message, keyPair.publicKey());
        BigInteger decrypted = rsa.decryptAcademic(cipher, keyPair.privateKey());

        System.out.println("RSA RANDOMIZED test: plain text: " + message + "/ cipher: " +
                cipher + "/ pk: " + keyPair.publicKey() + " / sk: " + 
                keyPair.privateKey() + " / n: " + keyPair.n());

        assertEquals(message, decrypted);
    }

    @Test
    public void generateKeyPairEncryptDecrypt_realisticKeys() {
        RSA rsa = new RSA(1024);

        RSA.KeyPair keyPair = rsa.generateRandomizedKeyPair();

        var message = new BigInteger("123456");
        BigInteger cipher = rsa.encryptAcademic(message, keyPair.publicKey());
        BigInteger decrypted = rsa.decryptAcademic(cipher, keyPair.privateKey());

        System.out.println("RSA RANDOMIZED test: plain text: " + message + "/ cipher: " +
                cipher + "/ pk: " + keyPair.publicKey() + " / sk: " + 
                keyPair.privateKey() + " / n: " + keyPair.n());

        assertEquals(message, decrypted);
    }
}