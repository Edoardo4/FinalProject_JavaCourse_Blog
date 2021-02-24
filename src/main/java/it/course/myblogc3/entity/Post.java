package it.course.myblogc3.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import it.course.myblogc3.entity.audit.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="POST")
@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Post extends DateAudit{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="TITLE", nullable=false, unique=true, length=100)
	private String title;
	
	@Column(name="CONTENT", columnDefinition="TEXT NOT NULL")
	private String content;
	
	@Column(name="IS_VISIBLE", columnDefinition="TINYINT(1)")
	private Boolean visible = false;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTHOR", nullable=false)
	private User author;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LANGUAGE", nullable=false)
	private Language language;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="POST_TAGS",
		joinColumns = {@JoinColumn(name="POST_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="TAG_ID", referencedColumnName="TAG_NAME")}
			)
	private Set<Tag> tags;
	
	/*@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="POST_COMMENTS",
		joinColumns = {@JoinColumn(name="POST_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="COMMENT_ID", referencedColumnName="ID")}
			)
	private Set<Comment> comments;*/
	
	@OneToMany(mappedBy="post", cascade=CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action=OnDeleteAction.CASCADE)
	List<Comment> comments;
	 
	@Column(columnDefinition = "TINYINT(2)", nullable = false)
	private int cost = 0;
	
	public Post(String title, String content, User author) {
		super();
		this.title = title;
		this.content = content;
		this.author = author;

	}
	public Post(Long id) {
		super();
		this.id = id;
	}
	public Post(String title, String content, User author, Language language) {
		super();
		this.title = title;
		this.content = content;
		this.author = author;
		this.language = language;
	
	}
	public Post(String title, String content, Boolean visible, User author, Language language) {
		super();
		this.title = title;
		this.content = content;
		this.visible = visible;
		this.author = author;
		this.language = language;
	}
	

	


	
	
}
