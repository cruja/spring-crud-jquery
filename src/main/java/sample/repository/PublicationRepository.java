package sample.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import sample.model.Publication;
import sample.model.User;

import java.util.Set;


public interface PublicationRepository extends DataTablesRepository<Publication, Long> {
    Set<Publication> findByPublisher(User publisher);
}
