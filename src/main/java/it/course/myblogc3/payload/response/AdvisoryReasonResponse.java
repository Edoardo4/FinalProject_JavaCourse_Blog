package it.course.myblogc3.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryReasonResponse {

	private Date startDate;	//AdvisoryReasonDetailId.startDate
	
	private Date endDate;
	private String severityDescription;

	
	private long advisoryReasonId; //AdvisoryReasonDetailId.AdvisoryReason.advisoryReasonId
	private String advisoryReasonName; //AdvisoryReasonDetailId.AdvisoryReason.advisoryReasonName
}
