package taco.klkl.domain.product.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import taco.klkl.domain.category.domain.category.QCategory;
import taco.klkl.domain.category.domain.subcategory.QSubcategory;
import taco.klkl.domain.category.domain.subcategory.Subcategory;
import taco.klkl.domain.category.domain.tag.QTag;
import taco.klkl.domain.category.domain.tag.Tag;
import taco.klkl.domain.category.exception.subcategory.SubcategoryNotFoundException;
import taco.klkl.domain.member.domain.Member;
import taco.klkl.domain.product.dao.ProductRepository;
import taco.klkl.domain.product.domain.Product;
import taco.klkl.domain.product.domain.QProduct;
import taco.klkl.domain.product.domain.QProductTag;
import taco.klkl.domain.product.domain.Rating;
import taco.klkl.domain.product.domain.SortCriteria;
import taco.klkl.domain.product.dto.request.ProductCreateUpdateRequest;
import taco.klkl.domain.product.dto.request.ProductFilterOptions;
import taco.klkl.domain.product.dto.request.ProductSortOptions;
import taco.klkl.domain.product.dto.response.ProductDetailResponse;
import taco.klkl.domain.product.dto.response.ProductSimpleResponse;
import taco.klkl.domain.product.exception.InvalidCityIdsException;
import taco.klkl.domain.product.exception.ProductMemberNotMatchException;
import taco.klkl.domain.product.exception.ProductNotFoundException;
import taco.klkl.domain.product.exception.SortDirectionNotFoundException;
import taco.klkl.domain.region.domain.city.City;
import taco.klkl.domain.region.domain.city.QCity;
import taco.klkl.domain.region.domain.country.QCountry;
import taco.klkl.domain.region.domain.currency.Currency;
import taco.klkl.domain.region.exception.city.CityNotFoundException;
import taco.klkl.domain.region.exception.currency.CurrencyNotFoundException;
import taco.klkl.global.common.response.PagedResponse;
import taco.klkl.global.util.CityUtil;
import taco.klkl.global.util.CurrencyUtil;
import taco.klkl.global.util.MemberUtil;
import taco.klkl.global.util.ProductUtil;
import taco.klkl.global.util.SubcategoryUtil;
import taco.klkl.global.util.TagUtil;

