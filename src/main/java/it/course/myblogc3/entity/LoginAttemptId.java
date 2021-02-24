package it.course.myblogc3.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor
public class LoginAttemptId implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@OneToOne
	private User user;
}
