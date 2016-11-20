package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.CRUD.Hidden;
import controllers.CRUD.Label;

import java.util.*;

@Entity
@Table(name="domain_zone")
public class DomainZone extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Label
	@Column(nullable=false, unique=true)
	public String name;
	
	@Label
	@Column(columnDefinition = "int(11) default 0")
	public int domains_count;
	
	@Label
	@Column(columnDefinition = "int(11) default 0")
	public int checked_domains_count;
	
	@Required
	@ManyToOne
	@JoinColumn(name = "status_id", nullable = false, columnDefinition = "bigint(20) default 1")
	@Label
	public DomainZoneStatus status;

    public DomainZone(String zoneName, int domainsCount) {
		this.name = zoneName;
		this.domains_count = domainsCount;
		this.status = new DomainZoneStatus(1);
	}
    
	public DomainZone(Long id) {
        this.id = id;
	}

	public String toString() {
        return name;
    }
    
}
