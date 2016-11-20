package controllers;

import java.util.Set;

import models.*;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return User.connect(username, password) != null;
	}

	static boolean check(String profile) {
		boolean check = false;
		Set<UserPermission> permissions = User.find("byEmail", connected()).<User> first().permission;
		for (UserPermission permission : permissions) {
			if (permission.canonical_name.equals(profile)) {
				check = true;
				break;
			}
		}
		return check;
	}

}
