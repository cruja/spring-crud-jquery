package sample.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import sample.model.Publication;


public interface PublicationRepository extends DataTablesRepository<Publication, Long> {

}
