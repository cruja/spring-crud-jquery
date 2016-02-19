package sample.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.TestRestTemplate.HttpClientOption;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
//@ContextConfiguration(classes = Config.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest({"server.port=8081", "management.port=8888"})
public class UserTest {

	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	/**
	 * Insert sample data at the beginning of all tests
	 */
	
	static String userEmail = "testadmin@gmail.com";
	
	@Before
	public void setUp() {
		
		subscriptionRepository.deleteAll();
		publicationRepository.deleteAll();
		userRepository.deleteAll();
		
		User admin = new User(null, "admin", userEmail, "password", Role.ADMIN, Status.ACTIVE);
		userRepository.save(admin);
		
	}

	@Test
	public void givenRepositoryWhenSavedThenPersistedThenRemoved() {
		assertEquals(1, userRepository.count());
		User user = new User(null, "view", "viewer@gmail.com", "password", Role.VIEWER, Status.ACTIVE);
		userRepository.save(user);
		assertEquals(2, userRepository.count());
		userRepository.delete(user);
		assertEquals(1, userRepository.count());
	}
	 
	

	@Test
	public void givenUserWhenEntityRequestedThenReturned() {

		
		User user = userRepository.findByEmail(userEmail);
		assertNotNull(user);
		Long userId = user.getId();
		
		
		RestTemplate restTemplate = new TestRestTemplate(userEmail, "password");
		String uri = "http://localhost:8080/users/" + userId; 
		restTemplate.headForHeaders(uri).setContentType(MediaType.APPLICATION_JSON);
		//TODO resolve the autentication
		ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);
		//ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
		assertEquals(HttpStatus.OK,  response.getStatusCode());
		User responseUser = response.getBody();
		// clear passwd
		user.setPassword(null);
		assertEquals(user, responseUser);
		
	}
}