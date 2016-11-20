package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.CRUD.Hidden;

import java.util.*;

@Entity
@Table(name="rubric_url_parsing_status")
public class RubricURLParsingStatus extends Model {

	@Hidden
	@Column(insertable=false, updatable=false)
    public Long id;
	
	@Required
	@Column(nullable=false, unique=true)
	public String name;

    public String toString() {
        return name;
    }
    
    public RubricURLParsingStatus(int id) {
        this.id = (long) id;
    }

	public RubricURLParsingStatus(Long id) {
        this.id = id;
	}
	    
}
