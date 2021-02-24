package it.course.myblogc3.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.course.myblogc3.entity.Post;
import it.course.myblogc3.entity.PostCost;
import it.course.myblogc3.repository.PostCostRepository;

@Service
public class PostService {
	 
	@Autowired PostCostRepository postCostRepository;

	public int shiftCost(Post post) {
		
		List<PostCost> postCosts = postCostRepository.getPostsCostWhereStarDateBetween(LocalDate.now(), post.getId());
		int shiftCost=0;
		shiftCost = post.getCost() + postCosts.stream().mapToInt(p -> p.getShiftCost()).sum();
		
		return shiftCost;
	}
}
