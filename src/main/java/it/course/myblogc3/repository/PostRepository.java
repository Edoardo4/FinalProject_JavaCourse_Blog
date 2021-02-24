package it.course.myblogc3.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.Language;
import it.course.myblogc3.entity.Post;
import it.course.myblogc3.entity.Tag;
import it.course.myblogc3.payload.response.PostDetailResponse;
import it.course.myblogc3.payload.response.PostResponse;
import it.course.myblogc3.payload.response.PostResponseForSearch;
import it.course.myblogc3.payload.response.PostResponseWithPreferredTags;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
	
	boolean existsByIdAndVisibleTrue(Long postId);
	
	boolean existsByTitle(String title);

	boolean existsByTitleStartsWith(String title);             
	boolean existsByTitleLike(String title);

	Optional<Post> findByIdAndVisibleTrue(long id);

	Post getOne(long id);
	
	List<Post> findAllByVisibleTrue();

	Set<Post> findByTagsIn(Set<Tag> tagNames);

	List<Post> findAllByVisibleTrueAndLanguage(Language language);
	
	@Query(value="select new it.course.myblogc3.payload.response.PostResponse("
			+ "p.id, "
			+ "p.title, "
			+ "p.author.id, "
			+ "p.author.username, "	
			+ "p.updatedAt, "
			//+ "COUNT(c.id)"
			+ "size(p.comments)"
			+ ") "
			+ "FROM Post p "
			//+ "LEFT JOIN Comment c ON c.post.id=p.id "
			+ "WHERE p.visible=true GROUP BY p.id "
			+ " "
			)
	List<PostResponse> getPostsVisible();
	
	@Query(value="select new it.course.myblogc3.payload.response.PostResponse("
			+ "p.id, "
			+ "p.title, "
			+ "p.author.id, "
			+ "p.author.username, "	
			+ "p.updatedAt, "
			//+ "COUNT(c.id)"
			+ "size(p.comments)"
			+ ") "
			+ "FROM Post p "
			//+ "LEFT JOIN Comment c ON c.post.id=p.id "
			+ "WHERE p.visible=true AND p.id = :postId "
			+ " "
			)
	PostResponse getPostVisiblePostId(@Param("postId") long postId);
	/*@Query(value="SELECT COUNT(c.id) FROM comment c WHERE c.post_id = postId;", nativeQuery = true)
	Long countComments(@Param("postId") int limiteSuperiore);*/

	//soluzione Adelchi
	@Query(value="SELECT new it.course.myblogc3.payload.response.PostResponse("
			+ "p.id, "
			+ "p.title, "
			+ "p.author.id, "
			+ "p.author.username, "
			+ "p.updatedAt, size(p.comments) "		
			+ ") "
			+ "FROM Post p "
			+ "LEFT JOIN p.tags ts "
			+ "WHERE p.visible = true AND ts.visible = true "
			+ "AND ts.tagName IN (:tags) "
			+ "GROUP BY p.id ORDER BY p.title")
		List<PostResponse> getPostsVisibleByTagNames(@Param("tags") Set<String> tags);
			
	
	 @Query(value="SELECT new it.course.myblogc3.payload.response.PostResponseWithPreferredTags("
			+ "p.id, "
			+ "p.title, "
			+ "p.author.id, "
			+ "p.author.username, "
			+ "p.updatedAt "	
			+ ") "
			+ "FROM Post p "
			+ "LEFT JOIN p.tags ts "
			+ "WHERE p.visible = true AND ts.visible = true "
			+ "AND ts.tagName IN (:tags) "
			+ "GROUP BY p.id ORDER BY p.title")
		List<PostResponseWithPreferredTags> getPostsVisibleByPreferredTag(@Param("tags") Set<String> tags);

	 
		@Query(value="SELECT new it.course.myblogc3.payload.response.PostResponse("
		+ "p.id, "
		+ "p.title, "
		+ "p.author.id, "
		+ "p.author.username, "
		+ "p.updatedAt, size(p.comments) "		
		+ ") "
		+ "FROM Post p "
		+ "WHERE p.visible = true AND p.language.langCode = :langCode AND p.language.visible=true "
		+ "GROUP BY p.id ORDER BY p.title")
	List<PostResponse> getPostsVisibleByLang(@Param("langCode") String langCode);
	
	@Query(value="select new it.course.myblogc3.payload.response.PostDetailResponse("
			+ "p.id, "
			+ "p.title, "
			+ "p.content, "
			+ "p.author.id, "
			+ "p.author.username, "	
			+ "p.updatedAt, "
			+ "COALESCE(ROUND(avg(v.vote),2), 0.00) "
			+ ") "
			+ "FROM Post p "
			+ "LEFT JOIN Voting v ON v.votingId.post.id = p.id "
			+ "WHERE p.visible=true and p.id = :postId AND p.cost=0"
			)
	PostDetailResponse getPostVisiblePublic(@Param("postId") long postId);
	

	@Query(value="SELECT new it.course.myblogc3.payload.response.PostDetailResponse("
			+ "p.id, "
			+ "p.title, "
			+ "p.content, "
			+ "p.author.id, "
			+ "p.author.username, "
			+ "p.updatedAt, "
			+ "COALESCE(AVG(v.vote), 0.00) "//AVERAGE
			+ ") "
			+ "FROM Post p "
			+ "LEFT JOIN Voting v ON p.id = v.votingId.post.id "
			+ "LEFT JOIN PurchasedPost pp ON pp.purchasePostId.post.id=:postId AND pp.purchasePostId.user.id=:userId "
			+ "WHERE p.visible=true AND p.id=:postId "
			)
	PostDetailResponse getPostDetailPrivate(@Param("postId") long postId, @Param("userId") long userId);
	
	
	@Query(value="SELECT "
			+ "p.id "
			+ "FROM Post p "
			+ "WHERE p.visible=true AND p.id=:postId")
			Long getPostId(@Param("postId")long postId);
		
	
	@Transactional
	@Modifying
	@Query(value="DELETE FROM Post p WHERE p.id=:postId")
	void deletePost(@Param("postId") long postId);
	
	@Transactional
	@Modifying
	@Query(value="DELETE FROM Post p WHERE p.title LIKE CONCAT('%',:title,'%')")
	void deletePostByTitle(@Param("title") String title);
	
	
	@Query(value="SELECT p.id, p.title, p.content, p.visible FROM Post p WHERE p.id = :postId")
	Post getPost(@Param("postId") long postId);
	
	@Query(value="SELECT pt.post_id FROM post_tags pt WHERE pt.tag_id = :tagName",nativeQuery = true)
	List<Long> getIdPosts(@Param("tagName") String tagName);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE post p SET p.is_visible = if(p.is_visible = 0, 1, 0) WHERE id = :postId ",nativeQuery = true)
	void changeStatusPost(@Param("postId") long postId);
	  
	
	@Query(value="SELECT p.* FROM post p "
			+ " WHERE p.is_visible=true "
			+ " AND   REGEXP_LIKE(p.title, :wordToFind ) OR REGEXP_LIKE(p.content,:wordToFind )",nativeQuery = true)
	List<Post> getPostsVisibleBySearchCaseSensitiveFalse(String wordToFind);
	
	
	@Query(value="SELECT p.* FROM post p "
			+ " WHERE p.is_visible=true "
			+ " AND   REGEXP_LIKE(p.title, BINARY :wordToFind ) OR REGEXP_LIKE(p.content, BINARY :wordToFind )",nativeQuery = true)
	List<Post> getPostsVisibleBySearchCaseSensitiveTrue(String wordToFind);
	
	@Query(value="select new it.course.myblogc3.payload.response.PostResponseForSearch("
			+ "p.id, "
			+ "p.title, "
			+ "p.content, "
			+ "p.author.username, "	
			+ "p.updatedAt "
			+ ") "
			+ "FROM Post p "
			+ "WHERE p.visible=true "
			)
	List<PostResponseForSearch> getPostResponseForSearch();
	
	

	
	/*@Query(value="SELECT p1 FROM Post p1 "
		+ "JOIN FETCH p1.tag "
		+ "JOIN FETCH p1.author u "
		+ "WHERE p1 IN (SELECT p from Post p "
		+ "JOIN p.tag pt "
		+ "JOIN User u on u.id= :userId "
		+ "JOIN u.preferredTags upt ON upt.tagName=pt.tagName "
		+ "WHERE p.visible = true "
		+ "AND pt.visible=true)"
		)
		Set<Post> getPostsVisibleByPreferredTags(@Param("userId") long userId);*/
}
