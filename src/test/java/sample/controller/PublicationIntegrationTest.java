package sample.controller;

import lombok.extern.log4j.Log4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.Application;
import sample.model.Publication;
import sample.model.Subscription;
import sample.model.User;
import sample.model.User.Role;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.UserService;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest("server.port=0")
@DirtiesContext
@Log4j
public class PublicationIntegrationTest {

    public static final String PUBLICATIONS_PATH = "/publications/";

    @Value("${local.server.port}")
    private int port;

	@Autowired
	private PublicationRepository publicationRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

    User publisher = null;
    User viewer = null;

    // statefull rest test connection
	private StatefulRestTemplate statefulRestTemplate = null;

	@Before
	public void setUp() {
        String userEmail =  "newPublisherIT@gmail.com";
		publisher = userService.createUserIfNotExist(userEmail, "password", Role.PUBLISHER);
		statefulRestTemplate = new StatefulRestTemplate("http://localhost:" + port,  "/login", userEmail, "password");
        publicationRepository.save(new Publication(null, "publicationTitle", "publicationAuthor", 2011, publisher));


        viewer = userService.createUserIfNotExist( "viewer@gmail.com", "password", Role.VIEWER);
	}


	@Test
	public void givenPublicationWhenEntityRequestedThenReturned() {

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);
        assertNotNull(publication.getId());

        String uri = statefulRestTemplate.getUrl(PUBLICATIONS_PATH + publication.getId());
        HttpHeaders reqHeaders = statefulRestTemplate.setJsonRequstHeaders();
        ResponseEntity<Publication> response = statefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), Publication.class);
		assertEquals(HttpStatus.OK,  response.getStatusCode());
		assertEquals(publication, response.getBody());
		
	}

    @Test
    public void givenPublicationsWhenRequestedThenReturned() {

        String uri = statefulRestTemplate.getUrl(PUBLICATIONS_PATH);
        HttpHeaders reqHeaders = statefulRestTemplate.getReqHeaders();
        ResponseEntity<String> response = statefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);
        assertEquals(HttpStatus.OK,  response.getStatusCode());
        assertTrue("Wrong body (title doesn't match):\n" + response.getBody(), response.getBody().contains("<title>Publications</title>"));

    }

    @Test
    public void givenPublicationWhenEntityRemovedThenDeleted() {

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);

        Subscription subscription = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, publication);
        subscriptionRepository.save(subscription);

        assertNotNull(publication.getId());

        String uri = statefulRestTemplate.getUrl(PUBLICATIONS_PATH + publication.getId() + "/delete");
        HttpHeaders reqHeaders = statefulRestTemplate.getReqHeaders();

        ResponseEntity<String> response = statefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);
        assertEquals(HttpStatus.OK,  response.getStatusCode());

        assertNull(subscriptionRepository.findOne(subscription.getId()));
        assertNull(publicationRepository.findOne(publication.getId()));
    }

}