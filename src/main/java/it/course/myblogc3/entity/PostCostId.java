package it.course.myblogc3.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor
public class PostCostId  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="POST_ID", nullable=false)
	private Post post;
	
	@Column(columnDefinition = "DATE", nullable = false)
	private LocalDate startDate;
	
	@Column(columnDefinition = "DATE", nullable = false)
	private LocalDate endDate;
}
