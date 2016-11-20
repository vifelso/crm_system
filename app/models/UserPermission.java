package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.CRUD.Hidden;

import java.util.*;

@Entity
@Table(name="permission")
public class UserPermission extends Model {
	
	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false, unique=true)
	public String canonical_name;
	
	@Required
	@Column(nullable=false, unique=true)
	public String name;
	
    public UserPermission(int id) {
        this.id = (long) id;
    }

    public String toString() {
        return name;
    }
    
}
