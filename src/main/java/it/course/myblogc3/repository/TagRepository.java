package it.course.myblogc3.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, String>{

	
	@Query(value="SELECT p.tags FROM Post p WHERE p.id=:postId")
	Set<Tag> getTagsInPost(@Param("postId") long postId);
	
	Set<Tag> findByTagNameInAndVisibleTrue(Set<String> tagNames);
	
	Set<Tag> findByTagNameIn(Set<String> tagNames);

	List<Tag> findAllByVisibleTrue();
	
	Optional<Tag> findByTagNameAndVisibleTrue(String tagNames);
	
	@Query(value="SELECT tag.tag_name FROM tag "
			+ "	LEFT JOIN user_preferred_tags ut ON ut.user_id=:userId "
			+ "	where ut.tag_id=tag.tag_name",nativeQuery = true)
	Set<String> getTagNameByPreferredTagUserId(@Param("userId")long userId);
	
	@Query(value="SELECT tag.tag_name FROM tag "
			+ "LEFT JOIN post_tags pt ON pt.post_id=:postId "
			+ "WHERE pt.tag_id=tag.tag_name",nativeQuery = true)
	Set<String> getTagNameByPostId(@Param("postId")long userId);
}
