package sample.valueobject;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;

import sample.model.Publication;
import sample.model.Subscription;

/**
 * Publication's subscription VO for the current user! 
 *
 */
public class PublicationSubscriptionVO {
	
	@JsonView(DataTablesOutput.View.class)
	private Publication publication;
	
	@JsonView(DataTablesOutput.View.class)
	private Subscription subscription;

	public PublicationSubscriptionVO(Publication publication, Subscription subscription) {
		this.publication = publication;
		this.subscription = subscription;
	}
}
