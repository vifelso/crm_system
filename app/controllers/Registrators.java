package controllers;

import java.util.List;

import common.GlobalConstants;

import models.Registrator;
import models.RegistratorName;
import models.RubricURL;
import models.RubricURLStatus;
import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.*;

public class Registrators extends CrudJson {

	public static boolean delitable = true;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_REGISTRATORS_EDIT);
	}

	public static void create() throws Exception {
		Registrator object = new Registrator();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Registrator checkSite = Registrator.find("site", object.site)
				.first();
		if (checkSite != null) {
			validation.addError("object.site", "registratorsite.dublicate");
		}
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", object);
			}
		}
		object._save();
	}

	public static void save(String id) {
		Registrator object = Registrator.findById(Long.valueOf(id));
		String oldSite = object.site;
		Binder.bind(object, "object", params.all());
		String newSite = object.site;
		validation.valid(object);
		if (!newSite.equals(oldSite)) {
			Registrator checkSite = Registrator.find("site", newSite).first();
			if (checkSite != null) {
				validation.addError("object.site", "registratorsite.dublicate");
			}
		}
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", object);
			}
		}
		object._save();
	}

}
