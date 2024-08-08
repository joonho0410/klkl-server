package taco.klkl.domain.comment.dto.request;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import taco.klkl.global.common.constants.CommentValidationMessage;

@SpringBootTest
public class CommentRequestDtoTest {
	@Autowired
	private Validator validator;

	@Test
	@DisplayName("없는 유저일 경우")
	public void testNotExistUserId() {
		//given
		final CommentRequestDto commentRequestNotExistUser = new CommentRequestDto(
			2L,
			"개추 ^^"
		);

		//when & then
		Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestNotExistUser);
		boolean foundNotNullMessage = violations.stream()
			.anyMatch(violation -> violation.getMessage().equals(CommentValidationMessage.USER_NOT_FOUND));

		assertTrue(foundNotNullMessage);
	}

	@Test
	@DisplayName("user_id가 널값일 경우")
	public void testNulluserId() {
		//given
		final CommentRequestDto commentRequestNotExistUser = new CommentRequestDto(
			null,
			"개추 ^^"
		);

		//when & then
		Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestNotExistUser);
		boolean foundNotNullMessage = violations.stream()
			.anyMatch(violation -> violation.getMessage().equals(CommentValidationMessage.USER_NOT_NULL));

		assertTrue(foundNotNullMessage);
	}

	@Test
	@DisplayName("내용이 널값일 경우")
	public void testNullContent() {
		//given
		final CommentRequestDto commentRequestNotExistUser = new CommentRequestDto(
			1L,
			null
		);

		//when & then
		Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestNotExistUser);
		boolean foundNotNullMessage = violations.stream()
			.anyMatch(violation -> violation.getMessage().equals(CommentValidationMessage.CONTENT_NOT_NULL));

		assertTrue(foundNotNullMessage);
	}

	@Test
	@DisplayName("내용이 빈칸일 경우")
	public void testBlankContent() {
		//given
		final CommentRequestDto commentRequestNotExistUser = new CommentRequestDto(
			1L,
			" "
		);

		//when & then
		Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestNotExistUser);
		boolean foundNotNullMessage = violations.stream()
			.anyMatch(violation -> violation.getMessage().equals(CommentValidationMessage.CONTENT_NOT_BLANK));

		assertTrue(foundNotNullMessage);
	}

	@ParameterizedTest
	@ValueSource(ints = {300, 400, 500})
	@DisplayName("내용이 길이가 너무 길 경우")
	public void testBigContent(int length) {
		//given
		String longContent = "hello".repeat(length);
		final CommentRequestDto commentRequestNotExistUser = new CommentRequestDto(
			1L,
			longContent
		);

		//when & then
		Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestNotExistUser);
		boolean foundNotNullMessage = violations.stream()
			.anyMatch(violation -> violation.getMessage().equals(CommentValidationMessage.CONTENT_SIZE));

		assertTrue(foundNotNullMessage);
	}
}
