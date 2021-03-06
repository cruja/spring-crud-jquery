package sample.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.*;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.Application;
//import sample.Config;
import sample.model.User;
import sample.model.User.Role;
import sample.model.User.Status;
import sample.repository.UserRepository;
import sample.service.UserService;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest({"server.port=0"})
@DirtiesContext
public class UserIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
	private UserRepository userRepository;

    @Autowired
    private UserService userService;

	User admin = null;
	private StatefulRestTemplate statefulRestTemplate = null;

	@Before
	public void setUp() {

		String userEmail = "testadmin@gmail.com";
		admin = userService.createUserIfNotExist(userEmail, "password", Role.ADMIN);
		statefulRestTemplate = new StatefulRestTemplate("http://localhost:" + port,  "/login", userEmail, "password");
	}

	@Test
	public void givenRepositoryWhenSavedThenPersistedThanRemoved() {
		long count = userRepository.count();
		User user = new User(null, "view", "viewer@gmail.com", "password", Role.VIEWER, Status.ACTIVE);
		userRepository.save(user);
		assertEquals(count + 1, userRepository.count());
		userRepository.delete(user);
		assertEquals(count, userRepository.count());
	}


	@Test
	public void givenUserWhenEntityRequestedThenReturned() {

		Long userId = admin.getId();
		String uri = statefulRestTemplate.getUrl("/users/" + userId);

        HttpHeaders reqHeaders = statefulRestTemplate.getReqHeaders();
        reqHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        reqHeaders.setAcceptCharset(Arrays.asList(Charset.defaultCharset()));

		ResponseEntity<User> response = statefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), User.class);
		assertEquals(HttpStatus.OK,  response.getStatusCode());
		User responseUser = response.getBody();

		// clear passwd
		admin.setPassword(null);
		assertEquals(admin, responseUser);
		
	}
}