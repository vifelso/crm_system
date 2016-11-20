package models;

import play.*;
import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import common.GlobalConstants;

import controllers.CRUD.Hidden;
import controllers.CRUD.Label;
import controllers.CRUD.PermissionCheck;
import controllers.Check;

import java.util.*;

@Entity
@Table(name="user")
public class User extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false)
	@PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public String family_name;
	
	@Required
	@Column(nullable=false)
	@PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public String first_name;
	
	@PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public String middle_name;
	
    @Email
    @Required
    @Column(nullable=false)
    @PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public String email;
	
    @Required
    @Password
    @Column(nullable=false)
    @PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public String password;
    
    @MaxSize(1000)
	@Column(nullable=false, columnDefinition="varchar(1000)")
    @PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public String contacts;
    
    @Required
    @ManyToMany
    @PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public Set<UserPosition> position;
    
    @ManyToMany
    @PermissionCheck(GlobalConstants.PERM_USERS_EDIT)
    public Set<UserPermission> permission;
    
    @ManyToOne
    @JoinColumn(name="status_id", nullable=false, columnDefinition="bigint(20) default 2")
    @Label
    public UserStatus status;
    
	@As("yyyy-MM-dd")
	@Column(columnDefinition="date", nullable=false)
	@Label
	public Date register_date;
	
	@As("yyyy-MM-dd")
	@Column(columnDefinition="date")
	@Label
	public Date block_date;
    
    public User() {
    	Set<UserPosition> positionsSet = new HashSet<UserPosition>();
    	positionsSet.add(new UserPosition(GlobalConstants.USER_POSITION_PURCHASING_MANAGER_ID));
    	this.position = positionsSet;
    }
    
    public User(Object id) {
    	this.id = (Long) id;
	}

	@PrePersist
    public void prepareToInsert() {
    	this.register_date = new Date();
    	this.status = new UserStatus(GlobalConstants.USER_STATUS_ACTIVE_ID);
    }
    
    public String toString() {
    	String userName = family_name + " " + first_name;
    	if (this.status.id == (long) 2){
    		userName = userName + " (заблок.)";
    	}
        return userName;
    }
    
    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }  
    
}
