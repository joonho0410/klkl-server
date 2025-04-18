package taco.klkl.domain.category.controller.category;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import taco.klkl.domain.category.domain.category.Category;
import taco.klkl.domain.category.domain.category.CategoryType;
import taco.klkl.domain.category.domain.subcategory.Subcategory;
import taco.klkl.domain.category.domain.subcategory.SubcategoryType;
import taco.klkl.domain.category.dto.response.category.CategoryDetailResponse;
import taco.klkl.domain.category.dto.response.subcategory.SubcategoryDetailResponse;
import taco.klkl.domain.category.service.category.CategoryService;
import taco.klkl.domain.token.service.TokenProvider;
import taco.klkl.global.config.security.TestSecurityConfig;
import taco.klkl.global.util.ResponseUtil;
import taco.klkl.global.util.TokenUtil;

@WebMvcTest(CategoryController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TokenProvider tokenProvider;

	@MockBean
	private ResponseUtil responseUtil;

	@MockBean
	private TokenUtil tokenUtil;

	@MockBean
	private CategoryService categoryService;

	private final Category category = Category.of(CategoryType.FOOD);
	private final Subcategory subcategory1 = Subcategory.of(category, SubcategoryType.DRESS);
	private final Subcategory subcategory2 = Subcategory.of(category, SubcategoryType.HAIR_CARE);
	private final List<Subcategory> subcategories = Arrays.asList(subcategory1, subcategory2);
	private final List<Subcategory> subcategories2 = Arrays.asList(subcategory1, subcategory2);

	@Test
	@DisplayName("카테고리 컨트롤러 GlobalResponse로 Wrapping되어 나오는지 Test")
	public void testGetAllCategories() throws Exception {
		// given
		List<CategoryDetailResponse> categoryDetailResponse = Arrays.asList(
			new CategoryDetailResponse(1L, "Category1", subcategories.stream()
				.map(SubcategoryDetailResponse::from)
				.toList()),
			new CategoryDetailResponse(2L, "Category2", subcategories2.stream()
				.map(SubcategoryDetailResponse::from)
				.toList())
		);

		// when
		when(categoryService.findAllCategories()).thenReturn(categoryDetailResponse);

		// then
		mockMvc.perform(get("/v1/categories/hierarchy")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.data", hasSize(2)))
			.andExpect(jsonPath("$.data[0].id", is(1)))
			.andExpect(jsonPath("$.data[0].name", is("Category1")))
			.andExpect(jsonPath("$.data[1].id", is(2)))
			.andExpect(jsonPath("$.data[1].name", is("Category2")))
			.andExpect(jsonPath("$.timestamp", notNullValue()));

		verify(categoryService, times(1)).findAllCategories();
	}
}
