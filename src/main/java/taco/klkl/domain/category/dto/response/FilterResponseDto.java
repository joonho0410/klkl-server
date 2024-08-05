package taco.klkl.domain.category.dto.response;

import taco.klkl.domain.category.domain.Filter;

public record FilterResponseDto(
	Long filterId,
	String filter
) {
	public static FilterResponseDto from(Filter filter) {
		return new FilterResponseDto(filter.getId(), filter.getName().getKoreanName());
	}
}
