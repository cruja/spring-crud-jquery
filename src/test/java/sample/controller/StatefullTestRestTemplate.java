package sample.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sample.model.User;
import sample.repository.UserRepository;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cristi on 22/02/2016.
 */

public class StatefullTestRestTemplate extends RestTemplate {

    String url = null;

    @Getter
    private RestTemplate statefullRestTemaplate = null;

    @Getter
    private HttpHeaders reqHeaders = new HttpHeaders();

    public StatefullTestRestTemplate(String url, String path, String username, String password) {

        super();
        this.url = url;


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("username", username);
        form.set("password", password);


        statefullRestTemaplate = new RestTemplate();

        //request CSRF
        ResponseEntity<String> page = statefullRestTemaplate.getForEntity(getUrl(path), String.class);

        //store the csrf token
        form.set("_csrf", getCsrf(page));
        headers.set("Cookie",  getSessionCookie(page));

        //authenticate
        final HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);
        ResponseEntity<String> responseEntity = statefullRestTemaplate.exchange(getUrl(path), HttpMethod.POST, requestEntity, String.class);
        reqHeaders.set("Cookie",  getSessionCookie(responseEntity));

    }

    String getUrl(String path) {
        return this.url + path;
    }

    HttpHeaders setJsonRequstHeaders() {
        reqHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        reqHeaders.setAcceptCharset(Arrays.asList(Charset.defaultCharset()));
        return reqHeaders;
    }

    private String getSessionCookie(ResponseEntity<String> responseEntity) {
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        String cookieValue = responseHeaders.getFirst("Set-Cookie");
        return cookieValue.substring(0, cookieValue.indexOf(';'));
    }

    private String getCsrf(ResponseEntity<String> responseEntity) {
        String csrf = "no csrf detected";
        String body = responseEntity.getBody();
        if (body != null) {
            Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*").matcher(body);
            if (matcher.find()) {
                csrf = matcher.group(1);
            }
        }
        return csrf;
    }
}
