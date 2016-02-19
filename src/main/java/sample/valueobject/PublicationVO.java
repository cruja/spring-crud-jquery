package sample.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PublicationVO {

	@Setter
	@Getter
	private Long id;

	@Setter
	@Getter
	private String name;

	@Override
	public String toString() {
		return name;
	}

}
