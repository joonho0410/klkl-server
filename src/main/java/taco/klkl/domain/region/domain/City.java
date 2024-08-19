package taco.klkl.domain.region.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "city")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {

	@Id
	@Column(name = "city_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(
		fetch = FetchType.LAZY,
		optional = false
	)
	@JoinColumn(
		name = "country_id",
		nullable = false
	)
	private Country country;

	@Column(
		name = "name",
		length = 50,
		nullable = false
	)
	private CityType name;

	private City(
		final Country country,
		final CityType name
	) {
		this.country = country;
		this.name = name;
	}

	public static City of(
		final Country country,
		final CityType name
	) {
		return new City(country, name);
	}
}
