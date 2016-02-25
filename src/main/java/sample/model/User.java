package sample.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User model
 */
@ToString(exclude = {"password"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"password"})
@Entity
public class User {
	
	public enum Role {
		ADMIN, PUBLISHER, VIEWER;
	}
	
	public enum Status {
		ACTIVE, BLOCKED;
	}
	
	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	
	@Id
	@GeneratedValue
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	private Long id;

	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	@NotEmpty
	private String name;
	
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	@NotEmpty
	@Email
	private String email;

	@Getter
	@Setter
	@NotEmpty
	@JsonIgnore
	private String password;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private Role role;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private Status status;
	
	
	public void encodePassword(String password) {
		this.password = PASSWORD_ENCODER.encode(password);
	}
	
	@JsonIgnore
	public boolean isActive () {
		return Status.ACTIVE.equals(status);
	}
}
