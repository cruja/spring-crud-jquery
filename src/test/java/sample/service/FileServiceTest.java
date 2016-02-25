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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
public class FileServiceTest {

    @Autowired
    private FileService fileService;


    @Before
    public void setUp() {
    }

    @Test
    public void testGetPublicationLocalFileName() {
        assertEquals("./1.pdf", FileService.getPublicationLocalFileName((long)1));
    }

    @Test
    public void givenRepositoryWhenStoredThenPersisted() throws IOException {

        Long id = (long)1111111;
        String content = "sample content";
        fileService.storeFile(content.getBytes(), id);

        assertTrue(new File(FileService.getPublicationLocalFileName(id)).exists());

        byte[] result = fileService.getFileAsBytes(id);
        assertArrayEquals(content.getBytes(), result);
    }

    @Test(expected = FileNotFoundException.class)
    public void givenRepositoryWhenFileNotStoredThenException() throws IOException {

        Long id = (long)1111112;
        String filename = FileService.getPublicationLocalFileName(id);
        File file =  new File(filename);
        if (file.exists()) {
            file.delete();
        }
        assertTrue(file.createNewFile());
        assertTrue(file.setReadOnly());
        String content = "sample content";
        fileService.storeFile(content.getBytes(), id);

    }


}
