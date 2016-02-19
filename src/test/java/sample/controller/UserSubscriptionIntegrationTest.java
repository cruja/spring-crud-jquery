package sample.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import sample.Application;
import sample.model.Publication;
import sample.model.Subscription;
import sample.model.User;
import sample.model.User.Role;
import sample.model.User.Status;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.FileService;
import sample.valueobject.PublicationVO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest({"server.port=8181", "management.port=8888"})
public class UserSubscriptionIntegrationTest {

	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileService fileService;
	
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

		User viewer = new User(null, "viewer", "viewer@gmail.com", "password", Role.VIEWER, Status.ACTIVE);
		userRepository.save(viewer);

		
		Publication pub1 = new Publication(null, "pub1", "anut1", 2000, publisher);
		publicationRepository.save(pub1);
		Publication pub2 = new Publication(null, "pub2", "anut2", 2001, publisher);
		publicationRepository.save(pub2);
		
		subscriptionRepository.save(new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, pub1));
		subscriptionRepository.save(new Subscription(null, Subscription.Type.YEARLY, LocalDate.now(), viewer, pub2));
	}

	@Test
	public void givenRepositoryWhenSavedThenPersisted() {

		
		User viewer =  userRepository.findByEmail("viewer@gmail.com");
		User publisher =  userRepository.findByEmail("publisher@gmail.com");
		assertEquals(2, userRepository.count());
		
		List<Subscription> subscripitons = subscriptionRepository.findByUser(viewer);

		assertEquals(2, subscripitons.size());
		Publication pub3 = new Publication(null, "pub3", "anut3", 2002, publisher);
		publicationRepository.save(pub3);	
		subscriptionRepository.save(new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, pub3));
		assertEquals(3, subscriptionRepository.count());
	}
	
	
	@Test
	public void givenSubscriptionsWhenUserSubscriptionsRequestedThenReturned() throws IOException {
		User viewer =  userRepository.findByEmail("viewer@gmail.com");
		List<Subscription> subscriptions = subscriptionRepository.findByUser(viewer);

		RestTemplate restTemplate = new TestRestTemplate();
	
		PublicationVO[] response = restTemplate.getForObject("http://localhost:8080/usersubscriptions/" + viewer.getId(), PublicationVO[].class);
		PublicationVO[] pubVOs = new PublicationVO[2];
		subscriptions.stream().map(s -> new PublicationVO(s.getPublication().getId(), s.getPublication().getTitle())).collect(Collectors.toList()).toArray(pubVOs);
		assertArrayEquals(pubVOs, response);
		
	}

	@Test
	public void givenSubscriptionsWhenEntityRequestedThenReturned() throws IOException {
		User viewer =  userRepository.findByEmail("viewer@gmail.com");
		List<Subscription> subscripitons = subscriptionRepository.findByUser(viewer);
		Subscription subscr = subscripitons.get(0);
		RestTemplate restTemplate = new TestRestTemplate();
	
		ResponseEntity<byte[]> response = restTemplate.getForEntity("http://localhost:8080/usersubscriptions/" + viewer.getId() + "/subscriptions/" +subscr.getId(), byte[].class);
		assertEquals(HttpStatus.OK,  response.getStatusCode());
		
		Publication publication = subscriptionRepository.findOne(subscr.getId()).getPublication();

		byte[] documentBody = fileService.getFileAsBytes(publication.getId());
		assertArrayEquals(documentBody, response.getBody());
		
	}
}