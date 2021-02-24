package it.course.myblogc3.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="POST_COST")
@Data @AllArgsConstructor @NoArgsConstructor
public class PostCost {

	@EmbeddedId
	private PostCostId postCostId;
	
	@Column(columnDefinition = "TINYINT(1)", nullable = false)
	private int shiftCost;
	  
}
