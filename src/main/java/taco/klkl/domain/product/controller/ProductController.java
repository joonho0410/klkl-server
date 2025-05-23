package taco.klkl.domain.product.controller;

import java.net.URI;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import taco.klkl.domain.product.dto.request.ProductCreateUpdateRequest;
import taco.klkl.domain.product.dto.request.ProductFilterOptions;
import taco.klkl.domain.product.dto.request.ProductSortOptions;
import taco.klkl.domain.product.dto.response.ProductDetailResponse;
import taco.klkl.domain.product.dto.response.ProductSimpleResponse;
import taco.klkl.domain.product.service.ProductService;
import taco.klkl.global.common.constants.ProductConstants;
import taco.klkl.global.common.response.PagedResponse;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "2. 상품", description = "상품 관련 API")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	@Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
	public PagedResponse<ProductSimpleResponse> getProducts(
		@PageableDefault(size = ProductConstants.DEFAULT_PAGE_SIZE) final Pageable pageable,
		@RequestParam(name = "city_id", required = false) final Set<Long> cityIds,
		@RequestParam(name = "subcategory_id", required = false) final Set<Long> subcategoryIds,
		@RequestParam(name = "tag_id", required = false) final Set<Long> tagIds,
		@RequestParam(name = "sort_by", required = false, defaultValue = "created_at") final String sortBy,
		@RequestParam(name = "sort_direction", required = false, defaultValue = "DESC") final String sortDirection
	) {
		final ProductFilterOptions filterOptions = new ProductFilterOptions(
			cityIds,
			subcategoryIds,
			tagIds
		);
		final ProductSortOptions sortOptions = new ProductSortOptions(
			sortBy,
			sortDirection
		);
		return productService.findProductsByFilterOptionsAndSortOptions(pageable, filterOptions, sortOptions);
	}

	@GetMapping("/search")
	@Operation(summary = "제목으로 상품 목록 조회", description = "제목으로 상품 목록을 조회합니다.")
	public PagedResponse<ProductSimpleResponse> searchProductsByName(
		@RequestParam(value = "name") @NotBlank final String partialName,
		@PageableDefault(size = ProductConstants.DEFAULT_PAGE_SIZE) final Pageable pageable,
		@RequestParam(name = "sort_by", required = false, defaultValue = "created_at") final String sortBy,
		@RequestParam(name = "sort_direction", required = false, defaultValue = "DESC") final String sortDirection
	) {
		final ProductSortOptions sortOptions = new ProductSortOptions(
			sortBy,
			sortDirection
		);
		return productService.findProductsByPartialName(partialName, pageable, sortOptions);
	}

	@GetMapping("/{productId}")
	@Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회합니다.")
	public ProductDetailResponse getProduct(
		@PathVariable final Long productId
	) {
		return productService.findProductById(productId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "상품 등록", description = "상품을 등록합니다.")
	public ResponseEntity<ProductDetailResponse> createProduct(
		@Valid @RequestBody final ProductCreateUpdateRequest createRequest
	) {
		final ProductDetailResponse createdProduct = productService.createProduct(createRequest);
		final URI location = createResourceLocation(createdProduct.id());
		return ResponseEntity.created(location).body(createdProduct);
	}

	@PutMapping("/{productId}")
	@Operation(summary = "상품 정보 수정", description = "상품 정보를 수정합니다.")
	public ProductDetailResponse updateProduct(
		@PathVariable final Long productId,
		@Valid @RequestBody final ProductCreateUpdateRequest updateRequest
	) {
		return productService.updateProduct(productId, updateRequest);
	}

	@DeleteMapping("/{productId}")
	@Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
	public ResponseEntity<Void> deleteProduct(
		@PathVariable final Long productId
	) {
		productService.deleteProduct(productId);
		return ResponseEntity.noContent().build();
	}

	private URI createResourceLocation(final Long resourceId) {
		return ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{productId}")
			.buildAndExpand(resourceId)
			.toUri();
	}
}
