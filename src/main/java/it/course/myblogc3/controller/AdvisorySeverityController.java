package it.course.myblogc3.controller;

import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.AdvisorySeverity;
import it.course.myblogc3.payload.request.AdvisorySeverityRequest;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.repository.AdvisoryReasonDetailRepository;
import it.course.myblogc3.repository.AdvisoryReasonRepository;
import it.course.myblogc3.repository.AdvisorySeverityRepository;

@RestController
public class AdvisorySeverityController {

	@Autowired AdvisoryReasonDetailRepository advisoryReasonDetailRepository;
	@Autowired AdvisoryReasonRepository advisoryReasonRepository;
	@Autowired AdvisorySeverityRepository advisorySeverityRepository;

	@PostMapping("private/create-advisory-severity")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> createAdvisorySeverity(@Valid @RequestBody AdvisorySeverityRequest advisorySeverityRequest,HttpServletRequest request) {
	
		if(advisorySeverityRepository.existsById(advisorySeverityRequest.getSeverityDescription()))
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","advisory severity description already exist",request.getRequestURI()
					), HttpStatus.OK);
		
		AdvisorySeverity ad = new AdvisorySeverity(
				advisorySeverityRequest.getSeverityDescription().toUpperCase(),
				advisorySeverityRequest.getSeverityValue()
				);
		
		advisorySeverityRepository.save(ad);
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","advisory severity created",request.getRequestURI()
				), HttpStatus.OK);
	}
	
	@GetMapping("private/get-advisory-severity")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> geAdvisorySeverity(HttpServletRequest request) {
	
		List<AdvisorySeverity> ad = advisorySeverityRepository.findAllByOrderBySeverityValueDesc();
	
		if(ad.isEmpty())
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","advisory severity not found",request.getRequestURI()
					), HttpStatus.OK);
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",ad,request.getRequestURI()
				), HttpStatus.OK);
	}
}
