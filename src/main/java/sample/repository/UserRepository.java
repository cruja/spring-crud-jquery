package sample.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import sample.model.User;

public interface UserRepository extends DataTablesRepository<User, Long> {
	User findByEmail(String email);
}
