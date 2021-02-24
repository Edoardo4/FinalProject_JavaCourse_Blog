package it.course.myblogc3.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.AdvisoryReason;
import it.course.myblogc3.entity.AdvisoryReasonDetail;
import it.course.myblogc3.entity.AdvisoryReasonDetailId;
import it.course.myblogc3.payload.response.AdvisoryReasonResponse;

@Repository
public interface AdvisoryReasonDetailRepository extends JpaRepository<AdvisoryReasonDetail, AdvisoryReasonDetailId>{

	Optional<AdvisoryReasonDetail> findByAdvisoryReasonDetailIdAdvisoryReasonAndEndDateEquals(AdvisoryReason ar, Date endDate);

	@Query(value="SELECT new it.course.myblogc3.payload.response.AdvisoryReasonResponse("
				+ "ad.advisoryReasonDetailId.startDate, "
				+ "ad.endDate, "
				+ "ad.advisorySeverity.severityDescription, "		
				+ "ad.advisoryReasonDetailId.advisoryReason.id, "
				+ "ad.advisoryReasonDetailId.advisoryReason.advisoryReasonName "
				+ ") "
				+ "FROM AdvisoryReasonDetail ad "
				+ "WHERE ad.endDate = :endDate "
				+ "ORDER BY ad.advisorySeverity.severityValue desc"
				)
		List<AdvisoryReasonResponse> getAdvisoryReasonResponse(@Param("endDate")Date endDate);
	
	/*
	private Date startDate;	//AdvisoryReasonDetailId.startDate
	
	private Date endDate;
	private String levelDescription;
	private int levelGravity;
	
	private long advisoryReasonId; //AdvisoryReasonDetailId.AdvisoryReason.advisoryReasonId
	private String advisoryReasonName; //AdvisoryReasonDetailId.AdvisoryReason.advisoryReasonName*/
}
