package it.course.myblogc3.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.Comment;
import it.course.myblogc3.payload.response.CommentResponse;
import it.course.myblogc3.payload.response.CommentResponseForPost;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

	Optional<Comment> findByIdAndVisibleTrue(long id);
	
	@Query(value="SELECT new it.course.myblogc3.payload.response.CommentResponse("
			+ "c.id, "
			+ "c.createdAt, "
			+ "c.comment, "
			+ "c.visible,"
			+ "u.username, "
			+ "p.title "
			+ ") "
			+ "FROM Comment c "
			+ "LEFT JOIN User u ON u.id=c.commentAuthor.id "
			+ "LEFT JOIN Post p ON p.id=c.post.id "
			+ "WHERE c.id = :commentId "
			)
	CommentResponse getCommentResponse(@Param("commentId") long postId);
																		

	@Query(value="SELECT new it.course.myblogc3.payload.response.CommentResponseForPost("
			+ "c.id, "
			+ "c.comment, "
			+ "c.createdAt,"
			+ "c.commentAuthor.username "
			+ ") "
			+ "FROM Comment c "
			+ "WHERE c.post.id = :postId "
			)
	List<CommentResponseForPost> getCommentResponseForPost(@Param("postId") long postId);
	
	@Transactional
	@Modifying
	@Query(value="DELETE FROM Comment c WHERE c.id=:commentId")
	void deleteComment(@Param("commentId") long commentId);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE FROM Comment c SET c.visible = false WHERE c.id=:commentId")
	void updateVisibleComment(@Param("commentId") long commentId);
	
	
}
