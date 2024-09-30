package taco.klkl.domain.region.service.city;

import java.util.List;

import org.springframework.stereotype.Service;

import taco.klkl.domain.region.dto.response.city.CityHierarchyResponse;
import taco.klkl.domain.region.dto.response.city.CitySimpleResponse;

@Service
public interface CityService {

	List<CitySimpleResponse> findAllCitiesByPartialString(final String partialString);

	CityHierarchyResponse findCityHierarchyById(final Long id);
}
