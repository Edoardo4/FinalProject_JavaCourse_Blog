package it.course.myblogc3.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryReasonNameRequest {
	
	 private long id;
	
	 @NotBlank @NotNull @Size(max=15)
	 private String advisoryReasonNameNew;
	 
	 
	 private String advisoryReasonNameOld;
}
