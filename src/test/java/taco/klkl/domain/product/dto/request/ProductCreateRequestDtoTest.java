package taco.klkl.domain.product.dto.request;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ProductCreateRequestDtoTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("유효한 ProductCreateRequestDto 생성 시 검증 통과")
	void validProductCreateRequestDto() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			"Valid product description",
			"Valid address",
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("상품명이 null일 때 검증 실패")
	void nullProductName() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			null,
			"Valid product description",
			"Valid address",
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("상품명은 필수 항목입니다.", violations.iterator().next().getMessage());
	}

	@Test
	@DisplayName("상품명이 빈 문자열일 때 검증 실패")
	void emptyProductName() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"",
			"Valid product description",
			"Valid address",
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("상품명은 비어있을 수 없습니다.", violations.iterator().next().getMessage());
	}

	@ParameterizedTest
	@ValueSource(ints = {101, 200, 1000})
	@DisplayName("상품명이 최대 길이를 초과할 때 검증 실패")
	void productNameExceedsMaxLength(int nameLength) {
		String longName = "a".repeat(nameLength);
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			longName,
			"Valid product description",
			"Valid address",
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("상품명은 100자 이하여야 합니다.", violations.iterator().next().getMessage());
	}

	@Test
	@DisplayName("상품 설명이 null일 때 검증 실패")
	void nullProductDescription() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			null,
			"Valid address",
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("상품 설명은 필수 항목입니다.", violations.iterator().next().getMessage());
	}

	@ParameterizedTest
	@ValueSource(ints = {2001, 3000, 5000})
	@DisplayName("상품 설명이 최대 길이를 초과할 때 검증 실패")
	void productDescriptionExceedsMaxLength(int descriptionLength) {
		String longDescription = "a".repeat(descriptionLength);
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			longDescription,
			"Valid address",
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("상품 설명은 2000자 이하여야 합니다.", violations.iterator().next().getMessage());
	}

	@ParameterizedTest
	@ValueSource(ints = {101, 200, 500})
	@DisplayName("주소가 최대 길이를 초과할 때 검증 실패")
	void addressExceedsMaxLength(int addressLength) {
		String longAddress = "a".repeat(addressLength);
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			"Valid product description",
			longAddress,
			100,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("주소는 100자 이하여야 합니다.", violations.iterator().next().getMessage());
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, -100, -1000})
	@DisplayName("가격이 음수일 때 검증 실패")
	void negativePrice(int price) {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			"Valid product description",
			"Valid address",
			price,
			1L,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("가격은 0 이상이어야 합니다.", violations.iterator().next().getMessage());
	}

	@Test
	@DisplayName("도시 ID가 null일 때 검증 실패")
	void nullCityId() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			"Valid product description",
			"Valid address",
			100,
			null,
			2L,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("도시 ID는 필수 항목입니다.", violations.iterator().next().getMessage());
	}

	@Test
	@DisplayName("상품 소분류 ID가 null일 때 검증 실패")
	void nullSubcategoryId() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			"Valid product description",
			"Valid address",
			100,
			1L,
			null,
			3L
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("상품 소분류 ID은 필수 항목입니다.", violations.iterator().next().getMessage());
	}

	@Test
	@DisplayName("통화 ID가 null일 때 검증 실패")
	void nullCurrencyId() {
		ProductCreateRequestDto dto = new ProductCreateRequestDto(
			"Valid Product Name",
			"Valid product description",
			"Valid address",
			100,
			1L,
			2L,
			null
		);

		Set<ConstraintViolation<ProductCreateRequestDto>> violations = validator.validate(dto);
		assertFalse(violations.isEmpty());
		assertEquals("통화 ID는 필수 항목입니다.", violations.iterator().next().getMessage());
	}
}
