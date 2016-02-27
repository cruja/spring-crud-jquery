package sample.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;


import sample.model.Publication;
import sample.model.Subscription;
import sample.model.User;


public interface SubscriptionRepository extends DataTablesRepository<Subscription, Long> {
	public List<Subscription> findByUser(User user);
	public Subscription findByUserAndPublication(User user, Publication publication);
	public long countByUser(User user);
	public long countByUserAndPublication(User user, Publication publication);
	public void deleteByPublication(Publication publication);
}
