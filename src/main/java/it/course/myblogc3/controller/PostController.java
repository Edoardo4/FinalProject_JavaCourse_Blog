package it.course.myblogc3.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import it.course.myblogc3.entity.Language;
import it.course.myblogc3.entity.Post;
import it.course.myblogc3.entity.PostCost;
import it.course.myblogc3.entity.PostCostId;
import it.course.myblogc3.entity.Tag;
import it.course.myblogc3.entity.User;
import it.course.myblogc3.payload.request.PostRequest;
import it.course.myblogc3.payload.request.ShiftCostRequest;
import it.course.myblogc3.payload.request.TagsNameRequest;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.payload.response.CommentResponseForPost;
import it.course.myblogc3.payload.response.PostDetailResponse;
import it.course.myblogc3.payload.response.PostResponse;
import it.course.myblogc3.payload.response.PostResponseForSearch;
import it.course.myblogc3.payload.response.PostResponseWithPreferredTags;
import it.course.myblogc3.repository.CommentRepository;
import it.course.myblogc3.repository.LanguageRespository;
import it.course.myblogc3.repository.PostCostRepository;
import it.course.myblogc3.repository.PostRepository;
import it.course.myblogc3.repository.TagRepository;
import it.course.myblogc3.repository.UserRepository;
import it.course.myblogc3.service.UserService;

@RestController
@Validated
public class PostController {

	@Autowired
	PostRepository postRepository;
	@Autowired
	TagRepository tagRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	LanguageRespository languageRespository;
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PostCostRepository postCostRepository;
	
