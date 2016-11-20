package controllers;

import common.GlobalConstants;

import play.mvc.*;

@Check(GlobalConstants.PERM_LOGS_VIEW)
public class Logs extends CrudJson {

	public static boolean delitable = false;

	public static boolean checkEditPermission() {
		return false;
	}

}
