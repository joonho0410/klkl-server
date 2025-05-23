package taco.klkl.domain.comment.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import taco.klkl.domain.member.domain.Member;
import taco.klkl.domain.product.domain.Product;

@Getter
@Entity(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
	@Id
	@Column(name = "comment_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(
		name = "content",
		nullable = false,
		length = 400
	)
	private String content;

	@Column(
		name = "created_at",
		nullable = false
	)
	private LocalDateTime createdAt;

	private Comment(
		final Product product,
		final Member member,
		final String content
	) {
		this.product = product;
		this.member = member;
		this.content = content;
		this.createdAt = LocalDateTime.now();
	}

	public void update(final String content) {
		this.content = content;
	}

	public static Comment of(
		final Product product,
		final Member member,
		final String content
	) {
		return new Comment(product, member, content);
	}
}
