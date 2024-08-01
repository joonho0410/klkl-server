package taco.klkl.domain.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import taco.klkl.domain.product.dao.ProductRepository;
import taco.klkl.domain.product.domain.Product;
import taco.klkl.domain.product.dto.request.ProductCreateRequestDto;
import taco.klkl.domain.product.dto.request.ProductUpdateRequestDto;
import taco.klkl.domain.product.dto.response.ProductDetailResponseDto;
import taco.klkl.domain.product.dto.response.ProductSimpleResponseDto;
import taco.klkl.domain.product.exception.ProductNotFoundException;
import taco.klkl.domain.user.domain.User;
import taco.klkl.global.util.UserUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final UserUtil userUtil;

	public List<ProductSimpleResponseDto> getAllProducts() {
		return productRepository.findAll().stream()
			.map(ProductSimpleResponseDto::from)
			.toList();
	}

	public ProductDetailResponseDto getProductById(final Long id) {
		final Product product = productRepository.findById(id)
			.orElseThrow(ProductNotFoundException::new);
		return ProductDetailResponseDto.from(product);
	}

	@Transactional
	public ProductDetailResponseDto createProduct(final ProductCreateRequestDto productDto) {
		final Product product = createProductEntity(productDto);
		productRepository.save(product);
		return ProductDetailResponseDto.from(product);
	}

	@Transactional
	public ProductDetailResponseDto updateProduct(final Long id, final ProductUpdateRequestDto productDto) {
		final Product product = productRepository.findById(id)
			.orElseThrow(ProductNotFoundException::new);
		product.update(productDto);
		return ProductDetailResponseDto.from(product);
	}

	@Transactional
	public void deleteProduct(final Long id) {
		final Product product = productRepository.findById(id)
			.orElseThrow(ProductNotFoundException::new);
		productRepository.delete(product);
	}

	private Product createProductEntity(final ProductCreateRequestDto productDto) {
		final User user = userUtil.findTestUser();
		return Product.of(
			user,
			productDto.name(),
			productDto.description(),
			productDto.address(),
			productDto.price(),
			productDto.cityId(),
			productDto.subcategoryId(),
			productDto.currencyId()
		);
	}
}
