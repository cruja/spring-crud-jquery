package sample.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import sample.Application;
import sample.Config;
import sample.model.Publication;
import sample.model.User;
import sample.model.User.Role;
import sample.model.User.Status;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@ContextConfiguration(classes = Config.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest({"server.port=8080", "management.port=8888"})
public class PublicationTest {

	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	/**
	 * Insert sample data at the beginning of all tests
	 */
	@Before
	public void setUp() {
		
		subscriptionRepository.deleteAll();
		publicationRepository.deleteAll();
		userRepository.deleteAll();
		
		User publisher = new User(null, "publisher", "publisher@gmail.com", "password", Role.PUBLISHER, Status.ACTIVE);
		userRepository.save(publisher);
		
		publicationRepository.save(new Publication(null, "pub1", "anut1", 2000, publisher));
		publicationRepository.save(new Publication(null, "pub2", "anut2", 2001, publisher));
		publicationRepository.save(new Publication(null, "pub3", "anut3", 2002, publisher));		
	}

	@Test
	public void givenRepositoryWhenSavedThenPersisted() {

		User publisher = userRepository.findOne((long)1);

		assertEquals(3, publicationRepository.count());
		publicationRepository.save(new Publication(null, "pub4", "anut4", 2004, publisher));
		assertEquals(4, publicationRepository.count());
	}
	
	

	@Test
	@Ignore
	public void givenPublicationsWhenEntityRequestedThenReturned() {
		Long pubId = (long)1;
		Publication publication = publicationRepository.findOne(pubId);
		assertNotNull(publication);
		RestTemplate restTemplate = new TestRestTemplate();
		//TODO resolve the autentication
		ResponseEntity<Publication> response = restTemplate.getForEntity("http://localhost:8080/publications/" + pubId, Publication.class);
		assertEquals(HttpStatus.OK,  response.getStatusCode());
		assertEquals(publication, response.getBody());
		
	}
}