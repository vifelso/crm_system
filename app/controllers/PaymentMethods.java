package controllers;

import common.GlobalConstants;

import play.mvc.*;

public class PaymentMethods extends CrudJson {
	
	public static boolean delitable = false;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_PAYMENT_METHODS_EDIT);
	}

}
