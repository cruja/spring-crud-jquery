package sample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
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
import sample.service.UserService;
import sample.valueobject.PublicationVO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebIntegrationTest("server.port=0")
@DirtiesContext
public class SubscriptionIntegrationTest {

	public static final String USERSUBSCRIPTIONS_PATH = "/usersubscriptions/";

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
	private StatefulRestTemplate publisherStatefulRestTemplate = null;
	private StatefulRestTemplate viewerStatefulRestTemplate = null;


	@Before
	public void setUp() {

        String publisherEmail =  "publisher@gmail.com";
        publisher = userService.createUserIfNotExist(publisherEmail, "password", Role.PUBLISHER);
        publisherStatefulRestTemplate = new StatefulRestTemplate("http://localhost:" + port,  "/login", publisherEmail, "password");

        String viewerEmail =  "viewer@gmail.com";
        viewer = userService.createUserIfNotExist(viewerEmail, "password", Role.VIEWER);
        viewerStatefulRestTemplate = new StatefulRestTemplate("http://localhost:" + port,  "/login", viewerEmail, "password");
 	}

    @Test
	public void givenSubscriptionsWhenRequestedThenReturned() throws IOException {

        Publication publication1 = new Publication(null, "publicationTitle", "publicationAuthor", 2001, publisher);
        publicationRepository.save(publication1);
        Publication publication2 = new Publication(null, "publicationTitle2", "publicationAuthor", 2002, publisher);
        publicationRepository.save(publication2);
        Subscription subscription = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, publication1);
        subscriptionRepository.save(subscription);
        Subscription subscription2 = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, publication2);
        subscriptionRepository.save(subscription2);


        String uri = viewerStatefulRestTemplate.getUrl(USERSUBSCRIPTIONS_PATH);
        viewerStatefulRestTemplate.setJsonRequstHeaders();
        HttpHeaders reqHeaders = viewerStatefulRestTemplate.getReqHeaders();

        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, PublicationVO.class);
        ResponseEntity<String> response = viewerStatefulRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PublicationVO> resultVos = mapper.readValue(response.getBody(), javaType);

        assertTrue(resultVos.size() > 2);
        assertTrue(resultVos.contains(new PublicationVO(publication1.getId(), publication1.getTitle())));
        assertTrue(resultVos.contains(new PublicationVO(publication2.getId(), publication2.getTitle())));

	}

}