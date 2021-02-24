package it.course.myblogc3.controller;

import java.time.Instant;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblogc3.entity.Language;
import it.course.myblogc3.payload.request.LanguageRequest;
import it.course.myblogc3.payload.response.ApiResponseCustom;
import it.course.myblogc3.repository.CommentRepository;
import it.course.myblogc3.repository.LanguageRespository;
import it.course.myblogc3.repository.PostRepository;
import it.course.myblogc3.repository.TagRepository;

@RestController
@Validated
public class LanguageController {

	@Autowired
	PostRepository postRepository;
	@Autowired
	TagRepository tagRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	LanguageRespository languageRespository;
	
	@PostMapping("private/create-lang")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> createLang(@Valid @RequestBody LanguageRequest languageRequest, HttpServletRequest request){
		
		
		if(languageRespository.existsByLangCodeOrLangDesc(languageRequest.getLangCode(),languageRequest.getLangDesc())) {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","lang code or lang desc already present",request.getRequestURI()
					), HttpStatus.OK);
		}
		
		Language l = new Language(languageRequest.getLangCode().toUpperCase(), languageRequest.getLangDesc().toUpperCase());

		languageRespository.save(l);
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","language has been added "+l.toString(),request.getRequestURI()
				), HttpStatus.OK);
	}
	
	@PutMapping("private/update-lang")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateLang(@Valid @RequestBody LanguageRequest languageRequest, HttpServletRequest request){
		
		Optional<Language> l = languageRespository.findById(languageRequest.getLangCode());
		if(!l.isPresent()) {
			
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","No language found",request.getRequestURI()
					), HttpStatus.OK);
			
		}
		
		l.get().setLangDesc(languageRequest.getLangDesc().toUpperCase());
		languageRespository.save(l.get());
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(),200,"OK","Language "+languageRequest.getLangCode()+" successfully updated. ",
					request.getRequestURI()
				), HttpStatus.OK);
	}
	
	
	@PutMapping("private/change-status-lang/{langCode}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateStatusLang(@Valid @PathVariable("langCode") @NotBlank @Size(min=2, max=2) String langCode ,HttpServletRequest request){

		
		Optional<Language> l = languageRespository.findById(langCode);
		
		if(!l.isPresent()) {
					
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(),200,"OK","No language found",request.getRequestURI()
					), HttpStatus.OK);
			
		}
		
		l.get().setVisible(!l.get().getVisible());
		
		languageRespository.save(l.get());
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(
					Instant.now(),200,"OK","Language change status ",request.getRequestURI()
				), HttpStatus.OK);
	}
}
