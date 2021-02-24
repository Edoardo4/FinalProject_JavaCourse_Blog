package it.course.myblogc3.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ADVISORY_SEVERETY")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AdvisorySeverity {

	@Id
	@Column(nullable=false, length = 15)
	private String severityDescription; // LOW, MEDIUM, HIGH ...etc
	
	@Column(nullable=false, columnDefinition="TINYINT(3)")
	private int severityValue; // 1, 2, 3, ...etc
	
	
	
}
