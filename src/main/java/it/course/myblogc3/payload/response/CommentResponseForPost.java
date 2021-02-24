package it.course.myblogc3.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommentResponseForPost {
	
	private Long id;
	private String comment;
	private Date createdAt;
	private String usernameAuthorOfComment;
	
	
	
}
