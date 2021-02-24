package it.course.myblogc3.payload.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisorySeverityRequest {

	@NotNull @NotBlank 	@Size(max=15)
	private String severityDescription; // LOW, MEDIUM, HIGH ...etc
	
	@NotNull @Min(1) @Max(100)
	private int severityValue; // 1, 2, 3, ...etc
}
