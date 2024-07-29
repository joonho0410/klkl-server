package taco.klkl.domain.product.dto.response;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import taco.klkl.domain.product.domain.Product;

class ProductSimpleResponseDtoTest {
	@Test
	@DisplayName("Product 객체로부터 ProductSimpleResponseDto 생성 테스트")
	void testFromProduct() {
		// given
		Long productId = 1L;
		String name = "맛있는 곤약젤리";
		int likeCount = 10;
		Long cityId = 2L;
		Long subcategoryId = 3L;

		Product mockProduct = Mockito.mock(Product.class);
		when(mockProduct.getProductId()).thenReturn(productId);
		when(mockProduct.getName()).thenReturn(name);
		when(mockProduct.getLikeCount()).thenReturn(likeCount);
		when(mockProduct.getCityId()).thenReturn(cityId);
		when(mockProduct.getSubcategoryId()).thenReturn(subcategoryId);

		// when
		ProductSimpleResponseDto dto = ProductSimpleResponseDto.from(mockProduct);

		// then
		assertThat(dto.productId()).isEqualTo(productId);
		assertThat(dto.name()).isEqualTo(name);
		assertThat(dto.likeCount()).isEqualTo(likeCount);
		assertThat(dto.cityId()).isEqualTo(cityId);
		assertThat(dto.subcategoryId()).isEqualTo(subcategoryId);
	}

	@Test
	@DisplayName("생성자를 통해 ProductSimpleResponseDto 생성 테스트")
	void testConstructor() {
		// given
		Long productId = 1L;
		String name = "맛있는 곤약젤리";
		int likeCount = 10;
		Long cityId = 2L;
		Long subcategoryId = 3L;

		// when
		ProductSimpleResponseDto dto = new ProductSimpleResponseDto(
			productId,
			name,
			likeCount,
			cityId,
			subcategoryId
		);

		// then
		assertThat(dto.productId()).isEqualTo(productId);
		assertThat(dto.name()).isEqualTo(name);
		assertThat(dto.likeCount()).isEqualTo(likeCount);
		assertThat(dto.cityId()).isEqualTo(cityId);
		assertThat(dto.subcategoryId()).isEqualTo(subcategoryId);
	}
}
