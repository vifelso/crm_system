package models;

import play.*;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.jpa.*;
import play.data.binding.*;

import groovy.beans.Bindable;

import javax.persistence.*;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import common.GlobalConstants;

import controllers.CRUD.For;
import controllers.CRUD.Hidden;
import controllers.CRUD.Label;
import controllers.CRUD.PermissionCheck;

import java.util.*;

@Entity
@Table(name="rubric_url")
public class RubricURL extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false, unique=true)
	@PermissionCheck(GlobalConstants.PERM_RUBRICS_EDIT)
    public String URL;
	
	@Label
	@Column(columnDefinition="int(11) default 0")
    public int sites_count;
	
	@Label
	@Column(columnDefinition="int(11) default 0")
    public int checked_sites_count;
	
	@Hidden
	@Column(columnDefinition="int(11) default 0")
    public int checked_sites_percent;
	
	@Label
	@Column(columnDefinition="int(11) default 0")
    public int saved_domains_count;
	
	@As("yyyy-MM-dd HH:mm")
	@Label
	public Date last_check_date;
    
	@Label
    @ManyToOne
    @JoinColumn(name="status_id", nullable=false, columnDefinition="bigint(20) default 1")
    public RubricURLStatus status;
	
	@Label
    @ManyToOne
    @JoinColumn(name="parsing_status_id", nullable=false, columnDefinition="bigint(20) default 1")
    public RubricURLParsingStatus parsing_status;	

}
