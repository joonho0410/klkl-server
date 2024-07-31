package taco.klkl.domain.region.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryType {
	JAPAN("일본"),

	CHINA("중국"),

	TAIWAN("대만"),

	THAILAND("태국"),

	VIETNAM("베트남"),

	PHILIPPINES("필리핀"),

	SINGAPORE("싱가포르"),

	INDONESIA("인도네시아"),

	MALAYSIA("말레이시아"),

	GUAM("괌"),

	USA("미국"),

	NONE("");

	private final String koreanName;

	public static CountryType getCountryTypeByName(String name) {
		return Arrays.stream(CountryType.values())
			.filter(c -> c.getKoreanName().equals(name))
			.findFirst()
			.orElse(NONE);
	}
}
