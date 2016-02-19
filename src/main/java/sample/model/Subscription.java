package sample.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Subscription model
 */
@Entity
@ToString(exclude = {"user", "publication"})
@AllArgsConstructor
@NoArgsConstructor
public class Subscription implements UserEntity {
	public enum Type {MONTHLY, YEARLY};
	
	@Id
	@GeneratedValue
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	private Long id;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private Type type;

	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	private LocalDate date;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonView(DataTablesOutput.View.class)
	private User user;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "publication_id")
	@JsonView(DataTablesOutput.View.class)
	private Publication publication;
	
	@Override
	public boolean isUserEntity(Long userId) {
		return userId.equals(user.getId());
	}

}
