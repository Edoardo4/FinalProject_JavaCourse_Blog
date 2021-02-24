package it.course.myblogc3.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.course.myblogc3.entity.User;
import it.course.myblogc3.repository.UserRepository;

@Service
public class BanService {

	@Autowired
	UserRepository userRepository;
	
	public void updateBannedUntil(User u, int days) {
		
		u.setBannedUntil(LocalDateTime.now().plusDays(days));
		
	}
}
