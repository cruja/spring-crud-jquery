package sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.model.Subscription;
import sample.model.User;
import sample.model.UserEntity;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserRepository userRepository;

	
	public List<Subscription> getUserSubscriptions(org.springframework.security.core.userdetails.User activeUser) {
		return getUserSubscriptions(Long.valueOf(activeUser.getUsername()));
	}
	
	public User getActiveUser(org.springframework.security.core.userdetails.User activeUser) {
		return userRepository.findOne(Long.valueOf(activeUser.getUsername()));
	}
	
	public<T extends UserEntity> void validateIfEntityOwner (org.springframework.security.core.userdetails.User activeUser, T entity) {
		if (!entity.isUserEntity(Long.valueOf(activeUser.getUsername()))) {
			throw new SecurityException("not authorized (not owner of entity) for userId: " + activeUser.getUsername() +  " entity: " + entity);
		};
	}

	public<T extends UserEntity> boolean isEntityOwner (org.springframework.security.core.userdetails.User activeUser, T entity) {
		return entity.isUserEntity(Long.valueOf(activeUser.getUsername()));
	}

	public List<Subscription> getUserSubscriptions(Long userId) {

		User user = userRepository.findOne(userId);
		
		//user subscriptions
		return subscriptionRepository.findByUser(user);
	}


}
