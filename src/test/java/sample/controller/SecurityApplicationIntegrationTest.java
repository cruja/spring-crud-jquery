package sample.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sample.Application;
import sample.model.User;
import sample.service.UserService;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Basic integration for security
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class SecurityApplicationIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private UserService userService;

    private StatefullTestRestTemplate statefullAdminRestTemplate = null;
    private StatefullTestRestTemplate statefullViewerRestTemplate = null;

    @Before
    public void setUp() {

        userService.createUserIfNotExist("adminEmail@gm.com", "password", User.Role.ADMIN);
        userService.createUserIfNotExist("viewerEmail@gm.com", "password", User.Role.VIEWER);
        statefullViewerRestTemplate = new StatefullTestRestTemplate("http://localhost:" + port,  "/login", "viewerEmail@gm.com", "password");
        statefullAdminRestTemplate = new StatefullTestRestTemplate("http://localhost:" + port,  "/login", "adminEmail@gm.com", "password");
    }

    @Test
    public void giveAuthUserThenReturnHomepage() throws Exception {
        String uri = statefullAdminRestTemplate.getUrl("/");

        HttpHeaders reqHeaders = statefullAdminRestTemplate.getReqHeaders();
        reqHeaders.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        ResponseEntity<String> response = statefullAdminRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Void>(reqHeaders), String.class);
        assertEquals(HttpStatus.OK,  response.getStatusCode());
        assertTrue("Wrong body (title doesn't match):\n" + response.getBody(), response.getBody().contains("<title>Welcome"));
    }

    @Test
    public void givenNoCsrfThenReturnLoginPage() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("username", "adminEmail@gm.com");
        form.set("password", "password");
        ResponseEntity<String> entity = new TestRestTemplate().exchange(statefullAdminRestTemplate.getUrl("/login"), HttpMethod.POST,
                new HttpEntity<MultiValueMap<String, String>>(form, headers),String.class);
        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    }


    @Test
    public void givenNotAuthorizedThenRedirectToLogin() throws Exception {
        ResponseEntity<String> response = statefullViewerRestTemplate.getForEntity(statefullAdminRestTemplate.getUrl("/users/"), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue("Wrong body (title doesn't match):\n" + response.getBody(), response.getBody().contains("<title>Welcome to"));
    }

}
