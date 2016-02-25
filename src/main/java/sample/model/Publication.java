package sample.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Publication model
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString //(exclude="publisher")
public class Publication implements UserEntity {

	@Id
	@GeneratedValue
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	private Long id;

	/**
	 * title
	 */
	@NotNull
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	@Size(min = 1, max = 128)
	private String title;

	/**
	 * author
	 */
	@NotNull
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	@Size(min = 1, max = 32)
	private String author;

	/**
	 * year
	 */
	@NotNull
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	@Min(1900)
	@Max(2016)
	private Integer year;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonView(DataTablesOutput.View.class)
	private User publisher;

	@Override
	public boolean isUserEntity(Long userId) {
		return userId.equals(publisher.getId());
	}

}
