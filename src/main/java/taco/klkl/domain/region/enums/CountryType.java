package taco.klkl.domain.region.enums;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	/**
	 *
	 * @param koreanName CountryType 이름
	 * @return CountryType
	 */
	public static CountryType getCountryTypeByName(String koreanName) {
		return Arrays.stream(CountryType.values())
			.filter(c -> c.getKoreanName().equals(koreanName))
			.findFirst()
			.orElse(NONE);
	}

	/**
	 * 부분문자열을 포함하는 CountryType을 찾는 함수
	 * @param partial 부분 문자열
	 * @return 부분문자열을 포함하는 CountryType의 리스트
	 */
	public static List<CountryType> getCountriesByPartialString(String partial) {

		if (partial == null || partial.isEmpty()) {
			return List.of();
		}

		String regex = ".*" + Pattern.quote(partial) + ".*";
		Pattern pattern = Pattern.compile(regex);

		return Arrays.stream(CountryType.values())
			.filter(c -> pattern.matcher(c.getKoreanName()).matches())
			.collect(Collectors.toList());
	}
}
