package sample.controller;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

import sample.model.Subscription;
import sample.model.User;
import sample.repository.SubscriptionRepository;
import sample.service.UserService;
import sample.valueobject.PublicationVO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAuthority('PUBLISHER') or hasAuthority('VIEWER')")
public class SubscriptionController {

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

	
	@RequestMapping(value = { "/subscriptions/" }, method = { RequestMethod.GET })
	public ModelAndView getSubscriptions() {
		return new ModelAndView("subscriptions");
	}


	/**
	 * REST Controller returning {@link DataTablesOutput} containing current user subscriptions
	 */
	@JsonView(DataTablesOutput.View.class)
	@RequestMapping(value = "/data/usersubscriptions", method = RequestMethod.GET)
	public DataTablesOutput<Subscription> getUserSubscriptons(@Valid DataTablesInput input, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {

		User currentUser = userService.getCurrentUser(activeUser);
		Specification<Subscription> andUserSubscriptions = (root, query, cb) -> cb.equal(root.get("user"), currentUser);
		return subscriptionRepository.findAll(input, andUserSubscriptions);
	}
}
