package taco.klkl.domain.region.controller.region;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
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

import taco.klkl.domain.region.domain.region.Region;
import taco.klkl.domain.region.domain.region.RegionType;
import taco.klkl.domain.region.dto.response.region.RegionResponse;
import taco.klkl.domain.region.service.region.RegionService;
import taco.klkl.domain.token.service.TokenProvider;
import taco.klkl.global.config.security.TestSecurityConfig;
import taco.klkl.global.util.ResponseUtil;
import taco.klkl.global.util.TokenUtil;

@WebMvcTest(RegionController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class RegionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private TokenProvider tokenProvider;

	@MockBean
	private ResponseUtil responseUtil;

	@MockBean
	private TokenUtil tokenUtil;

	@MockBean
	RegionService regionService;

	private final Region region1 = Region.from(RegionType.NORTHEAST_ASIA);
	private final Region region2 = Region.from(RegionType.SOUTHEAST_ASIA);
	private final Region region3 = Region.from(RegionType.ETC);

	@Test
	@DisplayName("모든 지역 조회 성공 테스트")
	void testGetAllRegions() throws Exception {
		// given
		List<RegionResponse> regionResponses = Arrays.asList(
			RegionResponse.from(region1),
			RegionResponse.from(region2),
			RegionResponse.from(region3)
		);

		when(regionService.findAllRegions()).thenReturn(regionResponses);

		// when & then
		mockMvc.perform(get("/v1/regions/hierarchy")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.data", hasSize(3)))
			.andExpect(jsonPath("$.data[0].name", is(region1.getName())))
			.andExpect(jsonPath("$.data[1].name", is(region2.getName())))
			.andExpect(jsonPath("$.data[2].name", is(region3.getName())))
			.andExpect(jsonPath("$.timestamp", notNullValue()));

		verify(regionService, times(1)).findAllRegions();
	}

	@Test
	@DisplayName("모든 지역 조회 empty 테스트")
	void testGetAllRegionsEmpty() throws Exception {
		// given
		when(regionService.findAllRegions()).thenReturn(Collections.emptyList());

		// when & then
		mockMvc.perform(get("/v1/regions/hierarchy")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess", is(true)))
			.andExpect(jsonPath("$.data", hasSize(0)))
			.andExpect(jsonPath("$.timestamp", notNullValue()));

		verify(regionService, times(1)).findAllRegions();
	}
}
