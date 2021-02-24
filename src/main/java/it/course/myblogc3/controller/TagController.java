package it.course.myblogc3.controller;

import java.time.Instant;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.Tag;
import it.course.myblogc3.entity.User;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.repository.TagRepository;
import it.course.myblogc3.repository.UserRepository;
import it.course.myblogc3.service.UserService;

@RestController
public class TagController {

	@Autowired
	TagRepository tagRepository;
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	
	@PutMapping("private/change-tag-status/{tagName}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> changeTagStatus(@PathVariable String tagName, HttpServletRequest request) {

		Optional<Tag> t = tagRepository.findById(tagName);
		
		if(!t.isPresent())
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 200,	"OK", "Tag not found",	request.getRequestURI()
					), HttpStatus.OK);
		
		t.get().setVisible(!t.get().getVisible());
		tagRepository.save(t.get());
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200,	"OK", "Tag updated",	request.getRequestURI()
				), HttpStatus.OK);
	}
	
	
	@PostMapping("private/add-preferred-tag/{tagName}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> addPreferredPost(@PathVariable String tagName, HttpServletRequest request) {

		Optional<Tag> tag = tagRepository.findByTagNameAndVisibleTrue(tagName.toUpperCase());
		
		if(!tag.isPresent())
			return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200,"OK", "Tag not found",	request.getRequestURI()
				), HttpStatus.OK);
		
		User user = userService.getAuthenticationUser();
		
		if(user.getPreferredTags().contains(tag.get()))
			return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200,"OK", "Tag already present into your preferred",	request.getRequestURI()
				), HttpStatus.OK);

		
		user.getPreferredTags().add(tag.get());
		
		return new ResponseEntity<ApiResponseCustom>(
			new ApiResponseCustom(Instant.now(), 200,"OK", "Tag has been added into your preferred",request.getRequestURI()
			), HttpStatus.OK);
	}
	
	
}
