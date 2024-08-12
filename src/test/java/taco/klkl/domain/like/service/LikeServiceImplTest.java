package taco.klkl.domain.like.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import taco.klkl.domain.like.dao.LikeRepository;
import taco.klkl.domain.like.domain.Like;
import taco.klkl.domain.like.dto.response.LikeResponseDto;
import taco.klkl.domain.like.exception.LikeCountMaximumException;
import taco.klkl.domain.like.exception.LikeCountMinimumException;
import taco.klkl.domain.product.domain.Product;
import taco.klkl.domain.product.service.ProductService;
import taco.klkl.domain.user.domain.User;
import taco.klkl.global.util.UserUtil;

class LikeServiceImplTest {

	@Mock
	private LikeRepository likeRepository;

	@Mock
	private ProductService productService;

	@Mock
	private UserUtil userUtil;

	@InjectMocks
	private LikeServiceImpl likeService;

	@Mock
	private Product product;

	@Mock
	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("새로운 좋아요 데이터 생성 테스트")
	void testCreateLike() {
		// given
		Long productId = 1L;

		when(productService.getProductEntityById(productId)).thenReturn(product);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(likeRepository.existsByProductAndUser(product, user)).thenReturn(false);
		when(productService.increaseLikeCount(product)).thenReturn(1);
		LikeResponseDto likeResponseDto = LikeResponseDto.of(true, 1);

		// when
		LikeResponseDto returnLikeResponseDto = likeService.createLike(productId);

		// then
		verify(likeRepository).save(any(Like.class));
		verify(productService).increaseLikeCount(product);
		assertThat(returnLikeResponseDto).isEqualTo(likeResponseDto);

	}

	@Test
	@DisplayName("이미 존재하는 좋아요 생성 테스트")
	void testCreateWhenAlreadyExists() {
		// given
		Long productId = 1L;

		when(productService.getProductEntityById(productId)).thenReturn(product);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(likeRepository.existsByProductAndUser(product, user)).thenReturn(true);
		when(product.getLikeCount()).thenReturn(1);
		LikeResponseDto likeResponseDto = LikeResponseDto.of(true, product.getLikeCount());

		// when
		LikeResponseDto returnLikeResponseDto1 = likeService.createLike(productId);
		LikeResponseDto returnLikeResponseDto2 = likeService.createLike(productId);
		LikeResponseDto returnLikeResponseDto3 = likeService.createLike(productId);

		// then
		verify(likeRepository, never()).save(any(Like.class));
		verify(productService, never()).increaseLikeCount(product);
		assertThat(returnLikeResponseDto1).isEqualTo(likeResponseDto);
		assertThat(returnLikeResponseDto2).isEqualTo(likeResponseDto);
		assertThat(returnLikeResponseDto3).isEqualTo(likeResponseDto);
	}

	@Test
	@DisplayName("존재하는 좋아요 데이터 삭제 테스트")
	void testDeleteLike() {
		// given
		Long productId = 1L;

		when(productService.getProductEntityById(productId)).thenReturn(product);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(likeRepository.existsByProductAndUser(product, user)).thenReturn(true);
		when(productService.decreaseLikeCount(product)).thenReturn(0);
		when(product.getLikeCount()).thenReturn(0);
		LikeResponseDto likeResponseDto = LikeResponseDto.of(false, product.getLikeCount());

		// when
		LikeResponseDto returnLikeResponseDto = likeService.deleteLike(productId);

		// then
		verify(likeRepository).deleteByProductAndUser(product, user);
		verify(productService).decreaseLikeCount(product);
		assertThat(returnLikeResponseDto).isEqualTo(likeResponseDto);
	}

	@Test
	@DisplayName("존재하지 않는 좋아요 데이터 삭제 테스트")
	void testDeleteLikeWhenNotExists() {
		// given
		Long productId = 1L;

		when(productService.getProductEntityById(productId)).thenReturn(product);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(likeRepository.existsByProductAndUser(product, user)).thenReturn(false);
		when(product.getLikeCount()).thenReturn(1);
		LikeResponseDto likeResponseDto = LikeResponseDto.of(false, product.getLikeCount());

		// when
		LikeResponseDto returnLikeResponseDto1 = likeService.deleteLike(productId);
		LikeResponseDto returnLikeResponseDto2 = likeService.deleteLike(productId);
		LikeResponseDto returnLikeResponseDto3 = likeService.deleteLike(productId);

		// then
		verify(likeRepository, never()).deleteByProductAndUser(product, user);
		verify(productService, never()).decreaseLikeCount(product);
		assertThat(returnLikeResponseDto1).isEqualTo(likeResponseDto);
		assertThat(returnLikeResponseDto2).isEqualTo(likeResponseDto);
		assertThat(returnLikeResponseDto3).isEqualTo(likeResponseDto);
	}

	@Test
	@DisplayName("새로운 좋아요 데이터 생성 최대값 에러 테스트")
	void testCreateLikeMaximumError() {
		// given
		Long productId = 1L;

		when(productService.getProductEntityById(productId)).thenReturn(product);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(likeRepository.existsByProductAndUser(product, user)).thenReturn(false);
		when(productService.increaseLikeCount(product)).thenThrow(LikeCountMaximumException.class);
		doThrow(LikeCountMaximumException.class).when(product).increaseLikeCount();

		// when & then
		assertThatThrownBy(() -> {
			likeService.createLike(productId);
		}).isInstanceOf(LikeCountMaximumException.class);
	}

	@Test
	@DisplayName("좋아요 데이터 삭제 최소값 에러 테스트")
	void testCreateLikeMinimumError() {
		// given
		Long productId = 1L;

		when(productService.getProductEntityById(productId)).thenReturn(product);
		when(userUtil.getCurrentUser()).thenReturn(user);
		when(likeRepository.existsByProductAndUser(product, user)).thenReturn(true);
		when(productService.decreaseLikeCount(product)).thenThrow(LikeCountMinimumException.class);
		doThrow(LikeCountMinimumException.class).when(product).decreaseLikeCount();

		// when & then
		assertThatThrownBy(() -> {
			likeService.deleteLike(productId);
		}).isInstanceOf(LikeCountMinimumException.class);
	}
}
