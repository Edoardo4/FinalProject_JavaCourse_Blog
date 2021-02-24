package it.course.myblogc3.payload.response;

import java.util.Date;

import it.course.myblogc3.entity.AdvisoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryResponse {

	private Date createdAt;	//AdvisoryReasonDetailId.startDate
	private Date updatedAt;
	
	private String severityDescription;
	private int serverityValue;
	
	private long advisoryReasonId; //AdvisoryReasonDetailId.AdvisoryReason.advisoryReasonId
	private String advisoryReasonName; //AdvisoryReasonDetailId.AdvisoryReason.advisoryReasonName
	
	//private String status;
	private AdvisoryStatus status;
	
	private String authorAdvisory;
	private String authorComment;
	
	private long commentId;
}
