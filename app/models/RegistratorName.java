package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.CRUD.Hidden;
import controllers.CRUD.Label;

import java.util.*;

@Entity
@Table(name = "registrator_name")
public class RegistratorName extends Model {

	@Hidden
	@Column(insertable = false, updatable = false)
	public Long id;

	@Required
	@Column(nullable = false, unique = true)
	@Label
	public String name;
	
	@Hidden
	@Column(nullable = false, unique = true)
	public String canonical_name;

	public RegistratorName(String registratorId) {
		this.name = registratorId;
		this.canonical_name = registratorId;
	}

	public String toString() {
		return name;
	}

}
