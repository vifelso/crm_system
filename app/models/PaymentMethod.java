package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import common.GlobalConstants;

import controllers.CRUD.Hidden;
import controllers.CRUD.PermissionCheck;

import java.util.*;

@Entity
@Table(name="payment_method")
public class PaymentMethod extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false, unique=true)
	@PermissionCheck(GlobalConstants.PERM_PAYMENT_METHODS_EDIT)
	public String name;
	
	@Required
	@Column(nullable=false)
	@PermissionCheck(GlobalConstants.PERM_PAYMENT_METHODS_EDIT)
	public String tax;
	
	@Required
	@MaxSize(1000)
	@Column(nullable=false, columnDefinition="varchar(1000)")
	@PermissionCheck(GlobalConstants.PERM_PAYMENT_METHODS_EDIT)
	public String required_transfer_data;
	
	@MaxSize(2000)
	@Column(columnDefinition="varchar(2000)")
	@PermissionCheck(GlobalConstants.PERM_PAYMENT_METHODS_EDIT)
    public String description;
    
    public String toString() {
        return name;
    }
    
}
