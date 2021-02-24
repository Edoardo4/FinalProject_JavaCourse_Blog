package it.course.myblogc3.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Check;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="VOTING")
@Data @AllArgsConstructor @NoArgsConstructor
@Check(constraints = "VOTE > 0 AND VOTE < 6")
public class Voting {

	//min = 1, max = 5
	
	@EmbeddedId
	private VotingId votingId;
	
	@Min(1) @Max(5)
	@Column(columnDefinition = "TINYINT(1)",  nullable = true)
	private int vote;
	
	@Column(name="CREATED_AT",
			updatable=false, insertable=false
			,columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;

	public Voting(VotingId votingId, @Min(1) @Max(5) int vote) {
		super();
		this.votingId = votingId;
		this.vote = vote;
	}
	
	
}
