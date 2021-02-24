package it.course.myblogc3.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryStatusChangeRequest {

	@NotNull
	private long commentId;
	
	@NotNull 
	private long userId;
	
	@NotNull 
	private long advisory_reason_id;
	
	@NotBlank @NotEmpty
	private String status;
	
	
}
