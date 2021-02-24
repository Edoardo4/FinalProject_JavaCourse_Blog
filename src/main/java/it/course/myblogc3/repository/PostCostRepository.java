package it.course.myblogc3.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.PostCost;
import it.course.myblogc3.entity.PostCostId;

@Repository
public interface PostCostRepository extends JpaRepository<PostCost, PostCostId>{

	@Query(value="SELECT * FROM post_cost pc WHERE :startDate BETWEEN pc.start_date AND pc.end_date AND pc.post_id=:postId",nativeQuery = true)
	List<PostCost> getPostsCostWhereStarDateBetween(@Param("startDate") LocalDate startDate, @Param("postId") long postId);
	

}
