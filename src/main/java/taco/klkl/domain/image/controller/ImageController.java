package taco.klkl.domain.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import taco.klkl.domain.image.dto.request.ImageUploadRequest;
import taco.klkl.domain.image.dto.response.PresignedUrlResponse;
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
		@Valid @RequestBody final ImageUploadRequest request
	) {
		return imageService.createUserImageUploadUrl(request);
	}

	@Operation(
		summary = "유저 이미지 업로드 완료 처리",
		description = "유저 이미지 업로드를 완료 처리합니다."
	)
	@PostMapping("/v1/users/me/upload-complete")
	public ResponseEntity<Void> uploadCompleteUserImage() {
		imageService.uploadCompleteUserImage();
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "상품 이미지 업로드 Presigned URL 생성",
		description = "상품 이미지 업로드를 위한 Presigned URL를 생성합니다."
	)
	@PostMapping("/v1/products/{productId}/upload-url")
	public PresignedUrlResponse createProductImageUploadUrl(
		@PathVariable final Long productId,
		@Valid @RequestBody final ImageUploadRequest request
	) {
		return imageService.createProductImageUploadUrl(productId, request);
	}

	@Operation(
		summary = "상품 이미지 업로드 완료 처리",
		description = "상품 이미지 업로드를 완료 처리합니다."
	)
	@PostMapping("/v1/products/{productId}/upload-complete")
	public ResponseEntity<Void> uploadCompleteProductImage(
		@PathVariable final Long productId
	) {
		imageService.uploadCompleteProductImage(productId);
		return ResponseEntity.ok().build();
	}
}
