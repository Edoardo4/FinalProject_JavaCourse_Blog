package it.course.myblogc3.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.AdvisoryReason;
import it.course.myblogc3.entity.AdvisoryReasonDetail;
import it.course.myblogc3.entity.AdvisoryReasonDetailId;
import it.course.myblogc3.entity.AdvisorySeverity;
import it.course.myblogc3.payload.request.AdvisoryReasonRequest;
import it.course.myblogc3.payload.response.AdvisoryReasonResponse;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.repository.AdvisoryReasonDetailRepository;
import it.course.myblogc3.repository.AdvisoryReasonRepository;
import it.course.myblogc3.repository.AdvisorySeverityRepository;
import it.course.myblogc3.service.AdvisoryService;
 
@RestController
public class AdvisoryReasonController {

	@Autowired AdvisoryReasonDetailRepository advisoryReasonDetailRepository;
	@Autowired AdvisoryReasonRepository advisoryReasonRepository;
	@Autowired AdvisorySeverityRepository advisorySeverityRepository;
	@Autowired AdvisoryService advisoryService;
	
	@PostMapping("private/create-advisory-reason")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> createAdvisoryReason(@Valid @RequestBody AdvisoryReasonRequest advisoryReasonRequest, HttpServletRequest request){
		
		Optional<AdvisoryReason> ar = advisoryReasonRepository.findByAdvisoryReasonName(advisoryReasonRequest.getAdvisoryReasonName());
		
		Optional<AdvisorySeverity> as = advisorySeverityRepository.findById(advisoryReasonRequest.getSeverityDescription());
		if(!as.isPresent())
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),404,"NOT FOUND","No severity found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		
		AdvisoryReason arNew = new AdvisoryReason();
		
		if(ar.isPresent()) {
			Optional<AdvisoryReasonDetail> ard = advisoryReasonDetailRepository.findByAdvisoryReasonDetailIdAdvisoryReasonAndEndDateEquals(ar.get(),advisoryService.fromCalendarToDate());
			ard.get().setEndDate(advisoryReasonRequest.getStartDate());
			AdvisoryReasonDetail ardNew = new AdvisoryReasonDetail(
					new AdvisoryReasonDetailId (ar.get(), advisoryReasonRequest.getStartDate()),advisoryService.fromCalendarToDate(),
					as.get()
					);
			advisoryReasonDetailRepository.save(ardNew);
		} else {
			arNew.setAdvisoryReasonName(advisoryReasonRequest.getAdvisoryReasonName());
			advisoryReasonRepository.save(arNew);
			AdvisoryReasonDetail ardNew = new AdvisoryReasonDetail(
					new AdvisoryReasonDetailId (arNew, advisoryReasonRequest.getStartDate()),advisoryService.fromCalendarToDate(),
					as.get()
					);
			advisoryReasonDetailRepository.save(ardNew);
		}
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","new advisoryReason added",request.getRequestURI()
				), HttpStatus.OK);
		
	}

	@PutMapping("private/change-advisory-reason-name")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> changeAdvisoryReasonName(@Valid @RequestParam String oldReason, String newReason, HttpServletRequest request) {
	
		Optional<AdvisoryReason> advisoryReasonOld = advisoryReasonRepository.findByAdvisoryReasonName(oldReason);

		if(!advisoryReasonOld.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(	Instant.now(),200,"OK","advisoryReason not found ",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		Optional<AdvisoryReason> advisoryReasonNew = advisoryReasonRepository.findByAdvisoryReasonName(newReason);
		
		if(advisoryReasonNew.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(	Instant.now(),200,"OK","advisoryReason name already in use ",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		advisoryReasonOld.get().setAdvisoryReasonName(newReason);
		       
		advisoryReasonRepository.save(advisoryReasonOld.get());
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(	Instant.now(),200,"OK","advisoryReason name changed ",request.getRequestURI()
				), HttpStatus.OK);
	
		}
	
	@GetMapping("public/get-advisoryReasonResponse")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getAdvisoryReasonResponse(HttpServletRequest request) {
	
	           
		List<AdvisoryReasonResponse> advisoryReasonResponse = advisoryReasonDetailRepository.getAdvisoryReasonResponse(advisoryService.fromCalendarToDate());
    
		if(advisoryReasonResponse.isEmpty()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(	Instant.now(),200,"OK","advisoryReasonResponse not found ",request.getRequestURI()
					), HttpStatus.OK);
		}
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",advisoryReasonResponse,request.getRequestURI()
				), HttpStatus.OK);
	}
	
}
