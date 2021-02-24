package it.course.myblogc3.controller;

import java.time.Instant;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.Post;
import it.course.myblogc3.entity.PurchasedPostId;
import it.course.myblogc3.entity.User;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.repository.PostCostRepository;
import it.course.myblogc3.repository.PostRepository;
import it.course.myblogc3.repository.PurchasedPostRepository;
import it.course.myblogc3.repository.UserRepository;
import it.course.myblogc3.service.PostService;
import it.course.myblogc3.service.UserService;

@RestController
public class PurchasedPostController {

	@Autowired PurchasedPostRepository purchasedPostRepository;
	@Autowired UserService userService;
	@Autowired UserRepository userRepository;
	@Autowired PostRepository postRepository;
	@Autowired PostCostRepository postCostRepository;
	@Autowired PostService postService;
	
	@PostMapping("private/purchase-post/{postId}")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> purchasePost(@PathVariable long postId, HttpServletRequest request) {
		
		User user = userService.getAuthenticationUser();
		
		Optional<Post> post = postRepository.findByIdAndVisibleTrue(postId);
		if(!post.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 200,"OK", "Post not found",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		if(post.get().getCost() == 0) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 200,"OK", "Post is free",request.getRequestURI()
					), HttpStatus.OK);
				}

		if(purchasedPostRepository.existsById(new PurchasedPostId(post.get(), user)))
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 200,"OK", "you have already buy this post ",request.getRequestURI()
					), HttpStatus.OK);
		
		
		int shiftCost = postService.shiftCost(post.get());
		
		if(user.getCredits() >= shiftCost) {
			userService.purchased(user, post.get(), shiftCost);

			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 200,"OK", "Post purchased",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200,"OK", "you don't have enough credits to buy the post ",request.getRequestURI()
				), HttpStatus.OK);
		
	}
	
}
