package io.mishustin.qa;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AesCryptoUtils {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";

    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private AesCryptoUtils() {
    }

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static String encrypt(byte[] pText, String password) {
        try {
            byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
            byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

            SecretKey aesKeyFromPassword = getAESKeyFromPassword(password.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] cipherText = cipher.doFinal(pText);

            byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherText)
                    .array();

            return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            throw new ToolboxException(e);
        }
    }

    public static String decrypt(String cText, String password) {
        try {
            byte[] decode = Base64.getDecoder().decode(cText.getBytes(UTF_8));

            ByteBuffer bb = ByteBuffer.wrap(decode);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);

            byte[] salt = new byte[SALT_LENGTH_BYTE];
            bb.get(salt);

            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            SecretKey aesKeyFromPassword = getAESKeyFromPassword(password.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, UTF_8);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException | IllegalArgumentException e) {
            throw new ToolboxException(e);
        }
    }

}
