package common;

public final class GlobalConstants {

	// user statuses
	public static final int USER_STATUS_ACTIVE_ID = 2;

	// user positions
	public static final int USER_POSITION_PURCHASING_MANAGER_ID = 1;
	public static final int USER_POSITION_CHEAF_PURCHASING_MANAGER_ID = 2;

	// YAC urls
	public static final String YACA_TIC_URL = "http://yaca.yandex.ru/yca/ungrp/cat/";
	public static final String YACA_TIME_URL = "http://yaca.yandex.ru/yca/time/ungrp/cat/";
	public static final String YACA_NAME_URL = "http://yaca.yandex.ru/yca/alf/ungrp/cat/";
	public static final String YACA_CHECK_SITE_URL = "http://yaca.yandex.ru/yca/cy/ch/";
	public static final String YACA_REGION_ID_PARAM = "yandex_gid";
	public static final String YACA_MOSCOW_REGION_ID = "213";
	public static final String YACA_SITE_ABSENT = "ресурс не описан в Яндекс.Каталоге";

	// Yandex constants
	public static final String YANDEX_IN_INDEX_URL = "http://yandex.ru/yandsearch?text=host:";
	public static final String YANDEX_BAR_URL = "http://bar-navig.yandex.ru/u?ver=2&show=32&url=http://";

	// Job constants
	public static final int DEFAULT_SLEEP_TIME = 2000;

	// Domain zones constants
	public static final String[] CHECKED_DOMAIN_ZONES = { "ru", "su", "рф", "com", "net", "org", "info", "com.ua",
			"ua", "kiev.ua", "org.ua", "gov.ua", "od.ua", "in.ua", "dp.ua", "lviv.ua", "ck.ua", "odessa.ua",
			"crimea.ua", "edu.ua", "if.ua", "cv.ua", "ks.ua", "lutsk.ua", "km.ua", "sebastopol.ua", "zt.ua",
			"kherson.ua", "uz.ua", "rv.ua", "zhitomir.ua", "dn.ua", "kharkov.ua", "zp.ua", "lg.ua", "donetsk.ua",
			"kh.ua", "sumy.ua", "lugansk.ua", "pl.ua", "poltava.ua", "kr.ua", "by", "biz", "tv", "uz", "md", "ee",
			"am", "spb.ru", "org.ru", "com.ru", "net.ru", "msk.ru", "cz", "ws", "co.il", "pp.ru", "fm", "pro",
			"com.tr", "fi", "it", "travel", "me", "in", "pl", "aero", "edu", "us", "cc", "mobi" };
	public static final String WHOIS_RIPN_URL = "http://www.ripn.net/nic/whois/whois.cgi?";
	public static final String WHOIS_INTERNIC_PREFIX = "http://reports.internic.net/cgi/whois?whois_nic=";
	public static final String WHOIS_INTERNIC_POSTFIX = "&type=domain";

	// Permissions constants
	public static final String PERM_ORDERS_VIEWALL = "ViewAllOrders";
	public static final String PERM_ORDERS_REASSIGN = "ReassignOrder";
	public static final String PERM_ORDERS_EDIT_PRICE = "EditOrderExpextedPrice";
	public static final String PERM_ORDERS_CANCEL = "CancelOrder";
	public static final String PERM_ORDERS_MAKEAUTO = "MakeAutoOrder";
	public static final String PERM_DOMAINS_VIEW = "ViewDomains";
	public static final String PERM_DOMAINS_EDIT = "EditDomains";
	public static final String PERM_REGISTRATORS_EDIT = "EditRegistrators";
	public static final String PERM_PAYMENT_METHODS_EDIT = "EditPaymentMethods";
	public static final String PERM_USERS_EDIT = "EditUsers";
	public static final String PERM_DOMAINZONES_VIEW = "ViewDomainZones";
	public static final String PERM_DOMAINZONES_EDIT = "EditDomainZones";
	public static final String PERM_RUBRICS_VIEW = "ViewRubrics";
	public static final String PERM_RUBRICS_EDIT = "EditRubrics";
	public static final String PERM_SETTINGS_VIEW = "ViewSettings";
	public static final String PERM_SETTINGS_EDIT = "EditSettings";
	public static final String PERM_LOGS_VIEW = "ViewLogs";

}
