package sample.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
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
import sample.service.UserService;
import sample.valueobject.PublicationVO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class PubSubscriptionTest {

	public static final String PUBLICATIONS_PATH = "/publications/";


	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;


	@Autowired
	private FileService fileService;

	User publisher = null;
	User viewer = null;


	@Before
	public void setUp() {

		publisher = userService.createUserIfNotExist("publisher@gmail.com", "password", Role.PUBLISHER);

		viewer = userService.createUserIfNotExist("viewer@gmail.com", "password", Role.VIEWER);

		Publication pub1 = new Publication(null, "pub1", "anut1", 2000, publisher);
		publicationRepository.save(pub1);
		Publication pub2 = new Publication(null, "pub2", "anut2", 2001, publisher);
		publicationRepository.save(pub2);
		
		subscriptionRepository.save(new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, pub1));
		subscriptionRepository.save(new Subscription(null, Subscription.Type.YEARLY, LocalDate.now(), viewer, pub2));

	}

	@Test
	public void givenRepositoryWhenSavedThenPersistedThanDeleted() {

		long count = subscriptionRepository.countByUser(viewer);

		Publication pub3 = new Publication(null, "pub3", "anut3", 2002, publisher);
		publicationRepository.save(pub3);
		Subscription subscription = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, pub3);

		subscriptionRepository.save(subscription);
		assertEquals(count + 1, subscriptionRepository.countByUser(viewer));

		subscriptionRepository.delete(subscription);
		assertEquals(count, subscriptionRepository.countByUser(viewer));
	}

}