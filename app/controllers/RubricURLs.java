package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import jobs.ParseYacaPages;
import jobs.scheduled.UpdateDeletedDomains;
import jobs.scheduled.UpdateParams;
import jobs.scheduled.UpdateTic;
import jobs.scheduled.UpdateWhois;
import jobs.scheduled.UpdateYaca;

import models.RubricURL;
import models.RubricURLParsingStatus;
import models.RubricURLStatus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.ForwardRequest;

import common.GlobalConstants;

import play.data.binding.Binder;
import play.db.Model;
import play.db.jpa.JPABase;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.jobs.Job;
import play.libs.F.Promise;
import play.mvc.*;

@Check(GlobalConstants.PERM_RUBRICS_VIEW)
public class RubricURLs extends CrudJson {

	public static boolean delitable = true;
	
	public static boolean checkEditPermission() {
		return Security.check(GlobalConstants.PERM_RUBRICS_EDIT);
	}

	public static void checkURL(Long id) throws InterruptedException {
		boolean startFromBegining = true;
		RubricURL rubricURL = RubricURL.findById(id);
		if (rubricURL.status.id == 3) {
			startFromBegining = false;
		}
		rubricURL.status = new RubricURLStatus(2);
		rubricURL.last_check_date = new Date();
		Document doc;
		try {
			doc = Jsoup.connect(GlobalConstants.YACA_TIC_URL + rubricURL.URL)
					.cookie(GlobalConstants.YACA_REGION_ID_PARAM, GlobalConstants.YACA_MOSCOW_REGION_ID).get();
			Elements counter = doc.getElementsByClass("b-site-counter__number");
			String sitesCountString = counter.get(0).text();
			int sitesCount = Integer.parseInt(sitesCountString.substring(0, sitesCountString.indexOf(" сайт")));
			rubricURL.sites_count = sitesCount;
			String parsingUrl = getParsingURL(rubricURL);
			if (rubricURL.parsing_status.id == 1 && rubricURL.sites_count > 1000
					&& rubricURL.checked_sites_count >= 1000) {
				rubricURL.parsing_status = new RubricURLParsingStatus(2);
				rubricURL.checked_sites_count = 0;
				rubricURL.checked_sites_percent = 0;
				rubricURL.saved_domains_count = 0;
			}
			if (rubricURL.parsing_status.id == 2 && rubricURL.checked_sites_count >= 1000) {
				rubricURL.parsing_status = new RubricURLParsingStatus(3);
				rubricURL.checked_sites_count = 0;
				rubricURL.checked_sites_percent = 0;
				rubricURL.saved_domains_count = 0;
			}
			new ParseYacaPages(rubricURL, parsingUrl, startFromBegining).in(3);
		} catch (IOException e) {
			rubricURL.status = new RubricURLStatus(3);
			rubricURL.save();
			e.printStackTrace();
		}
		rubricURL.save();
	}

	private static String getParsingURL(RubricURL rubric) {
		String parsingURL = GlobalConstants.YACA_TIC_URL;
		if (rubric.parsing_status.id == 1 && rubric.sites_count > 1000 && rubric.checked_sites_count >= 1000) {
			parsingURL = GlobalConstants.YACA_NAME_URL;
		}
		if (rubric.parsing_status.id == 2) {
			if (rubric.checked_sites_count >= 1000) {
				parsingURL = GlobalConstants.YACA_TIME_URL;
			} else {
				parsingURL = GlobalConstants.YACA_NAME_URL;
			}
		}
		if (rubric.parsing_status.id == 3) {
			parsingURL = GlobalConstants.YACA_TIME_URL;
		}
		return parsingURL;
	}

	public static String getRubricUrlsCount() {
		String rubricCounts = new String();
		String delimeter = ",";
		List<RubricURLStatus> allStatuses = RubricURLStatus.find("order by id asc").fetch();
		for (int index = 0; index < allStatuses.size(); index++) {
			rubricCounts = rubricCounts + getUrlsCountByStatus(allStatuses.get(index).id);
			if (index != allStatuses.size() - 1) {
				rubricCounts = rubricCounts + delimeter;
			}
		}
		return rubricCounts;
	}

	public static String getUrlsCountByStatus(long statusid) {
		Long urlsCount = RubricURL.count("status_id=" + statusid);
		return urlsCount.toString();
	}

	public static void save(String id) {
		RubricURL object = RubricURL.findById(Long.valueOf(id));
		String oldUrl = object.URL;
		RubricURLStatus statusFromDb = new RubricURLStatus(object.status.id);
		Binder.bind(object, "object", params.all());
		String newUrl = object.URL;
		object.status = statusFromDb;
		validation.valid(object);
		if (!newUrl.equals(oldUrl)) {
			RubricURL checkURL = RubricURL.find("url", newUrl).first();
			if (checkURL != null) {
				validation.addError("object.URL", "rubric.dublicate");
			}
		}
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

	public static void create() throws Exception {
		RubricURL object = new RubricURL();
		Binder.bind(object, "object", params.all());
		object.status = new RubricURLStatus(1);
		object.parsing_status = new RubricURLParsingStatus(1);
		validation.valid(object);
		RubricURL checkURL = RubricURL.find("url", object.URL).first();
		if (checkURL != null) {
			validation.addError("object.URL", "rubric.dublicate");
		}
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html", object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", object);
			}
		}
		object._save();
	}

}
