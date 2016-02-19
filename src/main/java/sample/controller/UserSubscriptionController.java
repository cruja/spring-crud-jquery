package sample.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sample.model.Publication;
import sample.model.Subscription;
import sample.repository.SubscriptionRepository;
import sample.service.FileService;
import sample.service.UserService;
import sample.valueobject.PublicationVO;

/**
 *
 */
@RestController
public class UserSubscriptionController {

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private FileService fileService;

	@RequestMapping(value = "/usersubscriptions/{userId}", method = RequestMethod.GET)
	public List<PublicationVO> getSubscriedPublications(@Valid @PathVariable Long userId) {

		List<Subscription> subscriptions = userService.getUserSubscriptions(userId);
		return subscriptions.stream()
				.map(s -> new PublicationVO(s.getPublication().getId(), s.getPublication().getTitle()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/usersubscriptions/{userId}/subscriptions/{subscriptionId}", method = RequestMethod.GET)
	public HttpEntity<byte[]> getContent(@Valid @PathVariable Long subscriptionId) throws IOException {

		Publication publication = subscriptionRepository.findOne(subscriptionId).getPublication();
		
		byte[] documentBody = fileService.getFileAsBytes(publication.getId());

		HttpHeaders header = prepareHttpHeaders();
		header.setContentLength(documentBody.length);

		return new HttpEntity<byte[]>(documentBody, header);

	}

	private HttpHeaders prepareHttpHeaders() {
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/pdf");
		header.set("Accept-Ranges", "bytes");
		return header;
	}
}
