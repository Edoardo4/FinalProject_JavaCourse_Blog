package it.course.myblogc3.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="PURCHASED_POST")
@Data @AllArgsConstructor @NoArgsConstructor
public class PurchasedPost {
	
	@EmbeddedId
	private PurchasedPostId purchasePostId;
	
	
	@Column(name="PURCHASE_DATE",
			updatable=false, insertable=false
			,columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date purchaseDate;


	public PurchasedPost(PurchasedPostId purchasePostId) {
		super();
		this.purchasePostId = purchasePostId;
	}
	
	
		
}
