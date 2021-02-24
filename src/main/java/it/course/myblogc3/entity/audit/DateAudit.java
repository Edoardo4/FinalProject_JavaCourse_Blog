package it.course.myblogc3.entity.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public class DateAudit implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="CREATED_AT",
			updatable=false, insertable=false
			,columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;

	@Column(name="UPDATED_AT",
			updatable=true, insertable=false
			,columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedAt;
	
}
