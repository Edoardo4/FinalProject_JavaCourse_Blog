package it.course.myblogc3.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostResponseForSearch {

	private Long id;
	private String title;
	private String content;
	private String authorName;
	private Date updateAt;

	
}
