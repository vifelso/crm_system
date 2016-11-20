package controllers;

import common.GlobalConstants;

import play.mvc.*;

@Check(GlobalConstants.PERM_SETTINGS_VIEW)
public class Settings extends CrudJson {
	
	public static boolean delitable = false;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_SETTINGS_EDIT);
	}
}
