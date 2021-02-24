package it.course.myblogc3.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import it.course.myblogc3.entity.audit.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="USER")
@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class User extends DateAudit{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NaturalId(mutable=true) //notation che serve per trattare il campo come "chiave primaria"
	@Column(name="EMAIL", nullable=false, length=120)
	private String email;
	
	@Column(name="USERNAME", nullable=false, length=12)
	public String username;
	
	@Column(name="PASSWORD", nullable=false, length=100)
	public String password;
	
	@Column(name="IS_ENABLE", columnDefinition="TINYINT(1)")
	public Boolean enabled=false;
	
	private String identifierCode;
	
	private String registrationConfirmCode;

	private LocalDateTime bannedUntil;

	@Column(nullable=false, columnDefinition="INT(6)")
	private int credits=0; 
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="USER_AUTHORITIES",
		joinColumns = {@JoinColumn(name="USER_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="AUTHORITY_ID", referencedColumnName="ID")}
			)
	private Set<Authority> authorities;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="USER_PREFERRED_TAGS",
		joinColumns = {@JoinColumn(name="USER_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="TAG_ID", referencedColumnName="TAG_NAME")}
			)
	private Set<Tag> preferredTags;
	
	public User(String email, String username, String password) {
		super();
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public User(Long id) {
		super();
		this.id = id;
	}
}
