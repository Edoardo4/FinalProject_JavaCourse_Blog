package it.course;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import it.course.myblogc3.entity.Comment;
import it.course.myblogc3.entity.Language;
import it.course.myblogc3.entity.Post;
import it.course.myblogc3.entity.Tag;
import it.course.myblogc3.entity.User;
import it.course.myblogc3.entity.Voting;
import it.course.myblogc3.entity.VotingId;
import it.course.myblogc3.repository.CommentRepository;
import it.course.myblogc3.repository.LanguageRespository;
import it.course.myblogc3.repository.PostRepository;
import it.course.myblogc3.repository.TagRepository;
import it.course.myblogc3.repository.TokenRepository;
import it.course.myblogc3.repository.UserRepository;
import it.course.myblogc3.repository.VotingRepository;

@EnableScheduling
@SpringBootApplication
public class Myblogc3Application {

	public static void main(String[] args) {
		SpringApplication.run(Myblogc3Application.class, args);
		
	}
	
	@Autowired
	PostRepository postRepository;
	@Autowired
	LanguageRespository languageRespository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	TagRepository tagRepository;
	@Autowired
	VotingRepository votingRepository;
	@Autowired
	TokenRepository tokenRepository;
	
	@Scheduled(cron="0 0 10 * * * ") //seconds,minutes,hours,day of month(1-31),day of week(0-6)
	@Transactional
	public void deleteOldTokens() {
		tokenRepository.deleteAllByExpiryDateLessThan(new Date());
	}
	
	//@EventListener(ApplicationReadyEvent.class) per farla partire anche in un controller, non per forza nel main
	@Bean
	@Transactional
	@Profile("dev")
	public void createPostAutomatically() {
		
		String titlePost = "title_";                    
		String contentComment = "content_";                    

		if(!postRepository.existsByTitleStartsWith(titlePost)) {
			
			List<User> authorEditor = userRepository.getUserEditor();
			List<User> authorReader = userRepository.getUserReader();

			List<Language> language = languageRespository.findByVisibleTrue();
			String content = "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";
			
			List<Tag> tags = tagRepository.findAllByVisibleTrue();
			
		    Random rand = new Random();
	
		    List<Post> posts = new ArrayList<Post>();
			List<Voting> voting = new ArrayList<Voting>();

			for(int i=0; i<10; i++) {
				Post p = new Post(
						titlePost+i,
						content,
						rand.nextBoolean(),
						authorEditor.get(rand.nextInt(authorEditor.size())),
						language.get(rand.nextInt(language.size()))
						);
				if(p.getVisible()) {
					//comment
				    List<Comment> comments = new ArrayList<Comment>();
					for(int x=0; x<5; x++) {
						Comment comment = new Comment(
								contentComment+x,
								p,
								authorReader.get(rand.nextInt(authorReader.size()))					
								);
						comments.add(comment);
					}
					//tag
					Set<Tag> tg = new HashSet<Tag>();
					int[] array = {1,2};
					int limit=array[rand.nextInt(array.length)];
					for(int j=0; j<limit; j++) {
						tg.add(tags.get(rand.nextInt(tags.size())));
					}
					//voting
					for(int k=0; k<authorReader.size(); k++) {
						voting.add(new Voting(
								new VotingId(p,authorReader.get(k)),
								(int)(Math.random() * 5)+1)
								);
					}
 					p.setComments(comments);
					p.setTags(tg);
				}
				posts.add(p);
			}
			postRepository.saveAll(posts);
			votingRepository.saveAll(voting);
		}
	}
}
