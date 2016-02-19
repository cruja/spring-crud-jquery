package sample.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sample.model.Subscription;
import sample.repository.SubscriptionRepository;
import sample.service.UserService;
import sample.valueobject.PublicationVO;

@RestController
public class UserSubscriptionsAsynchController  {
	
	@Autowired
	private MessageSendingOperations<String> messagingTemplate;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

	
	@RequestMapping(value = "/userasynch" , method = RequestMethod.GET)
	public void sendEvent() {
		//@Valid @PathVariable Long userId, @Valid @PathVariable Long subscriptionId
		Long subscriptionId = (long)2;
		Subscription subscription = subscriptionRepository.findOne(subscriptionId);		
		PublicationVO publicationVO = new PublicationVO(subscription.getPublication().getId(), subscription.getPublication().getTitle());
		 this.messagingTemplate.convertAndSend("/topic/usernewsubscription/1", publicationVO);	
	}

	
	@MessageMapping(value = "/usersubscr")
	@SendTo("/topic/usernewsubscription/1")
	public PublicationVO addSubscriedPublication() {
		//@Valid @PathVariable Long userId, @Valid @PathVariable Long subscriptionId
		Long subscriptionId = (long)1;
		Subscription subscription = subscriptionRepository.findOne(subscriptionId);		
		return new PublicationVO(subscription.getPublication().getId(), subscription.getPublication().getTitle());
			
	}


}
