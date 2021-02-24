package it.course.myblogc3.payload.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ShiftCostRequest {

	private Long postId;

	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private int shiftCost;

}
