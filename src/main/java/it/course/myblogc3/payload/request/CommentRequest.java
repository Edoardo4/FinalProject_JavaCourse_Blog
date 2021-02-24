package it.course.myblogc3.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommentRequest {

	@NotBlank
	//@Pattern(regexp = "{A-Za-z}*")
	@Size(min=1, max=200)
	private String comment;

	
}
