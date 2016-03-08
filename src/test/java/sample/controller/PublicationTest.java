package sample.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sample.Application;
import sample.model.Publication;
import sample.model.User;
import sample.model.User.Role;
import sample.model.User.Status;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class PublicationTest {

	@Autowired
	private PublicationRepository publicationRepository;
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
	private SubscriptionRepository subscriptionRepository;

    User publisher = null;


    @Before
	public void setUp() {

        publisher = userService.createUserIfNotExist("newPublisherIT@gmail.com", "password", Role.PUBLISHER);
	}

	@Test
	public void givenRepositoryWhenPublicationSavedThenPersisted() {

		long publicationsCount = publicationRepository.count();
        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);

        publicationRepository.save(publication);
		assertEquals(publicationsCount + 1, publicationRepository.count());

	}

    @Test
    public void givenPublicationWhenDeletedThenRemoved() {

        long publicationsCount = publicationRepository.count();
        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);

        publicationRepository.save(publication);
        assertEquals(publicationsCount + 1, publicationRepository.count());

        publicationRepository.delete(publication);
        assertEquals(publicationsCount, publicationRepository.count());
    }

    @Test
    public void givenPublicationWhenUpdatedThenPersisted() {

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

        Publication publication = new Publication(null, "pubTitle", "pubAuthor", 2015, publisher);
        publicationRepository.save(publication);
        Set<Publication> userPublications = publicationRepository.findByPublisher(publisher);
        assertTrue(userPublications.contains(publication));
    }
}