package sample.service;

import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.Application;
import sample.model.Publication;
import sample.model.Subscription;

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

    @Test
    public void givenSubscriptionsWhenRequestedByPublicationIdThenReturned() {

        long pubId1 = (long) 1;
        long pubId2 = (long) 2;
        Publication pub1 = new Publication(pubId1, "pub1", "anut1", 2000, null);
        Publication pub2 = new Publication(pubId2, "pub2", "anut2", 2001, null);


        Subscription s1 = new Subscription((long)1, Subscription.Type.MONTHLY, LocalDate.now(), null, pub1);
        Subscription s2 = new Subscription((long)2, Subscription.Type.YEARLY, LocalDate.now(), null, pub2);


        Map<Long, Subscription> subscriptionsByPubId = pubsubscriptionService.getSubscriptionsMapByPublicationId(Arrays.asList(s1, s2));
        assertEquals(s1, subscriptionsByPubId.get(pubId1));
        assertEquals(s2, subscriptionsByPubId.get(pubId2));
        assertEquals(2, subscriptionsByPubId.size());
    }
}
