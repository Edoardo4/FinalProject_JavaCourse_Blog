package it.course.myblogc3.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryRequest {

	@NotNull
	private long commentId;
	
	@NotNull @NotBlank
	@Size(max=15)
	private String reason;
	
	@Size(max=100)
	private String description;
}
