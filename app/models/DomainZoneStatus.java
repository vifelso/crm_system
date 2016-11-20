package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.CRUD.Hidden;

import java.util.*;

@Entity
@Table(name="domain_zone_status")
public class DomainZoneStatus extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false, unique=true)
	public String name;

    public String toString() {
        return name;
    }
    
    public DomainZoneStatus(int id) {
        this.id = (long) id;
    }
    
	public DomainZoneStatus(Long id) {
        this.id = id;
	}
    
}
