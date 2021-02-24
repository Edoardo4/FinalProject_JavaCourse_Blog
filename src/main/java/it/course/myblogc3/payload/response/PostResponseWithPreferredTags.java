package it.course.myblogc3.payload.response;

import java.util.Date;
import java.util.Set;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostResponseWithPreferredTags {

	private Long id;
	private String title;
	private Long authorId;
	private String authorName;
	private Date updatedAt;
	private Set<String> tagNames;
	
	public PostResponseWithPreferredTags(Long id, String title, Long authorId, String authorName, Date updatedAt) {
		super();
		this.id = id;
		this.title = title;
		this.authorId = authorId;
		this.authorName = authorName;
		this.updatedAt = updatedAt;
	}
	

	/*public static PostResponseWithPreferredTags createFromEntity(PostResponseWhitPreferredTagNeverTag p,Set<String> tagNames) {
		
		return new PostResponseWithPreferredTags(
		p.getId(),
		p.getAuthorName(),
		p.getAuthorId(),
		p.getTitle(),
		p.getUpdatedAt(),
		tagNames
		);
	}*/
	
	
	
}