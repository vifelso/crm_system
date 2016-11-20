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
@Table(name="setting")
public class Setting extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false)
	@Label
    public String last_viewed_yaca_site;
	
	@Hidden
    public String temp_last_viewed_yaca_site;
	
	@Required
	@Column(columnDefinition = "int(11) default 0")
	@PermissionCheck(GlobalConstants.PERM_SETTINGS_EDIT)
    public int update_domain_days_before_free;
	
	@Required
	@Column(columnDefinition = "int(11) default 0")
	@PermissionCheck(GlobalConstants.PERM_SETTINGS_EDIT)
    public int update_del_domains_days_interval;
	
}
