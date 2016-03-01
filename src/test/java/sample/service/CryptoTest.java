package sample.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
public class CryptoTest {

    @Autowired
    private CryptoService cryptoService;


    @Before
    public void setUp() {
    }

    @Test
    public void givenContentWhenEncryptDecryptThenMatch() throws CryptoService.CryptoException {
        String key = "MY SECRET KEY!!!";
        byte[] content = "MY_CONTENT_WHICH_NEED_TO_BE_ENCRYPTED".getBytes();
        byte[] encryptedContent = cryptoService.encrypt(key, content);
        byte[] decryptedContent = cryptoService.decrypt(key, encryptedContent);
        assertArrayEquals(content, decryptedContent);
    }

}
