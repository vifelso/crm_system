package controllers;

import java.util.Date;
import java.util.List;

import jobs.ParseYacaPages;
import jobs.UpdateDomainInfo;

import org.hibernate.annotations.Filter;

import common.GlobalConstants;

import controllers.CRUD.ObjectType;

import models.Domain;
import models.DomainStatus;
import models.DomainZone;
import models.Order;
import models.OrderStatus;
import models.RubricURL;
import models.RubricURLStatus;
import models.Setting;
import models.User;
import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.*;

public class Domains extends CrudJson {

	public static boolean delitable = false;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_DOMAINS_EDIT);
	}

	public static void refreshDomainParams(Long id) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		new UpdateDomainInfo(id).now();
	}

	public static int getUpdateDomainBeforeDays() {
		Setting settings = Setting.findById((long) 1);
		return settings.update_domain_days_before_free;
	}

	public static void checkDomain(Long id) {
		Domain domain = Domain.findById(id);
		domain.status = new DomainStatus(2);
		domain.save();
	}

	public static void chooseDomain(Long id) {
		Domain domain = Domain.findById(id);
		domain.status = new DomainStatus(3);
		domain.save();
	}

	public static void cancelDomainOrders(Long id) {
		Domain domain = Domain.findById(id);
		for (Order order : (List<Order>) domain.orders) {
			order.status = new OrderStatus(8);
			order.save();
		}
		domain.status = new DomainStatus(1);
		domain.save();
	}

	public static String getDomainsCount() {
		String domainsCounts = new String();
		String delimeter = ",";
		List<DomainStatus> allStatuses = DomainStatus.find("order by id asc").fetch();
		for (int index = 0; index < allStatuses.size(); index++) {
			domainsCounts = domainsCounts + getDomainsCountByStatus(allStatuses.get(index).id);
			if (index != allStatuses.size() - 1) {
				domainsCounts = domainsCounts + delimeter;
			}
		}
		return domainsCounts;
	}

	public static String getDomainsCountByStatus(long statusid) {
		Long domainsCount = Domain.count("status_id=" + statusid);
		return domainsCount.toString();
	}

	public static void save(String id) {
		Domain object = Domain.findById(Long.valueOf(id));
		DomainStatus statusFromDb = new DomainStatus(object.status.id);
		DomainZone zoneFromDb = new DomainZone(object.domain_zone.id);
		Binder.bind(object, "object", params.all());
		object.status = statusFromDb;
		object.domain_zone = zoneFromDb;
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html", object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", object);
			}
		}
		object._save();
	}

	public static void showByName(String name) {
		Domain object = Domain.find("name", name).first();
		try {
			render(request.controller.replace(".", "/") + "/show.html", object);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", object);
		}
	}

}
