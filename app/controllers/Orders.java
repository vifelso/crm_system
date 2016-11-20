package controllers;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Query;

import common.GlobalConstants;

import models.Domain;
import models.DomainStatus;
import models.Order;
import models.OrderStatus;
import models.RubricURL;
import models.RubricURLStatus;
import models.User;
import models.UserStatus;

import controllers.CRUD.ObjectType;
import play.data.binding.Binder;
import play.db.Model;
import play.db.jpa.JPA;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.*;

public class Orders extends CrudJson {

	public static boolean delitable = false;

	public static boolean checkEditPermission() {
		return true;
	}

	public static boolean checkCancelPermission() {
		return Security.check(GlobalConstants.PERM_ORDERS_CANCEL);
	}

	public static boolean checkMakeAutoPermission() {
		return Security.check(GlobalConstants.PERM_ORDERS_MAKEAUTO);
	}

	public static boolean checkViewAllOrdersPermission() {
		return Security.check(GlobalConstants.PERM_ORDERS_VIEWALL);
	}

	public static void autoStatus(Long id) {
		Order order = Order.findById(id);
		order.status = new OrderStatus(10);
		order.save();
	}

	public static void onPhoneStatus(Long id) {
		Order order = Order.findById(id);
		order.status = new OrderStatus(2);
		order.save();
	}

	public static void thinkingStatus(Long id) {
		Order order = Order.findById(id);
		order.status = new OrderStatus(3);
		order.save();
	}

	public static void agreeStatus(Long id) {
		Order order = Order.findById(id);
		order.status = new OrderStatus(4);
		order.save();
	}

	public static void paymentWaitStatus(Long id) {
		Order order = Order.findById(id);
		order.status = new OrderStatus(5);
		order.save();
	}

	public static void notOnPhoneStatus(Long id) {
		Order order = Order.findById(id);
		Domain domain = Domain.findById(order.domain.id);
		domain.status = new DomainStatus(2);
		domain.save();
		order.status = new OrderStatus(6);
		order.save();
	}

	public static void disagreeStatus(Long id) {
		Order order = Order.findById(id);
		Domain domain = Domain.findById(order.domain.id);
		domain.status = new DomainStatus(2);
		domain.save();
		order.status = new OrderStatus(7);
		order.save();
	}

	public static void cancelOrder(Long id) {
		Order order = Order.findById(id);
		Domain domain = Domain.findById(order.domain.id);
		domain.status = new DomainStatus(2);
		domain.save();
		order.status = new OrderStatus(8);
		order.save();
	}

	public static void purchasedStatus(Long id) {
		Order order = Order.findById(id);
		Domain domain = Domain.findById(order.domain.id);
		domain.status = new DomainStatus(6);
		domain.save();
		order.status = new OrderStatus(9);
		order.purchase_date = new Date();
		order.save();
	}

	public static void blank() throws Exception {
		Order object = new Order();
		String domainId = request.params.get("domainid");
		Domain domain = Domain.findById(Long.parseLong(domainId));
		Domain objectDomain = new Domain();
		objectDomain.name = domain.name;
		object.domain = objectDomain;
		object.create_date = null;
		object.status = null;
		object.manager = selectAppropriateManager();
		renderArgs.put("orderDomainId", domainId);
		request.params.put("filter_blocked_users", "true");
		try {
			render(object);
		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", object);
		}
	}

	private static User selectAppropriateManager() {
		User defaultUser = null;
		Query userPositionQuery = JPA.em().createNativeQuery(
				"select user_id from user_position where position_id = :position_id");
		userPositionQuery.setParameter("position_id", GlobalConstants.USER_POSITION_PURCHASING_MANAGER_ID);
		List managerIds = userPositionQuery.getResultList();

		if (managerIds.size() > 0) {
			SortedMap managerOrders = new TreeMap();
			for (BigInteger managerId : (List<BigInteger>) managerIds) {
				User user = User.findById(managerId.longValue());
				if (user.status.id != 2) {
					managerOrders.put(Order.count("manager = ?", user), user);
				}
			}
			if (managerOrders.size() > 0) {
				return (User) managerOrders.get(managerOrders.firstKey());
			}
		}

		return defaultUser;
	}

	public static void create() throws Exception {
		Order object = new Order();
		Binder.bind(object, "object", params.all());
		object.status = new OrderStatus(1);
		object.create_date = new Date();
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			object.create_date = null;
			try {
				render(request.controller.replace(".", "/") + "/blank.html", object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", object);
			}
		}
		Domain domain = Domain.findById(object.domain.id);
		domain.status = new DomainStatus(4);
		domain.save();
		object._save();
	}

	public static String getOrdersCount() {
		boolean viewAllPermission = Security.check(GlobalConstants.PERM_ORDERS_VIEWALL);
		Long currenUserId = User.find("byEmail", Security.connected()).<User> first().id;
		String ordersCounts = new String();
		String delimeter = ",";
		List<OrderStatus> allStatuses = OrderStatus.find("order by id asc").fetch();
		for (int index = 0; index < allStatuses.size(); index++) {
			ordersCounts = ordersCounts
					+ getOrdersCountByStatus(allStatuses.get(index).id, currenUserId, viewAllPermission);
			if (index != allStatuses.size() - 1) {
				ordersCounts = ordersCounts + delimeter;
			}
		}
		return ordersCounts;
	}

	public static String getOrdersCountByStatus(long statusid, Long currenUserId, boolean viewAllPermission) {
		Long ordersCounts = new Long(0);
		if (viewAllPermission) {
			ordersCounts = Order.count("status_id=" + statusid);
		} else {
			ordersCounts = Order.count("status_id=" + statusid + " and manager_id=" + currenUserId);
		}
		return ordersCounts.toString();
	}

}
