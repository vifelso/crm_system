package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import common.GlobalConstants;

import controllers.CRUD.Hidden;
import controllers.CRUD.Label;
import controllers.CRUD.PermissionCheck;

import java.util.*;

@Entity
@Table(name="registrator")
public class Registrator extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false, unique=true)
	@PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String site;
	
	@Required
	@Column(nullable=false, unique=true)
	@PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String name;
	
    @MaxSize(2000)
	@Column(columnDefinition="varchar(2000)")
    @PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String domain_transfer_procedure;
    
    @MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    @PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String domain_transfer_data;
    
    @MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    @PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String domain_intake_procedure;
    
    @MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    @PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String additional_info;
    
    @MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    @PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public String account_data;
    
    @OneToMany
    @PermissionCheck(GlobalConstants.PERM_REGISTRATORS_EDIT)
    public Set<RegistratorName> registrator_name;
	
    public String toString() {
        return site;
    }
	
}
