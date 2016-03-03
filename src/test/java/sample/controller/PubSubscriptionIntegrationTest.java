package sample.controller;

import java.io.IOException;
import java.time.LocalDate;

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
import sample.service.CryptoService;
import sample.service.FileService;
import sample.service.UserService;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest("server.port=0")
@DirtiesContext
public class PubSubscriptionIntegrationTest {

	public static final String PUBSUBSCRIPTIONS_PATH = "/pubsubscriptions/";

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

    @Autowired
    private CryptoService cryptoService;


	@Autowired
	private FileService fileService;


    User publisher = null;
    User viewer = null;

    // statefull rest test connection
	private StatefulRestTemplate publisherStatefulRestTemplate = null;
	private StatefulRestTemplate viewerStatefulRestTemplate = null;

	/**
	 * Insert sample data at the beginning of all tests
	 */
	@Before
	public void setUp() {

        String publisherEmail =  "publisher@gmail.com";
        String viewerEmail =  "viewer@gmail.com";

        publisher = userService.createUserIfNotExist(publisherEmail, "password", Role.PUBLISHER);
        publisherStatefulRestTemplate = new StatefulRestTemplate("http://localhost:" + port,  "/login", publisherEmail, "password");

        viewer = userService.createUserIfNotExist(viewerEmail, "password", Role.VIEWER);
        viewerStatefulRestTemplate = new StatefulRestTemplate("http://localhost:" + port,  "/login", viewerEmail, "password");
 	}


    @Test
    public void givenPublicationWhenSubscribedThenPersistedWhenUnsubscribedThenDeleted() {


        Publication publication = new Publication(null,  "publicationTitle", "publicationAuthor", 2000, publisher);
        publicationRepository.save(publication);

        String uri = viewerStatefulRestTemplate.getUrl(PUBSUBSCRIPTIONS_PATH + publication.getId() + "/subscribe");
        HttpHeaders reqHeaders = viewerStatefulRestTemplate.getReqHeaders();
        ResponseEntity<String> response = viewerStatefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);

        assertEquals(HttpStatus.OK,  response.getStatusCode());
        Subscription subscription = subscriptionRepository.findByUserAndPublication(viewer, publication);
        assertNotNull(subscription);

        uri = viewerStatefulRestTemplate.getUrl(PUBSUBSCRIPTIONS_PATH + subscription.getId() + "/unsubscribe");
        reqHeaders = viewerStatefulRestTemplate.getReqHeaders();
        response = viewerStatefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);

        assertEquals(HttpStatus.OK,  response.getStatusCode());
        assertEquals(0,  subscriptionRepository.countByUserAndPublication(viewer, publication));
    }

    @Test
    public void givenSubscriptionsWhenPublicationRequestedThenReturned() throws IOException, CryptoService.CryptoException {

        Publication publication = new Publication(null, "publicationTitle", "publicationAuthor", 2000, publisher);
        publicationRepository.save(publication);
        Subscription subscription = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, publication);
        subscriptionRepository.save(subscription);

        byte[] content = "Sample content".getBytes();
        fileService.storeFile(content, publication.getId());
        String uri = viewerStatefulRestTemplate.getUrl(PUBSUBSCRIPTIONS_PATH + publication.getId());
        HttpHeaders reqHeaders = viewerStatefulRestTemplate.getReqHeaders();
        ResponseEntity<byte[]> response = viewerStatefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), byte[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(cryptoService.encrypt(CryptoService.CRYPTO_KEY, content), response.getBody());
    }

}