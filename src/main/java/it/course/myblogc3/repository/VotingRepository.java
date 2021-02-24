package it.course.myblogc3.repository;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblogc3.entity.Voting;
import it.course.myblogc3.entity.VotingId;

@Repository
public interface VotingRepository  extends JpaRepository<Voting, VotingId>{

	@Query(value="SELECT AVG(vote) FROM voting where post_id=:id", nativeQuery=true)
	Double postAvg(@Param("id") long id);
	
	
	@Transactional
	@Modifying
	@Query(value="INSERT INTO voting "
			+ "(created_at, vote, voter, post_id) "
			+ "VALUES (CURRENT_TIMESTAMP, :vote, :voter, :postId)", nativeQuery=true
			)
	void insertVote(@Param("vote") int vote, @Param("voter") long voter, @Param("postId") long postId);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE voting "
			+ "SET vote=:vote "
			+ "WHERE voter=:voter AND post_id=:postId ", nativeQuery=true
			)
	void updateVote(@Param("vote") int vote, @Param("voter") long voter, @Param("postId") long postId);
	
	/*@Query(value="select new it.course.myblog.payload.response.PostResponseWhitVoting"
			+ "("
			+ "p.id, "
			+ "p.title, "
			+ "p.content, "
			+ "p.language.langCode, "
			+ "p.author.username, "
			+ "p.dbFile.fileName, "
			+ "p.updatedAt, "
			+ "COUNT(v.vote), "
			+ "COALESCE(avg(v.vote), 0.00)"
			+ ")"
			+ "from  Voting v "
			+ "right outer join "
			+ "Post p "
			+ "on p.id = v.votingId.post.id "
			+ "where p.visible=true "
			+"group by p.id "
			+ "order by p.updatedAt desc "
			+ "")
	List<PostResponseWhitVoting> findPostCoutAndAvg();
	
	@Query(value="select new it.course.myblog.payload.response.PostResponseWhitVoting"
			+ "("
			+ "p.id, "
			+ "p.title, "
			+ "p.content, "
			+ "p.language.langCode, "
			+ "p.author.username, "
			+ "p.dbFile.fileName, "
			+ "p.updatedAt, "
			+ "COUNT(v.vote), "
			+ "COALESCE(avg(v.vote), 0.00)"
			+ ")"
			+ "from  Voting v "
			+ "right outer join "
			+ "Post p "
			+ "on p.id = v.votingId.post.id "
			+ "where p.visible=true and p.author.id = :id "
			+"group by p.id "
			+ "order by p.updatedAt desc "
			+ "")
	List<PostResponseWhitVoting> findPostCoutAndAvgByAuthor(@Param("id") Long id);
	/*
	private Long id;
	private String username;
	private String email;
	private long numberOfVotes;
	private double average;
	 * *//*
	@Query(value="select new it.course.myblog.payload.response.AuhtorResponse"
			+"("
			+"u.id, "
			+"u.username, "
			+"u.email, "
			+"COUNT(v.vote), "
			+"COALESCE(avg(v.vote), 0.00) "
			+")"
			+ "from  Voting v "
			+ "right outer join "
			+ "Post p "
			+ "on p.id = v.votingId.post.id "
			+ "right outer join "
			+ "User u "
			+ "on p.author.id = :id"
			+ "")
	List<AuhtorResponse> findResumeVotingByAuthor(@Param("id") Long id);
*/

	

	
}
