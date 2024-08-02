package taco.klkl.domain.region.dto.response;

import taco.klkl.domain.region.domain.Country;

public record CountrySimpleResponseDto(
	Long countryId,
	String name
) {
	public static CountrySimpleResponseDto from(Country country) {
		return new CountrySimpleResponseDto(
			country.getCountryId(),
			country.getName().getKoreanName()
		);
	}
}
