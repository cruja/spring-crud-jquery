package sample.service;

import lombok.extern.log4j.Log4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.Application;
import sample.model.Publication;
import sample.model.Subscription;
import sample.repository.PublicationRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;
/**
 * Created by cristi on 03/03/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@Log4j
public class PubsubscriptionTest {

    @Autowired
    private PubsubscriptionService pubsubscriptionService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private FileService fileService;

    @Before
    public void setUp() {
        File baseFolder = new File(FileService.PUBLICATIONS_PATH);
        baseFolder.mkdirs();
    }

    @Test
    public void givenSubscriptionsWhenRequestedByPublicationIdThenReturned() {

        long pubId1 = (long) 1;
        long pubId2 = (long) 2;
        Publication pub1 = new Publication(pubId1, "pub1", "anut1", 2000, null);
        Publication pub2 = new Publication(pubId2, "pub2", "anut2", 2001, null);


        Subscription s1 = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), null, pub1);
        Subscription s2 = new Subscription(null, Subscription.Type.YEARLY, LocalDate.now(), null, pub2);


        Map<Long, Subscription> subscriptionsByPubId = pubsubscriptionService.getSubscriptionsMapByPublicationId(Arrays.asList(s1, s2));
        assertEquals(s1, subscriptionsByPubId.get(pubId1));
        assertEquals(s2, subscriptionsByPubId.get(pubId2));
        assertEquals(2, subscriptionsByPubId.size());
    }
    @Test
    public void givenPublicationWhenStoredThenPersisted() throws IOException {

        Publication pub1 = new Publication(null, "pub1", "anut1", 2000, null);

        byte[] content = "sample content".getBytes();
        pubsubscriptionService.storePublication(pub1, content);

        Long pubId1 = pub1.getId();
        assertNotNull(pubId1);
        assertArrayEquals(content, fileService.getFileAsBytes(pubId1));
    }

    @Test
    public void givenPublicationAndFileLokedWhenStoredThenTransactionException() throws IOException {

            String originalTitle =  "originalTitle";
        Publication pub1 = new Publication(null, originalTitle, "anut1", 2000, null);
        byte[] content = "sample content".getBytes();
        pubsubscriptionService.storePublication(pub1, content);

        Long pubId1 = pub1.getId();
        assertNotNull(pubId1);
        String modifiedTitle = "modifiedPublicationTitle";
        pub1.setTitle(modifiedTitle);
        // lock the file
        File file = new File(FileService.getPublicationLocalFileName(pubId1));
        file.setReadOnly();

        // cleanup on exit
        file.deleteOnExit();

        byte[] contentModified = "sample modified content".getBytes();
        try {
            pubsubscriptionService.storePublication(pub1, contentModified);
            assertTrue("exception should be thrown as the file is set as readonly so can not be modified", false);
        } catch(java.io.FileNotFoundException accessException) {

            // check rollback
            pub1.setTitle(originalTitle);
            assertEquals(pub1, publicationRepository.findOne(pubId1));
            assertArrayEquals(contentModified, fileService.getFileAsBytes(pubId1));
        }


    }
}
