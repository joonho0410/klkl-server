package taco.klkl.domain.image.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import taco.klkl.domain.image.dto.request.MultipleImagesUpdateRequest;
import taco.klkl.domain.image.dto.request.MultipleImagesUploadRequest;
import taco.klkl.domain.image.dto.request.SingleImageUpdateRequest;
import taco.klkl.domain.image.dto.request.SingleImageUploadRequest;
import taco.klkl.domain.image.dto.response.MultipleUploadCompleteResponse;
import taco.klkl.domain.image.dto.response.PresignedUrlResponse;
import taco.klkl.domain.image.dto.response.SingleUploadCompleteResponse;
import taco.klkl.domain.image.service.ImageService;

@Slf4j
@RestController
@Tag(name = "0. 이미지", description = "이미지 관련 API")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	@Operation(
		summary = "유저 이미지 업로드 Presigned URL 생성",
		description = "유저 이미지 업로드를 위한 Presigned URL를 생성합니다."
	)
	@PostMapping("/v1/users/me/upload-url")
	public PresignedUrlResponse createUserImageUploadUrl(
		@Valid @RequestBody final SingleImageUploadRequest request
	) {
		return imageService.createUserImageUploadUrl(request);
	}

	@Operation(
		summary = "상품 이미지 업로드 Presigned URL 생성",
		description = "상품 이미지 업로드를 위한 Presigned URL를 생성합니다."
	)
	@PostMapping("/v1/products/{productId}/upload-url")
	public List<PresignedUrlResponse> createProductImageUploadUrls(
		@PathVariable final Long productId,
		@Valid @RequestBody final MultipleImagesUploadRequest request
	) {
		return imageService.createProductImageUploadUrls(productId, request);
	}

	@Operation(
		summary = "유저 이미지 업로드 완료 처리",
		description = "유저 이미지 업로드를 완료 처리합니다."
	)
	@PostMapping("/v1/users/me/upload-complete")
	public SingleUploadCompleteResponse uploadCompleteUserImage(
		@Valid @RequestBody final SingleImageUpdateRequest request
	) {
		return imageService.uploadCompleteUserImage(request);
	}

	@Operation(
		summary = "상품 이미지 업로드 완료 처리",
		description = "상품 이미지 업로드를 완료 처리합니다."
	)
	@PostMapping("/v1/products/{productId}/upload-complete")
	public MultipleUploadCompleteResponse uploadCompleteProductImages(
		@PathVariable final Long productId,
		@Valid @RequestBody final MultipleImagesUpdateRequest request
	) {
		return imageService.uploadCompleteProductImages(productId, request);
	}
}