	@PostMapping("private/create-post")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> createPost(@Valid @RequestBody PostRequest postRequest, HttpServletRequest request) {
		
		if(postRepository.existsByTitle(postRequest.getTitle())) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","This post title already exist",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		}
		
		Optional<Language> l = languageRespository.findByLangCodeAndVisibleTrue(postRequest.getLanguage());
		
		if(!l.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","language not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		}
		
		User u = userService.getAuthenticationUser();
		
		Post post = new Post(
				postRequest.getTitle(),
				postRequest.getContent(),
				u,
				l.get()
				);
		
		Set <Tag> tags = tagRepository.findByTagNameInAndVisibleTrue(postRequest.getTagNames()); 	
		if(!tags.isEmpty()) {
			
			post.setTags(tags);
		}
		
		postRepository.save(post);
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","new posts added",request.getRequestURI()
				), HttpStatus.NOT_FOUND);
	}

	@PutMapping("private/change-post-visible/{postId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> changeStatusPost(@PathVariable Long postId, HttpServletRequest request) {

		boolean exists = postRepository.existsById(postId);
		
		if(exists == false) {
					
					return new ResponseEntity<ApiResponseCustom>(
							new ApiResponseCustom(
									Instant.now(),200,"OK","Post not found",request.getRequestURI()
							), HttpStatus.OK);
		}
	     
		postRepository.changeStatusPost(postId);
		
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(
						Instant.now(),200,"OK","post status changed",request.getRequestURI()
					), HttpStatus.OK);
	}
	
	public ResponseEntity<ApiResponseCustom> getVisibilePosts(HttpServletRequest request) {
	
		List<PostResponse> posts = postRepository.getPostsVisible();
		
		
		if(posts.isEmpty()) {
			
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","posts not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<ApiResponseCustom>( 
				new ApiResponseCustom(Instant.now(), 200, "OK", posts, request.getRequestURI()), HttpStatus.OK);
		
	}
	
	//add average
	@GetMapping("public/get-visibile-post/{postId}")
	public ResponseEntity<ApiResponseCustom> getVisibilePostPublic(@Valid @PathVariable Long postId, HttpServletRequest request) {

		PostDetailResponse postDetailResponse = postRepository.getPostVisiblePublic(postId);
		
		if(postDetailResponse.getId() == null) {
			
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","post not found",request.getRequestURI()
					), HttpStatus.OK);
			
		}
		
		Set<Tag> tags = tagRepository.getTagsInPost(postId);
		postDetailResponse.setTagNames(tags.stream().map(Tag::getTagName).collect(Collectors.toSet()));
		
		List<CommentResponseForPost> commentResponseForPost = commentRepository.getCommentResponseForPost(postId);
		postDetailResponse.setCommentResponseForPost(commentResponseForPost);
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200, "OK", postDetailResponse, request.getRequestURI()), HttpStatus.OK);
		
		}
	
	@GetMapping("private/get-visibile-post-private/{postId}")
	public ResponseEntity<ApiResponseCustom> getVisibilePostPrivate(@Valid @PathVariable Long postId, HttpServletRequest request) {

		PostDetailResponse postDetailResponse = postRepository.getPostDetailPrivate(postId,userService.getAuthenticationUser().getId());
		
		if(postDetailResponse.getId() == null) {
			
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","post not found",request.getRequestURI()
					), HttpStatus.OK);
			
		}
		
		Set<Tag> tags = tagRepository.getTagsInPost(postId);
		postDetailResponse.setTagNames(tags.stream().map(Tag::getTagName).collect(Collectors.toSet()));
		
		List<CommentResponseForPost> commentResponseForPost = commentRepository.getCommentResponseForPost(postId);
		postDetailResponse.setCommentResponseForPost(commentResponseForPost);
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200, "OK", postDetailResponse, request.getRequestURI()), HttpStatus.OK);
		
		}
		
	@DeleteMapping("private/delete-post/{postId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> deletePost(@Valid @PathVariable Long postId, HttpServletRequest request) {

		postRepository.deletePost(postId);

			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","post deleted",request.getRequestURI()
					), HttpStatus.OK);
	}
	
	@GetMapping("public/get-visibile-posts-by-tags")
	public ResponseEntity<ApiResponseCustom> getVisibilePostsByTags(@RequestBody TagsNameRequest tagsNameRequest, HttpServletRequest request) {
		
		List<PostResponse> pr = new ArrayList<PostResponse>();
		
		Set<Tag> tags = tagRepository.findByTagNameInAndVisibleTrue(tagsNameRequest.getTagNames());

		if(tags.isEmpty()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","tags not found",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		Set<Long> idPosts = new HashSet<Long>();
		
		for(Tag t :tags) {
			idPosts.addAll(postRepository.getIdPosts(t.getTagName()));
		}
			
		if(idPosts.isEmpty()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","no posts whit tags: "+tagsNameRequest.getTagNames(),request.getRequestURI()
					), HttpStatus.OK);
		}
		
		for(Long i : idPosts) {
			pr.add(postRepository.getPostVisiblePostId(i));
		}

		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",pr,request.getRequestURI()
				), HttpStatus.OK);
	}
	
	/*soluzione Adelchi
	 @GetMapping("public/get-visibile-posts-by-tags")
	public ResponseEntity<ApiResponseCustom> getVisiblePostsByTags(@RequestBody TagNamesRequest tagNamesRequest, HttpServletRequest request) {
		
		List<PostResponse> ps = postRepository.getPostsVisibleByTagNames(tagNamesRequest.getTagNames());
		if(ps.isEmpty())
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200,"OK",
				"No posts found with tags "+tagNamesRequest.getTagNames(), request.getRequestURI()),HttpStatus.OK);
	
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, 
				"OK", ps, request.getRequestURI()), HttpStatus.OK);
	}*/
	 
	
	@GetMapping("public/get-visibile-posts-by-lang/{langCode}")
	public ResponseEntity<ApiResponseCustom> getVisibilePostsByLang(@Valid @PathVariable("langCode") @NotBlank @Size(min=2, max=2) String langCode, HttpServletRequest request) {
		
		Optional<Language> l = languageRespository.findById(langCode);
		
		if(!l.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","language not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		}
		
		
		List<PostResponse> postsResponse = postRepository.getPostsVisibleByLang(langCode);
		
		if(postsResponse.isEmpty()) {	
				return new ResponseEntity<ApiResponseCustom>(
						new ApiResponseCustom(Instant.now(),200,"OK","posts whit langauge: "+l.get().toString() +" not found",request.getRequestURI()
						), HttpStatus.NOT_FOUND);
			}
				
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",postsResponse,request.getRequestURI()
				), HttpStatus.OK);
	
	}
	
	/*ricerca: titolo e contenuto
	caseSensitive exactMatch
	caseSensitive !exactMatch
	!caseSensitive exactMatch
	!caseSensitive !exactMatch
	*/
	@GetMapping("public/get-visibile-posts-by-keyword-sql")
	public ResponseEntity<ApiResponseCustom> getVisibilePostsByKeyWordsSql(@RequestParam boolean caseSensitive,@RequestParam boolean exactMatch,@RequestParam String keyWord,
			HttpServletRequest request) {

		String keyWordExact = "\\b".concat(keyWord.concat("\\b"));
		 
		List<Post> posts =new ArrayList<Post>();
		List<PostResponseForSearch> postsResForSer= new ArrayList<PostResponseForSearch>();
		
		if(caseSensitive == true && exactMatch==true) {
			posts = postRepository.getPostsVisibleBySearchCaseSensitiveTrue(keyWordExact);
		}
		else if(caseSensitive==true &&  exactMatch==false){
			posts = postRepository.getPostsVisibleBySearchCaseSensitiveTrue(keyWord);
		}
		else if(caseSensitive==false &&  exactMatch==true){
			posts = postRepository.getPostsVisibleBySearchCaseSensitiveFalse(keyWordExact);
		}
		else if(caseSensitive==false &&  exactMatch==false){
			posts = postRepository.getPostsVisibleBySearchCaseSensitiveFalse(keyWord);
		}
		
		if(posts.isEmpty()) 
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),404,"OK","no posts found for keyword: "+keyWord,request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		
		postsResForSer=posts.stream().map(post->new PostResponseForSearch(
				post.getId(),
				post.getTitle(),
				post.getContent(),
				userRepository.getAuthorUsernameByPostId(post.getId()),
				post.getUpdatedAt()
				)).collect(Collectors.toList());	
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",postsResForSer,request.getRequestURI()
				), HttpStatus.OK);
	}
	
	@GetMapping("public/get-visibile-posts-by-keyword-java")
	public ResponseEntity<ApiResponseCustom> getVisibilePostsByKeyWordsJava(@RequestParam boolean caseSensitive,@RequestParam boolean exactMatch,@RequestParam String keyWord,
			HttpServletRequest request) {
		
		List<PostResponseForSearch> posts = postRepository.getPostResponseForSearch();
		List<PostResponseForSearch> newPosts = new ArrayList<PostResponseForSearch>();
		
		String keyWordExact = "\\b".concat(keyWord.concat("\\b"));
		
		Pattern pattern;
		Matcher mContent;
		Matcher mTitle;
	 
		if(caseSensitive == true && exactMatch==true) {
			for(PostResponseForSearch p : posts) {
				pattern = Pattern.compile(keyWordExact);
				 mContent = pattern.matcher(p.getContent());
				 mTitle = pattern.matcher(p.getTitle());
	
				 if(mContent.find() == true || mTitle.find() == true) {
					 newPosts.add(p);
				 }
			}
		}
		else if(caseSensitive==true && exactMatch==false){
			for(PostResponseForSearch p : posts) {
				pattern = Pattern.compile(keyWord);
				 mContent = pattern.matcher(p.getContent());
				 mTitle = pattern.matcher(p.getTitle());
	
				 if(mContent.find() == true || mTitle.find() == true) {
					 newPosts.add(p);
				 }
			}
		}
		else if(caseSensitive==false && exactMatch==true){
			for(PostResponseForSearch p : posts) {
				pattern = Pattern.compile(keyWordExact,Pattern.CASE_INSENSITIVE);
				 mContent = pattern.matcher(p.getContent());
				 mTitle = pattern.matcher(p.getTitle());
	
				 if(mContent.find() == true || mTitle.find() == true) {
					 newPosts.add(p);
				 }
			}
		}
		else if(caseSensitive==false && exactMatch==false){
			for(PostResponseForSearch p : posts) {
				pattern = Pattern.compile(keyWord,Pattern.CASE_INSENSITIVE);
				 mContent = pattern.matcher(p.getContent());
				 mTitle = pattern.matcher(p.getTitle());
	
				 if(mContent.find() == true || mTitle.find() == true) {
					 newPosts.add(p);
				 }
			}
		}
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",newPosts,request.getRequestURI()
				), HttpStatus.OK);
	}
	
	@GetMapping("private/get-visibile-posts-by-preferred-tag")
	public ResponseEntity<ApiResponseCustom> getVisibilePostsByPreferredTag(HttpServletRequest request) {
		
		User user = userService.getAuthenticationUser();
		
		Set<String> tagNames = tagRepository.getTagNameByPreferredTagUserId(user.getId());

		if(tagNames.isEmpty()) 
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),404,"OK","no preferred tags found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);	
				
		List<PostResponseWithPreferredTags> posts = new ArrayList<PostResponseWithPreferredTags>();
				
		posts = postRepository.getPostsVisibleByPreferredTag(tagNames);
		
		posts.forEach(p -> p.setTagNames(tagRepository.getTagNameByPostId(p.getId())));
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK",posts,request.getRequestURI()
				), HttpStatus.OK);
	}
	
	//ADMIN
	//update cost
	@PutMapping("private/update-post-code/{postId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> updatePostCode(@Valid @PathVariable Long postId, @Valid @PathVariable int cost,HttpServletRequest request) {

		Optional<Post> post = postRepository.findById(postId);
		
		if(!post.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","post not found",request.getRequestURI()
					), HttpStatus.OK);
		}

		if(post.get().getCost() != 0)
			return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","the post cost must be 0",request.getRequestURI()
				), HttpStatus.OK);
		
		post.get().setCost(cost);
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","post cost updated",request.getRequestURI()
				), HttpStatus.OK);
	}
	
	//insert shiftCost  
	@PostMapping("private/insert-shiftCost")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> insertShiftCost(@RequestBody ShiftCostRequest shiftCostRequest, HttpServletRequest request) {
		/*                       costo iniziale :5
		 * intervallo-A    1 |------------------------(-2)-------------------- --|30
           intervallo-2             7|----------------(-2)------------------|20
           intervallo-B                 10 |-----------(+2)---------|15
           intervallo-4                      13|---------(-2)------------|17
		*/
		
		Optional<Post> post = postRepository.findById(shiftCostRequest.getPostId());
		if(!post.isPresent())
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"404","post not found",request.getRequestURI()
					), HttpStatus.NOT_FOUND);
		
		
		Optional<PostCost> pc = postCostRepository.findById(new PostCostId(post.get(), shiftCostRequest.getStartDate(), shiftCostRequest.getEndDate()));
		if(pc.isPresent())
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"404","There is already a promotion for that post, which starts and ends on the same dates",request.getRequestURI()
					), HttpStatus.NOT_FOUND);		
					
		List<PostCost> postCosts = postCostRepository.getPostsCostWhereStarDateBetween(shiftCostRequest.getStartDate(), post.get().getId());
		
		int shiftCost=0;
		
		shiftCost = postCosts.stream().mapToInt(p -> p.getShiftCost()).sum();
	
		
		if(LocalDate.now().isAfter(shiftCostRequest.getEndDate()))
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"404","end Date not valid",request.getRequestURI()
					), HttpStatus.NOT_FOUND);	
		
		
		if(shiftCostRequest.getStartDate().isAfter(shiftCostRequest.getEndDate()))
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"404","end Date and start date not valid",request.getRequestURI()
					), HttpStatus.NOT_FOUND);	
		
		
		shiftCost += shiftCostRequest.getShiftCost();
		
		if(shiftCost < 0) {
			if(post.get().getCost() - (Math.abs(shiftCost))  <= 0) 
				return new ResponseEntity<ApiResponseCustom>(
						new ApiResponseCustom(Instant.now(),200,"404","the cost of the post has reached 0",request.getRequestURI()
						), HttpStatus.NOT_FOUND);
		}
			
		PostCost postCost = new PostCost(
				new PostCostId(post.get(),shiftCostRequest.getStartDate(), shiftCostRequest.getEndDate() ), shiftCostRequest.getShiftCost()
				);
		postCostRepository.save(postCost);
		
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","shift cost added",request.getRequestURI()
				), HttpStatus.OK);
	}
	
}
