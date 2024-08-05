package taco.klkl.domain.product.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import taco.klkl.domain.product.dao.ProductRepository;
import taco.klkl.domain.product.dto.request.ProductCreateUpdateRequestDto;
import taco.klkl.domain.product.dto.response.ProductDetailResponseDto;
import taco.klkl.domain.product.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Product 통합 테스트")
public class ProductIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ProductService productService;

	@Autowired
	ProductRepository productRepository;

	@Test
	@DisplayName("상품 상세 조회 API 테스트")
	public void testGetProductById() throws Exception {
		// given
		ProductCreateUpdateRequestDto createRequest = new ProductCreateUpdateRequestDto(
			"name",
			"description",
			"address",
			1000,
			414L,
			310L,
			438L
		);
		ProductDetailResponseDto productDto = productService.createProduct(createRequest);

		// when & then
		mockMvc.perform(get("/v1/products/" + productDto.productId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.code", is("C000")))
			.andExpect(jsonPath("$.data.productId", is(productDto.productId().intValue())))
			.andExpect(jsonPath("$.data.userId", is(productDto.userId().intValue())))
			.andExpect(jsonPath("$.data.name", is(productDto.name())))
			.andExpect(jsonPath("$.data.description", is(productDto.description())))
			.andExpect(jsonPath("$.data.address", is(productDto.address())))
			.andExpect(jsonPath("$.data.price", is(productDto.price())))
			.andExpect(jsonPath("$.data.cityId", is(productDto.cityId().intValue())))
			.andExpect(jsonPath("$.data.subcategoryId", is(productDto.subcategoryId().intValue())))
			.andExpect(jsonPath("$.data.currencyId", is(productDto.currencyId().intValue())))
			.andExpect(jsonPath("$.timestamp", notNullValue()));
	}

	@Test
	@DisplayName("상품 등록 API 테스트")
	public void testCreateProduct() throws Exception {
		// given
		ProductCreateUpdateRequestDto createRequest = new ProductCreateUpdateRequestDto(
			"name",
			"description",
			"address",
			1000,
			414L,
			310L,
			438L
		);

		// when & then
		mockMvc.perform(post("/v1/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.code", is("C000")))
			.andExpect(jsonPath("$.data.productId", notNullValue()))
			.andExpect(jsonPath("$.data.userId", notNullValue()))
			.andExpect(jsonPath("$.data.name", is(createRequest.name())))
			.andExpect(jsonPath("$.data.description", is(createRequest.description())))
			.andExpect(jsonPath("$.data.address", is(createRequest.address())))
			.andExpect(jsonPath("$.data.price", is(createRequest.price())))
			.andExpect(jsonPath("$.data.cityId", is(createRequest.cityId().intValue())))
			.andExpect(jsonPath("$.data.subcategoryId", is(createRequest.subcategoryId().intValue())))
			.andExpect(jsonPath("$.data.currencyId", is(createRequest.currencyId().intValue())))
			.andExpect(jsonPath("$.timestamp", notNullValue()));
	}

	@Test
	@DisplayName("모든 상품 조회 API 테스트")
	public void testGetAllProducts() throws Exception {
		// given
		ProductCreateUpdateRequestDto createRequest1 = new ProductCreateUpdateRequestDto(
			"name1",
			"description1",
			"address1",
			1000,
			414L,
			310L,
			438L
		);
		ProductCreateUpdateRequestDto createRequest2 = new ProductCreateUpdateRequestDto(
			"name2",
			"description2",
			"address2",
			2000,
			415L,
			311L,
			438L
		);
		productService.createProduct(createRequest1);
		productService.createProduct(createRequest2);

		// when & then
		mockMvc.perform(get("/v1/products")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.code", is("C000")))
			.andExpect(jsonPath("$.data", hasSize(3)))
			.andExpect(jsonPath("$.data[0].name", is(createRequest1.name())))
			.andExpect(jsonPath("$.data[1].name", is(createRequest2.name())))
			.andExpect(jsonPath("$.timestamp", notNullValue()));
	}

	@Test
	@DisplayName("상품 수정 API 테스트")
	public void testUpdateProduct() throws Exception {
		// given
		ProductCreateUpdateRequestDto updateRequest = new ProductCreateUpdateRequestDto(
			"name",
			"description",
			"address",
			1000,
			414L,
			310L,
			438L
		);
		ProductDetailResponseDto productDto = productService.createProduct(updateRequest);

		ProductCreateUpdateRequestDto updateDto = new ProductCreateUpdateRequestDto(
			"Updated Name",
			"Updated Description",
			"Updated Address",
			2000,
			415L,
			310L,
			438L
		);

		// when & then
		mockMvc.perform(put("/v1/products/" + productDto.productId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.code", is("C000")))
			.andExpect(jsonPath("$.data.productId", is(productDto.productId().intValue())))
			.andExpect(jsonPath("$.data.name", is(updateDto.name())))
			.andExpect(jsonPath("$.data.description", is(updateDto.description())))
			.andExpect(jsonPath("$.data.address", is(updateDto.address())))
			.andExpect(jsonPath("$.data.price", is(updateDto.price())))
			.andExpect(jsonPath("$.data.cityId", is(updateDto.cityId().intValue())))
			.andExpect(jsonPath("$.data.subcategoryId", is(updateDto.subcategoryId().intValue())))
			.andExpect(jsonPath("$.data.currencyId", is(updateDto.currencyId().intValue())))
			.andExpect(jsonPath("$.timestamp", notNullValue()));
	}

	@Test
	@DisplayName("상품 삭제 API 테스트")
	public void testDeleteProduct() throws Exception {
		// given
		ProductCreateUpdateRequestDto createRequest = new ProductCreateUpdateRequestDto(
			"name",
			"description",
			"address",
			1000,
			414L,
			310L,
			438L
		);
		ProductDetailResponseDto productDto = productService.createProduct(createRequest);

		// when & then
		mockMvc.perform(delete("/v1/products/" + productDto.productId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.code", is("C000")))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andExpect(jsonPath("$.timestamp", notNullValue()));
	}
}
