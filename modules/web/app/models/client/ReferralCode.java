package models.client;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Id;
import play.db.ebean.Model;
import play.Logger;
import models.orders.Orders;
import java.util.Random;

@Entity
public class ReferralCode extends Model{ 
	@Id
	public Long referral_code_id;
	//fields
	@Column(nullable=false)
	public String code;
	
	@Column(nullable=false)
	public Date date_generated;
	
	@OneToMany(mappedBy="referralCode")
	public Orders order;
	
	@Column(nullable=false)
	@OneToOne
	public Client client;
	
	public ReferralCode(){
	
	}
	
	public ReferralCode(String code){
	  this.code = code;
	}

	public void saveReferralCode(){
		if(referral_code_id == null){
			date_generated = new Date();
			save();

		}else{
			update();

		}
	}
	
	public static Finder<Long, ReferralCode> find = new Finder<Long, ReferralCode>(Long.class, ReferralCode.class);
	
	public ReferralCode getReferralCode(String code){
			return find.where().eq("code", code).findUnique();
	}
	
	//generate a random code
	public String generateString(Random rng, int length){
			char[] text = new char[length];
			for (int i = 0; i < length; i++)
			{
				text[i] = alphabet.charAt(rng.nextInt(alphabet.length()));
			}
			String uniq_code = "tmly"+ new String(text);
			ReferralCode rc = getReferralCode(uniq_code);
			if(rc!=null){
				generateString(rng, 5);
			}
			return uniq_code;
	}	
	
	@Transient
	public String alphabet    = "abcdefghijklmnopqrstuvwxyz";
} 
