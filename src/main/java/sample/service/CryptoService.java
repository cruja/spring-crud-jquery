package sample.service;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Log4j
@Service
public class CryptoService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    //TODO use a dynamic generated key instead!
    public static final String CRYPTO_KEY = "MY SECRET KEY!!!";

    public byte[] encrypt(String key,  byte[] inputData) throws CryptoException {
        return doCrypto(Cipher.ENCRYPT_MODE, key, inputData);
    }

    public byte[] decrypt(String key, byte[] inputData) throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, key, inputData);
    }

    private byte[] doCrypto(int cipherMode, String key, byte[] inputData) throws CryptoException {
        try {
            //TODO pad key to 16 bytes! for AES algo
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            return cipher.doFinal(inputData);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    @NoArgsConstructor
    public class CryptoException extends Exception {
        public CryptoException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
