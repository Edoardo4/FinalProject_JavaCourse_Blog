package it.course.myblogc3.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.Advisory;
import it.course.myblogc3.entity.AdvisoryId;
import it.course.myblogc3.entity.AdvisoryReason;
import it.course.myblogc3.entity.Comment;
import it.course.myblogc3.payload.response.AdvisoryResponse;

@Repository
public interface AdvisoryRepository extends JpaRepository<Advisory, AdvisoryId>{

	@Transactional
	@Modifying
	@Query(value="UPDATE advisory a SET a.status = 1 WHERE reporter = :reporter AND comment_id = :comment_id AND advisory_reason_id = :advisory_reason_id ",nativeQuery = true)
	void changeStatusAdvisory(@Param("reporter") Long reporter, @Param("comment_id") Long comment_id,  @Param("advisory_reason_id") Long advisory_reason_id );

	
	@Query(value="SELECT * from advisory a  WHERE reporter = :reporter AND comment_id = :comment_id AND advisory_reason_id = :advisory_reason_id ",nativeQuery = true)
	Advisory existAdvisoryById(@Param("reporter") Long reporter, @Param("comment_id") Long comment_id,  @Param("advisory_reason_id") Long advisory_reason_id);
	
	/*/@Query(value="SELECT a.created_at, a.updated_at, ads.severity_description, ads.severity_value, "
			+ "a.advisory_reason_id, adr.advisory_reason_name,a.`status`,u.username, a.comment_id "
			+ "FROM advisory a "
			+ "INNER JOIN advisory_reason_detail adt ON a.advisory_reason_id = adt.advisory_reason_id "
			+ "INNER JOIN advisory_severety ads ON adt.advisory_severity = ads.severity_description "
			+ "INNER JOIN advisory_reason adr ON a.advisory_reason_id = adr.id "
			+ "INNER JOIN user u ON a.reporter = u.id "
			+ "INNER JOIN comment c ON c.id = a.comment_id "
			+ "INNER JOIN user uc ON c.comment_author = uc.id "
			+ "GROUP BY a.created_at "
			+ "ORDER BY a.`status`=\"IN_PROGRESS\", a.`status`=\"OPEN\"",nativeQuery = true)
	List<Object>getAdvisoryResponse();*/

	@Query("SELECT NEW it.course.myblogc3.payload.response.AdvisoryResponse("
			+ "a.createdAt, "
			+ "a.updatedAt, "
			+ "ard.advisorySeverity.severityDescription,"
			+ "ard.advisorySeverity.severityValue, "
			+ "a.advisoryId.advisoryReason.id, "
			+ "a.advisoryId.advisoryReason.advisoryReasonName, "
			+ "a.status, "
			+ "a.advisoryId.reporter.username, "
			+ "a.advisoryId.comment.commentAuthor.username, "
			+ "a.advisoryId.comment.id"
			+ ")"
			+ "FROM Advisory a "
			+ "LEFT JOIN AdvisoryReasonDetail ard "
			+ "ON a.advisoryId.advisoryReason.id = ard.advisoryReasonDetailId.advisoryReason.id "
			+ "WHERE a.status IN (0,1) "
			+ "AND a.createdAt BETWEEN ard.advisoryReasonDetailId.startDate AND ard.endDate "
			+ "ORDER BY a.status ASC")
		List<AdvisoryResponse> getOpenAdvisories();                
	
	@Query(value="SELECT ads.severity_value "
			+ "FROM advisory a "
			+ "INNER JOIN advisory_reason_detail adt ON a.advisory_reason_id = adt.advisory_reason_id "
			+ "INNER JOIN advisory_severety ads ON adt.advisory_severity = ads.severity_description "
			+ "WHERE a.reporter = :reporter AND a.comment_id = :comment_id AND a.advisory_reason_id = :advisory_reason_id "
			,nativeQuery = true)
	int getSeverityValueFromAdvisory(@Param("reporter") Long reporter, @Param("comment_id") Long comment_id,  @Param("advisory_reason_id") Long advisory_reason_id);      
	
	/*@Query(value="SELECT ard.advisorySeverity.severityValue "
			+ "FROM Advisory a "
			+ "LEFT JOIN AdvisoryReasonDetail ard "
			+ "ON a.advisoryId.advisoryReason.id = ard.advisoryReasonDetailId.advisoryReason.id "
			+ "WHERE ard.advisoryReasonDetailId.advisoryReason.id = :reasonId "
			+ "AND a.createdAt BETWEEN ard.advisoryReasonDetailId.startDate AND ard.endDate ")
	int getSeverityValue(@Param ("reasonId") long reasonId);*/
	
	//jpql
	/*@Query("select a from Advisory a where a.advisoryId.comment.id=:commentId "
	+ "And a.advisoryId.reporter.id=:reporterId "
	+ "And a.advisoryId.advisoryReason.id=:reason")
	Optional<Advisory> getAdvisory(@Param("commentId") long commentId,@Param("reporterId") long reporterId,@Param("reason") long reason);*/
	
	boolean existsByAdvisoryIdCommentAndAdvisoryIdAdvisoryReason(Comment c, AdvisoryReason ar);


}

