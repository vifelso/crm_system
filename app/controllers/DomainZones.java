package controllers;

import java.util.Date;
import java.util.List;

import common.GlobalConstants;

import jobs.ParseDomainZoneInfo;
import jobs.ParseYacaPages;

import models.Domain;
import models.DomainStatus;
import models.DomainZone;
import models.DomainZoneStatus;
import models.RubricURL;
import models.RubricURLStatus;
import models.User;
import models.UserStatus;
import play.mvc.*;

@Check(GlobalConstants.PERM_DOMAINZONES_VIEW)
public class DomainZones extends CrudJson {

	public static boolean delitable = false;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_DOMAINZONES_EDIT);
	}

	public static String checkZone(Long id) {
		DomainZone zone = DomainZone.findById(id);
		String response = "";
		String ckeckedZonesString = "";
		String[] checkedZones = GlobalConstants.CHECKED_DOMAIN_ZONES;
		boolean isZoneInCheckedList = false;
		for (int i = 0; i < checkedZones.length; i++) {
			ckeckedZonesString = ckeckedZonesString + checkedZones[i];
			if (i != (checkedZones.length - 1)) {
				ckeckedZonesString = ckeckedZonesString + ", ";
			}
			if (checkedZones[i].equals(zone.name)) {
				isZoneInCheckedList = true;
			}
		}
		if (isZoneInCheckedList) {
			boolean startFromBegining = true;
			if (zone.status.id == 5) {
				startFromBegining = false;
			}
			zone.status = new DomainZoneStatus(4);
			zone.save();
			new ParseDomainZoneInfo(zone, startFromBegining).now();
		} else {
			response = ckeckedZonesString;
		}
		return response;
	}

	public static void noInfo(Long id) {
		DomainZone domainZone = DomainZone.findById(id);
		domainZone.status = new DomainZoneStatus(2);
		domainZone.save();
	}

	public static void checkPossible(Long id) {
		DomainZone domainZone = DomainZone.findById(id);
		domainZone.status = new DomainZoneStatus(3);
		domainZone.save();
	}

	public static String getDomainZonesCount() {
		String domainZonesCounts = new String();
		String delimeter = ",";
		List<DomainZoneStatus> allStatuses = DomainZoneStatus.find("order by id asc").fetch();
		for (int index = 0; index < allStatuses.size(); index++) {
			domainZonesCounts = domainZonesCounts + getDomainZonesCountsByStatus(allStatuses.get(index).id);
			if (index != allStatuses.size() - 1) {
				domainZonesCounts = domainZonesCounts + delimeter;
			}
		}
		return domainZonesCounts;
	}

	public static String getDomainZonesCountsByStatus(long statusid) {
		Long domainZonesCounts = DomainZone.count("status_id=" + statusid);
		return domainZonesCounts.toString();
	}

}
