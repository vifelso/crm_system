package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.CRUD.Label;

import java.util.*;

@Entity
@Table(name="log")
public class Log extends Model {
	
    @Lob
    @Label
    @MaxSize(10000)
    public String deleted_domains_update_result;
	
    @Lob
    @Label
    @MaxSize(10000)
    public String whois_update_result;
    
    @Lob
    @Label
    @MaxSize(10000)
    public String params_update_result;
    
    @Lob
    @Label
    @MaxSize(10000)
    public String tic_update_result;
    
    @Lob
    @Label
    @MaxSize(10000)
    public String yaca_update_result;
    
    @Lob
    @Label
    @MaxSize(10000)
    public String newdomains_whois_update_result;
}
