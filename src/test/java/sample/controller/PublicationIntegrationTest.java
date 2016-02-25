package sample.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import sample.Application;
import sample.model.Publication;
import sample.model.User;
import sample.model.User.Role;
import sample.model.User.Status;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.UserService;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest({"server.port=8081", "management.port=8888"})
public class PublicationIntegrationTest {

    @Value("${local.server.port}")
    private int port;

	@Autowired
	private PublicationRepository publicationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	String userEmail =  "newPublisherIT@gmail.com";

    // statefull rest test connection
	private StatefullTestRestTemplate statefullTestRestTemplate = null;

	@Before
	public void setUp() {

		User publisher = userService.createUserIfNotExist(userEmail, "password", Role.PUBLISHER);
		statefullTestRestTemplate = new StatefullTestRestTemplate("http://localhost:" + port,  "/login", userEmail, "password");
        publicationRepository.save(new Publication(null, "publicationTitle", "publicationAuthor", 2011, publisher));
	}


	@Test
	public void givenPublicationWhenEntityRequestedThenReturned() {

        User publisher = userRepository.findByEmail(userEmail);

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);
        assertNotNull(publication.getId());

//        Set<Publication> userPublications = publicationRepository.findByPublisher(publisher);
//        Publication publication = userPublications.iterator().next();
//  	assertNotNull(publication);

        String uri = statefullTestRestTemplate.getUrl("/publications/" + publication.getId());
        HttpHeaders reqHeaders = statefullTestRestTemplate.setJsonRequstHeaders();
        ResponseEntity<Publication> response = statefullTestRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), Publication.class);
		assertEquals(HttpStatus.OK,  response.getStatusCode());
		assertEquals(publication, response.getBody());
		
	}

    @Test
    public void givenPublicationsWhenRequestedThenReturned() {

        String uri = statefullTestRestTemplate.getUrl("/publications/");
        HttpHeaders reqHeaders = statefullTestRestTemplate.getReqHeaders();
        ResponseEntity<String> response = statefullTestRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);
        assertEquals(HttpStatus.OK,  response.getStatusCode());
        assertTrue("Wrong body (title doesn't match):\n" + response.getBody(), response.getBody().contains("<title>Publications</title>"));

    }

    @Test
    public void givenPublicationWhenEntityRemovedThenDeleted() {

        User publisher = userRepository.findByEmail(userEmail);

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);
        assertNotNull(publication.getId());

        String uri = statefullTestRestTemplate.getUrl("/publications/" + publication.getId() + "/delete");
        HttpHeaders reqHeaders = statefullTestRestTemplate.getReqHeaders();
        ResponseEntity<String> response = statefullTestRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);
        assertEquals(HttpStatus.OK,  response.getStatusCode());

        assertNull(publicationRepository.findOne(publication.getId()));
    }

}