package sample.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.Application;
import sample.model.Publication;
import sample.model.User;
import sample.model.User.Role;
import sample.model.User.Status;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.UserService;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
public class PublicationTest {

	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
	private SubscriptionRepository subscriptionRepository;


	String userEmail =  "newPublisher@gmail.com";

    @Before
	public void setUp() {

        userService.createUserIfNotExist( userEmail, "password", Role.PUBLISHER);
	}

	@Test
	public void givenRepositoryWhenPublicationSavedThenPersisted() {


		User publisher = userRepository.findByEmail(userEmail);

		long publicationsCount = publicationRepository.count();
        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);

        publicationRepository.save(publication);
		assertEquals(publicationsCount + 1, publicationRepository.count());

	}

    @Test
    public void givenPublicationWhenDeletedThenRemoved() {


        User publisher = userRepository.findByEmail(userEmail);

        long publicationsCount = publicationRepository.count();
        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);

        publicationRepository.save(publication);
        assertEquals(publicationsCount + 1, publicationRepository.count());

        publicationRepository.delete(publication);
        assertEquals(publicationsCount, publicationRepository.count());
    }

    @Test
    public void givenPublicationWhenUpdatedThenPersisted() {

        User publisher = userRepository.findByEmail(userEmail);

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);

        publication.setAuthor("newAuthor");
        publication.setTitle("newTitle");
        publication.setYear(2016);
        publicationRepository.save(publication);

        Publication persistedPublication = publicationRepository.findOne(publication.getId());
        assertEquals(publication, persistedPublication);
    }

    @Test
    public void givenRepositoryWhenPublicationSavedThenPersistedForToUser() {

        User publisher = userRepository.findByEmail(userEmail);

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);
        Set<Publication> userPublications = publicationRepository.findByPublisher(publisher);
        assertTrue(userPublications.contains(publication));
    }
}