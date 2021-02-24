package it.course.myblogc3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);	
	
	Optional<User> findByUsernameOrEmail(String username, String email);
	
	Boolean existsByUsernameOrEmail(String username, String email);
	
	List<User> findAllByEnabledTrue();
	
	Optional<User> findByIdAndEnabledTrue(Long id);
	
	Optional<User> findByUsername(String username);
	Optional<User> findByUsernameAndEnabledTrue(String username);
    
    Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	Optional<User> findByIdentifierCode(String identifierCode);

	Optional<User> findByIdentifierCodeAndEmail(String identifierCode, String email);

	Optional<User> findByRegistrationConfirmCodeAndEmail(String registrationConfirmCode, String email);

	
	@Query(value="SELECT * FROM User u INNER JOIN user_authorities ua ON ua.user_id = u.id and ua.authority_id = 2 GROUP BY u.id ",nativeQuery = true)
	List<User> getUserEditor();
	
	@Query(value="SELECT * FROM User u INNER JOIN user_authorities ua ON ua.user_id = u.id and ua.authority_id = 3 GROUP BY u.id ",nativeQuery = true)
	List<User> getUserReader();
	
	/*@Query(value="SELECT new it.course.myblogc3.payload.response.PostResponse("
			+ "p.id, "
			+ "p.title, "
			+ "p.author.id, "
			+ "p.author.username, "
			+ "p.updatedAt, size(p.comments) "		
			+ ") "
			+ "FROM Post p "
			+ "LEFT JOIN p.tag ts "
			+ "WHERE p.visible = true AND ts.visible = true "
			+ "AND ts.tagName IN (:tags) "
			+ "GROUP BY p.id ORDER BY p.title")
		List<PostResponse> getPostsVisibleByTagNames(@Param("tags") Set<String> tags);*/
	
	@Query(value="SELECT * FROM user u "
			+ "	INNER JOIN comment c ON c.comment_author= u.id "
			+ "	WHERE c.id = :idComment",nativeQuery = true)
	User getcommentAuthorFromCommentId(@Param("idComment") Long idComment);
	
	@Query(value="SELECT p.author.username "
			+ "FROM Post p "
			+ "WHERE p.id = :postId ")
	String getAuthorUsernameByPostId(long postId);

}
