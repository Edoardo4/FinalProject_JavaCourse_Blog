package it.course.myblogc3.controller;

import java.time.Instant;


import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.Comment;
import it.course.myblogc3.entity.Post;
import it.course.myblogc3.entity.User;
import it.course.myblogc3.payload.request.CommentRequest;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.payload.response.CommentResponse;
import it.course.myblogc3.repository.CommentRepository;
import it.course.myblogc3.repository.PostRepository;
import it.course.myblogc3.service.UserService;

@RestController
public class CommentController {

	@Autowired
	PostRepository postRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	UserService userService;
	
	@PostMapping("private/create-comment/{postId}")
	@PreAuthorize("hasRole('READER') or hasRole('EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> createComment(@Valid @PathVariable Long postId,@Valid @RequestBody CommentRequest commentRequest, HttpServletRequest request) {

		Boolean post = postRepository.existsByIdAndVisibleTrue(postId);
		
		if(post==false) {
			
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","post not found",request.getRequestURI()
					), HttpStatus.OK);
		}

		User loggedUser = userService.getAuthenticationUser();

		Comment c = new Comment(commentRequest.getComment(), new Post(postId), loggedUser);
		commentRepository.save(c);
		
		//new Post(postId).getComments().add(c);
		
		//postRepository.save(new Post(postId));
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(	Instant.now(),200,	"OK","new comment has been added to post "+postId,request.getRequestURI()
				), HttpStatus.OK);		
	}
	
	/*
	 * get-comment/commentId = attributi del comment+username+titolo post a cui è associato*/
	@GetMapping("public/get-comment/{commentId}")
	public ResponseEntity<ApiResponseCustom> getCommentResponse(@Valid @PathVariable Long commentId, HttpServletRequest request) {
	
	
		CommentResponse commentResponse = commentRepository.getCommentResponse(commentId);

		if(commentResponse == null) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(	Instant.now(),200,"OK","comment not found ",request.getRequestURI()
					), HttpStatus.OK);
		}
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",commentResponse,request.getRequestURI()
				), HttpStatus.OK);
	}
		
	@DeleteMapping("private/delete-comment/{commentId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> deleteComment(@Valid @PathVariable Long commentId, HttpServletRequest request) {

		commentRepository.deleteComment(commentId);

		 
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","comment deleted",request.getRequestURI()
					), HttpStatus.OK);
	}

}
