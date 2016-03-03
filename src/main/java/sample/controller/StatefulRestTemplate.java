package sample.controller;

import lombok.Getter;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StatefulRestTemplate extends RestTemplate {

    String url = null;

    @Getter
    private RestTemplate statefulRestTemplate = null;

    @Getter
    private HttpHeaders reqHeaders = new HttpHeaders();

    public StatefulRestTemplate(String url, String path, String username, String password) {

        super();
        this.url = url;


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("username", username);
        form.set("password", password);


        statefulRestTemplate = new RestTemplate();

        //request CSRF
        ResponseEntity<String> page = statefulRestTemplate.getForEntity(getUrl(path), String.class);

        //store the csrf token
        form.set("_csrf", getCsrf(page));
        headers.set("Cookie",  getSessionCookie(page));

        //authenticate
        final HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);
        ResponseEntity<String> responseEntity = statefulRestTemplate.exchange(getUrl(path), HttpMethod.POST, requestEntity, String.class);
        reqHeaders.set("Cookie",  getSessionCookie(responseEntity));

    }

    public String getUrl(String path) {
        return this.url + path;
    }

    public HttpHeaders setJsonRequstHeaders() {
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
