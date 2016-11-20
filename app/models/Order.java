package models;

import play.*;
import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;
import play.mvc.Before;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import common.GlobalConstants;

import controllers.CRUD.Hidden;
import controllers.CRUD.Label;
import controllers.CRUD.PermissionCheck;

import java.util.*;

@Entity
@Table(name="orders")
public class Order extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@ManyToOne(optional=false)
	@Label
	public Domain domain;
	
	@Required
	@As("yyyy-MM-dd HH:mm")
	@Column(nullable=false)
	@Label
	public Date create_date;
	
    @Required
    @ManyToOne
    @JoinColumn(name="manager_id", nullable=false, columnDefinition="bigint(20) default 1")
    @PermissionCheck(GlobalConstants.PERM_ORDERS_REASSIGN)
    public User manager;
	
	@Required
	@Column(nullable=false)
	@PermissionCheck(GlobalConstants.PERM_ORDERS_EDIT_PRICE)
	public String expected_price;
    
    @Email
    public String owners_email;
	
    @MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    public String contacts;
	
	@MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    public String notes;
	
    public int actual_price;
    
    @Required
    @ManyToOne
    @JoinColumn(name="actual_order_type_id")
    public OrderType actual_order_type;
    
    @ManyToOne
    @JoinColumn(name="payment_method_id", columnDefinition="bigint(20) default 1")
    public PaymentMethod payment_method;
    
	@MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    public String payment_info;
	
    @MaxSize(1000)
	@Column(columnDefinition="varchar(1000)")
    public String reghost_info;
	
    @Required
    @ManyToOne
    @JoinColumn(name="status_id", nullable=false, columnDefinition="bigint(20) default 1")
    @Label
    public OrderStatus status;
	
	@As("yyyy-MM-dd HH:mm")
	@Label
	public Date purchase_date;
	
}
