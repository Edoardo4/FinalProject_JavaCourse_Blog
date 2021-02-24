package it.course.myblogc3.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.Advisory;
import it.course.myblogc3.entity.AdvisoryId;
import it.course.myblogc3.entity.AdvisoryReason;
import it.course.myblogc3.entity.AdvisoryStatus;
import it.course.myblogc3.entity.Comment;
import it.course.myblogc3.entity.User;
import it.course.myblogc3.payload.request.AdvisoryRequest;
import it.course.myblogc3.payload.request.AdvisoryStatusChangeRequest;
import it.course.myblogc3.payload.response.AdvisoryResponse;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.repository.AdvisoryReasonRepository;
import it.course.myblogc3.repository.AdvisoryRepository;
import it.course.myblogc3.repository.CommentRepository;
import it.course.myblogc3.repository.UserRepository;
import it.course.myblogc3.service.BanService;
import it.course.myblogc3.service.UserService;

@RestController
public class AdvisoryController {

	@Autowired CommentRepository commentRepository;
	@Autowired UserService userService;
	@Autowired AdvisoryRepository advisoryRepository;
	@Autowired AdvisoryReasonRepository advisoryReasonRepository;
	@Autowired UserRepository userRepository;
	@Autowired BanService banService;
	
	@PostMapping("private/create-advisory")
	@PreAuthorize("hasRole('READER') or hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> createAdvisory(@Valid @RequestBody AdvisoryRequest advisoryRequest, HttpServletRequest request) {
		//uno user non può segnalare un proprio commento
		
		User user = userService.getAuthenticationUser();
		
		Optional<Comment> c = commentRepository.findByIdAndVisibleTrue(advisoryRequest.getCommentId());
		if(!c.isPresent()) 				
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),404,"NOT_FOUND","comment not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		
		if(user.getId().equals(c.get().getCommentAuthor().getId())) 
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),401,"OK","the comment author cannot report the comment",request.getRequestURI()
					), HttpStatus.FORBIDDEN);
		
		            
		Optional<AdvisoryReason> adr = advisoryReasonRepository.findByAdvisoryReasonName(advisoryRequest.getReason());
		if(!adr.isPresent()) 				
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),404,"NOT_FOUND","advisory reason not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		 
		AdvisoryId adId = new AdvisoryId(c.get(),user, adr.get());
		
		boolean exists = advisoryRepository.existsByAdvisoryIdCommentAndAdvisoryIdAdvisoryReason(c.get(),adr.get());
		if(exists) 
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","reporting the comment for this reason existing",request.getRequestURI()
					), HttpStatus.OK);
		
		Advisory ad = new Advisory(
				adId,
				AdvisoryStatus.OPEN,
				advisoryRequest.getDescription()
				);
		advisoryRepository.save(ad);
		
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","new advisory added",request.getRequestURI()
					), HttpStatus.OK);
	}
	
	@PutMapping("private/change-staus-advisory")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> changeStatusPost(@Valid @RequestBody AdvisoryStatusChangeRequest advisoryStatusChangeRequest, HttpServletRequest request) {

		Advisory a = advisoryRepository.existAdvisoryById(advisoryStatusChangeRequest.getUserId(),advisoryStatusChangeRequest.getCommentId(),advisoryStatusChangeRequest.getAdvisory_reason_id());
		        
		if(a == null) {			
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(
							Instant.now(),404,"NOT_FOUND","Advisory not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		}
	     
		if(a.getStatus().ordinal() == 2 ||  a.getStatus().ordinal() == 3) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(
							Instant.now(),404,"NOT_FOUND","Advisory already closed",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		}
		
		a.setStatus(AdvisoryStatus.valueOf(advisoryStatusChangeRequest.getStatus()));
		advisoryRepository.save(a);
		
		if(advisoryStatusChangeRequest.getStatus().equals(AdvisoryStatus.valueOf("CLOSED_WHIT_CONSEQUENCE").toString())) {
			commentRepository.updateVisibleComment(advisoryStatusChangeRequest.getCommentId());		
		
			//recuperare la gravità
			int days = advisoryRepository.getSeverityValueFromAdvisory(advisoryStatusChangeRequest.getUserId(),advisoryStatusChangeRequest.getCommentId(),advisoryStatusChangeRequest.getAdvisory_reason_id());
	
			
			User authorOfComment  = userRepository.getcommentAuthorFromCommentId(advisoryStatusChangeRequest.getCommentId());
			
			//aggoirnare bannedUntil
			if(authorOfComment.getBannedUntil() == null)
				banService.updateBannedUntil(authorOfComment, days);
			else
				authorOfComment.setBannedUntil(authorOfComment.getBannedUntil().plusDays(days));
			
			authorOfComment.setEnabled(false);
			
			userRepository.save(authorOfComment);

		}

		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(
					Instant.now(),200,"OK","advisory status changed",request.getRequestURI()
				), HttpStatus.OK);
	}
	
	@GetMapping("public/get-open-advisory")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getOpenAdvisory(HttpServletRequest request) {
	
	
		List<AdvisoryResponse> advisoryResponse = advisoryRepository.getOpenAdvisories();

		if(advisoryResponse.isEmpty()){
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(	Instant.now(),200,"OK","advisoryResponse not found ",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",advisoryResponse,request.getRequestURI()
				), HttpStatus.OK);
	}
	
}
