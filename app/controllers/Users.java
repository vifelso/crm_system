package controllers;

import java.util.Date;
import java.util.List;

import common.GlobalConstants;

import models.Domain;
import models.DomainStatus;
import models.Registrator;
import models.User;
import models.UserStatus;
import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.*;

public class Users  extends CrudJson {

	public static boolean delitable = false;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_USERS_EDIT);
	}
	
	public static void blockUser(Long id) {
		User user = User.findById(id);
		user.status = new UserStatus(2);
		user.block_date = new Date();
		user.save();
	}
	
	public static void unblockUser(Long id) {
		User user = User.findById(id);
		user.status = new UserStatus(1);
		user.block_date = null;
		user.save();
	}
	
	public static String getUsersCount() {
		String usersCounts = new String();
		String delimeter = ",";
		List<UserStatus> allStatuses = UserStatus.find("order by id asc").fetch();
		for (int index = 0; index < allStatuses.size(); index++) {
			usersCounts = usersCounts + getUsersCountByStatus(allStatuses.get(index).id);
			if(index != allStatuses.size()-1) {
				usersCounts = usersCounts + delimeter;
			}
		}
		return usersCounts;	
	}
	
	public static String getUsersCountByStatus(long statusid) {
		Long usersCount = User.count("status_id=" + statusid);
		return usersCount.toString();
	}

}