@Slf4j
@Primary
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final JPAQueryFactory queryFactory;
	private final ProductRepository productRepository;

	private final MemberUtil memberUtil;
	private final TagUtil tagUtil;
	private final SubcategoryUtil subcategoryUtil;
	private final CityUtil cityUtil;
	private final CurrencyUtil currencyUtil;
	private final ProductUtil productUtil;

	@Override
	public PagedResponse<ProductSimpleResponse> findProductsByFilterOptionsAndSortOptions(
		final Pageable pageable,
		final ProductFilterOptions filterOptions,
		final ProductSortOptions sortOptions
	) {
		validateFilterOptions(filterOptions);

		final JPAQuery<?> baseQuery = createBaseQuery(filterOptions);
		final long total = getCount(baseQuery);
		final List<Product> products = fetchProducts(baseQuery, pageable, sortOptions);
		final Page<Product> productPage = new PageImpl<>(products, pageable, total);

		return PagedResponse.of(productPage, productUtil::createProductSimpleResponse);
	}

	@Override
	public PagedResponse<ProductSimpleResponse> findProductsByPartialName(
		final String partialName,
		final Pageable pageable,
		final ProductSortOptions sortOptions
	) {
		final QProduct product = QProduct.product;
		final QCity city = QCity.city;
		final QCountry country = QCountry.country;
		final QSubcategory subcategory = QSubcategory.subcategory;
		final QCategory category = QCategory.category;

		final JPAQuery<?> baseQuery = queryFactory
			.from(product)
			.where(product.name.contains(partialName));

		final long total = getCount(baseQuery);

		baseQuery.join(product.city, city).fetchJoin()
			.join(product.subcategory, subcategory).fetchJoin()
			.join(city.country, country).fetchJoin()
			.join(subcategory.category, category).fetchJoin();

		final List<Product> products = fetchProducts(baseQuery, pageable, sortOptions);
		final Page<Product> productPage = new PageImpl<>(products, pageable, total);

		return PagedResponse.of(productPage, productUtil::createProductSimpleResponse);
	}

	@Override
	public ProductDetailResponse findProductById(final Long id) throws ProductNotFoundException {
		final Product product = productRepository.findById(id)
			.orElseThrow(ProductNotFoundException::new);
		return productUtil.createProductDetailResponse(product);
	}

	@Override
	@Transactional
	public ProductDetailResponse createProduct(final ProductCreateUpdateRequest createRequest) {
		final Product product = createProductEntity(createRequest);
		productRepository.save(product);
		if (createRequest.tagIds() != null) {
			Set<Tag> tags = createTagsByTagIds(createRequest.tagIds());
			product.addTags(tags);
		}
		return productUtil.createProductDetailResponse(product);
	}

	@Override
	@Transactional
	public int increaseLikeCount(Product product) {
		return product.increaseLikeCount();
	}

	@Override
	@Transactional
	public int decreaseLikeCount(Product product) {
		return product.decreaseLikeCount();
	}

	@Override
	@Transactional
	public ProductDetailResponse updateProduct(
		final Long id,
		final ProductCreateUpdateRequest updateRequest
	) throws ProductNotFoundException {
		final Product product = productRepository.findById(id)
			.orElseThrow(ProductNotFoundException::new);
		validateMyProduct(product);
		updateProductEntity(product, updateRequest);
		updateProductEntityTags(product, updateRequest.tagIds());
		return productUtil.createProductDetailResponse(product);
	}

	@Override
	@Transactional
	public void deleteProduct(final Long id) throws ProductNotFoundException {
		final Product product = productRepository.findById(id)
			.orElseThrow(ProductNotFoundException::new);
		validateMyProduct(product);
		productRepository.delete(product);
	}

	private JPAQuery<?> createBaseQuery(final ProductFilterOptions filterOptions) {
		final QProduct product = QProduct.product;
		final QProductTag productTag = QProductTag.productTag;
		final QTag tag = QTag.tag;

		JPAQuery<?> query = queryFactory.from(product);

		final BooleanBuilder builder = new BooleanBuilder();
		builder.and(createCityFilter(filterOptions.cityIds()));
		builder.and(createSubcategoryFilter(filterOptions.subcategoryIds()));
		builder.and(createTagFilter(filterOptions.tagIds()));

		if (filterOptions.tagIds() != null && !filterOptions.tagIds().isEmpty()) {
			query = query.leftJoin(product.productTags, productTag)
				.leftJoin(productTag.tag, tag);
		}

		return query.where(builder);
	}

	private long getCount(JPAQuery<?> baseQuery) {
		return Optional.ofNullable(baseQuery.select(QProduct.product.countDistinct()).fetchOne())
			.orElse(0L);
	}

	private List<Product> fetchProducts(
		final JPAQuery<?> baseQuery,
		final Pageable pageable,
		final ProductSortOptions sortOptions
	) {
		final JPAQuery<Product> productQuery = baseQuery.select(QProduct.product).distinct();

		applySorting(productQuery, sortOptions);

		return productQuery
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private void applySorting(final JPAQuery<Product> query, final ProductSortOptions sortOptions) {
		final PathBuilder<Product> pathBuilder = new PathBuilder<>(Product.class, "product");
		final Sort.Direction sortDirection = createSortDirectionByQuery(sortOptions.sortDirection());
		final SortCriteria sortBy = SortCriteria.fromQuery(sortOptions.sortBy());
		final OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(
			sortDirection == Sort.Direction.ASC ? Order.ASC : Order.DESC,
			pathBuilder.get(sortBy.getValue(), Comparable.class)
		);
		query.orderBy(orderSpecifier);
	}

	private BooleanExpression createCityFilter(final Set<Long> cityIds) {
		if (cityIds == null || cityIds.isEmpty()) {
			return null;
		}
		return QProduct.product.city.id.in(cityIds);
	}

	private BooleanExpression createSubcategoryFilter(final Set<Long> subcategoryIds) {
		if (subcategoryIds == null || subcategoryIds.isEmpty()) {
			return null;
		}
		return QProduct.product.subcategory.id.in(subcategoryIds);
	}

	private BooleanExpression createTagFilter(final Set<Long> filterIds) {
		if (filterIds == null || filterIds.isEmpty()) {
			return null;
		}
		return QProductTag.productTag.tag.id.in(filterIds);
	}

	private Set<Tag> createTagsByTagIds(final Set<Long> filterIds) {
		return filterIds.stream()
			.map(tagUtil::findTagEntityById)
			.collect(Collectors.toSet());
	}

	private Product createProductEntity(final ProductCreateUpdateRequest createRequest) {
		final Rating rating = Rating.from(createRequest.rating());
		final Member member = memberUtil.getCurrentMember();
		final City city = findCityById(createRequest.cityId());
		final Subcategory subcategory = findSubcategoryById(createRequest.subcategoryId());
		final Currency currency = findCurrencyById(createRequest.currencyId());

		return Product.of(
			createRequest.name(),
			createRequest.description(),
			createRequest.address(),
			createRequest.price(),
			rating,
			member,
			city,
			subcategory,
			currency
		);
	}

	private void updateProductEntity(final Product product, final ProductCreateUpdateRequest updateRequest) {
		final Rating rating = Rating.from(updateRequest.rating());
		final City city = findCityById(updateRequest.cityId());
		final Subcategory subcategory = findSubcategoryById(updateRequest.subcategoryId());
		final Currency currency = findCurrencyById(updateRequest.currencyId());

		product.update(
			updateRequest.name(),
			updateRequest.description(),
			updateRequest.address(),
			updateRequest.price(),
			rating,
			city,
			subcategory,
			currency
		);
	}

	private void updateProductEntityTags(final Product product, final Set<Long> tagIds) {
		if (tagIds != null) {
			final Set<Tag> updatedTags = createTagsByTagIds(tagIds);
			product.updateTags(updatedTags);
		}
	}

	private Sort.Direction createSortDirectionByQuery(final String query) throws SortDirectionNotFoundException {
		try {
			return Sort.Direction.fromString(query);
		} catch (IllegalArgumentException e) {
			throw new SortDirectionNotFoundException();
		}
	}

	private Pageable createPageableSortedByCreatedAtDesc(final Pageable pageable) {
		return PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			Sort.by(Sort.Direction.DESC, "createdAt")
		);
	}

	private City findCityById(final Long cityId) throws CityNotFoundException {
		return cityUtil.findCityEntityById(cityId);
	}

	private Subcategory findSubcategoryById(final Long subcategoryId) throws SubcategoryNotFoundException {
		return subcategoryUtil.findSubcategoryEntityById(subcategoryId);
	}

	private Currency findCurrencyById(final Long currencyId) throws CurrencyNotFoundException {
		return currencyUtil.findCurrencyEntityById(currencyId);
	}

	private void validateFilterOptions(final ProductFilterOptions filterOptions) {
		if (filterOptions.cityIds() != null) {
			validateCityIds(filterOptions.cityIds());
		}
		if (filterOptions.subcategoryIds() != null) {
			validateSubcategoryIds(filterOptions.subcategoryIds());
		}
		if (filterOptions.tagIds() != null) {
			validateTagIds(filterOptions.tagIds());
		}
	}

	private void validateCityIds(final Set<Long> cityIds) throws InvalidCityIdsException {
		boolean isValidCityIds = cityUtil.isCitiesMappedToSameCountry(cityIds);
		if (!isValidCityIds) {
			throw new InvalidCityIdsException();
		}
	}

	private void validateSubcategoryIds(final Set<Long> subcategoryIds) {
		subcategoryIds.forEach(subcategoryUtil::findSubcategoryEntityById);
	}

	private void validateTagIds(final Set<Long> tagIds) {
		tagIds.forEach(tagUtil::findTagEntityById);
	}

	private void validateMyProduct(final Product product) {
		final Member me = memberUtil.getCurrentMember();
		if (!product.getMember().equals(me)) {
			throw new ProductMemberNotMatchException();
		}
	}
}
