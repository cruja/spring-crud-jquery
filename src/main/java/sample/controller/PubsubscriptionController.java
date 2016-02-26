package sample.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

import sample.model.Publication;
import sample.model.Subscription;
import sample.model.User;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.UserService;
import sample.valueobject.PublicationSubscriptionVO;

/**
 *
 */
@RestController
@PreAuthorize("hasAuthority('PUBLISHER') or hasAuthority('VIEWER')")
public class PubsubscriptionController {

	@Autowired
	private PublicationRepository publicationRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;


	@RequestMapping(value = { "/pubsubscriptions/" }, method = { RequestMethod.GET })
	public ModelAndView getPublications() {
		return new ModelAndView("pubsubscriptions");
	}

	/**
	 * REST Controller returning {@link DataTablesOutput}
	 */
	@JsonView(DataTablesOutput.View.class)
	@RequestMapping(value = "/data/pubsubscriptions", method = RequestMethod.GET)
	public DataTablesOutput<PublicationSubscriptionVO> getPublicationsWithUserSubscripitons(
			@Valid DataTablesInput input,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {

		DataTablesOutput<Publication> publicationDTO = publicationRepository.findAll(input);

		// viewer subscriptions
		List<Subscription> subscriptions = userService.getUserSubscriptions(activeUser);
		
		Map<Long, Subscription> userSubscriptionsByPubId = subscriptions.stream().collect(
				Collectors.toMap(s -> s.getPublication().getId(), s -> s));

		DataTablesOutput<PublicationSubscriptionVO> pubSubscriptionDTO = buildPubSubcriptionDTO(publicationDTO,
				userSubscriptionsByPubId);
		
		return pubSubscriptionDTO;
	}

	/**
	 * Build the PubSubscription DataTablesOutput, basically transforming Publication entities into PublicationSubscriptionVO and wrapping them back into DataTableOutput
	 * @param publicationDTO
	 * @param userSubscriptionsByPubId the Map containing for each publicationId its corresponding subscription
	 * @return DataTablesOutput
	 */
	private DataTablesOutput<PublicationSubscriptionVO> buildPubSubcriptionDTO(
			DataTablesOutput<Publication> publicationDTO, Map<Long, Subscription> userSubscriptionsByPubId) {

		DataTablesOutput<PublicationSubscriptionVO> pubSubscriptionDTO = new DataTablesOutput<PublicationSubscriptionVO>();

		pubSubscriptionDTO.setDraw(publicationDTO.getDraw());
		pubSubscriptionDTO.setError(publicationDTO.getError());
		pubSubscriptionDTO.setRecordsFiltered(publicationDTO.getRecordsFiltered());
		pubSubscriptionDTO.setRecordsTotal(publicationDTO.getRecordsTotal());
		
		List<PublicationSubscriptionVO> data = publicationDTO.getData().stream()
				.map(p -> new PublicationSubscriptionVO(p, userSubscriptionsByPubId.get(p.getId())))
				.collect(Collectors.toList());
		
		pubSubscriptionDTO.setData(data);

		return pubSubscriptionDTO;
	}

	@RequestMapping(value = "/pubsubscriptions/{publicationId}/subscribe", method = RequestMethod.GET)
	public void subscribe(@Valid @PathVariable Long publicationId,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser, HttpServletResponse response) throws IOException {

		User viewer = userService.getCurrentUser(activeUser);
		Publication publication = publicationRepository.findOne(publicationId);
		Subscription subscription = new Subscription(null, Subscription.Type.MONTHLY, LocalDate.now(), viewer, publication);
		
		subscriptionRepository.save(subscription);
		response.sendRedirect("/pubsubscriptions/");
	}

	@RequestMapping(value = "/pubsubscriptions/{subscriptionId}/unsubscribe", method = RequestMethod.GET)
	public void unsubscribe(@Valid @PathVariable Long subscriptionId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser, HttpServletResponse response) throws IOException {

		// security validation that current user is the owner
		userService.validateIfEntityOwner(activeUser, subscriptionRepository.findOne(subscriptionId));

		subscriptionRepository.delete(subscriptionId);
		response.sendRedirect("/pubsubscriptions/");
	}

}
