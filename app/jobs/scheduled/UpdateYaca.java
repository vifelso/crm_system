package jobs.scheduled;

import java.net.IDN;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import jobs.ParseDomainZoneInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.Domain;
import models.DomainZone;
import models.DomainZoneStatus;
import models.Log;
import models.Setting;

import common.CommonUtils;
import common.GlobalConstants;

import parsers.domains.CatalogPageParser;
import play.jobs.Job;
import play.jobs.On;

public class UpdateYaca extends Job {

	private List<String> domainsFromDb;
	private List<DomainZone> domainZones;
	private List<String> savedDomains;
	private boolean jobFinished = false;
	private int checkedDomainsCount;
	private String lastViewedSite;

	public UpdateYaca(int checkedDomainsCount) {
		this.checkedDomainsCount = checkedDomainsCount;
	}

	public void doJob() {
		initJob();
		System.out.println("5. Update yaca: start/restart job");
		boolean yacaParsed = false;
		String currentDomainName = "";
		for (int i = 0; i < 100; i++) {
			if (!yacaParsed) {
				if (!this.jobFinished) {
					Document doc;
					try {
						doc = Jsoup.connect(GlobalConstants.YACA_TIME_URL + i + ".html").timeout(10 * 1000)
								.cookie(GlobalConstants.YACA_REGION_ID_PARAM, GlobalConstants.YACA_MOSCOW_REGION_ID)
								.get();
						Elements sites = CatalogPageParser.parseSitesElements(doc);
						for (int j = 0; j < sites.size(); j++) {
							Element siteElement = sites.get(j);
							if (!this.jobFinished) {
								String url = CatalogPageParser.parseUrl(siteElement);
								currentDomainName = url;
								System.out.println("5. Update yaca: domain=" + url);
								if (i == 0 && j == 0) {
									setTempLastCheckedSite(url);
								}
								if (url.equals(lastViewedSite)) {
									yacaParsed = true;
									break;
								}
								if (checkDomainNameExists(url) || !CommonUtils.isDomainNameApplicable(url)) {
									checkedDomainsCount = checkedDomainsCount + 1;
									continue;
								}
								String idnURL = IDN.toASCII(url);
								Domain domain = new Domain(url, idnURL);
								domain.setCatalogPageParams(siteElement);
								domain.setYacaSitePageParams(idnURL.toUpperCase());
								domain.setPR(idnURL);
								domain.setWebArcivewCount(idnURL);
								DomainZone domainZone = updateDomainZones(url);
								domain.domain_zone = domainZone;
								domain.tic_updated = new Date();
								domain.params_updated = new Date();
								Date fake_date = CommonUtils.getFakeDate();
								domain.created = fake_date;
								domain.paid_till = fake_date;
								domain.free_date = fake_date;
								domain.create();
								savedDomains.add(url);
								checkedDomainsCount = checkedDomainsCount + 1;
							}
						}

					} catch (Exception e) {
						saveException(currentDomainName, e);
						this.jobFinished = true;
						restartJob();
						e.printStackTrace();
					}
				}
			} else {
				break;
			}
		}

		if (!this.jobFinished) {
			Setting settings = Setting.findById((long) 1);
			settings.last_viewed_yaca_site = settings.temp_last_viewed_yaca_site;
			settings.temp_last_viewed_yaca_site = "";
			settings.save();
			Log log = Log.findById((long) 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			log.yaca_update_result = "Завершено: " + sdf.format(new Date()) + " / " + "Проверено доменов: "
					+ checkedDomainsCount;
			log.save();
			new UpdateNewDomainsWhois(0).in(60);
		}
	}

	private void initJob() {
		domainsFromDb = populateDomainsFromDB();
		domainZones = populateDomainZonesFromDB();
		savedDomains = new ArrayList<String>();
		Setting settings = Setting.findById((long) 1);
		lastViewedSite = settings.last_viewed_yaca_site;
	}

	private List<String> populateDomainsFromDB() {
		List<String> domainNames = new ArrayList<String>();
		List<Domain> domains = Domain.findAll();
		for (Domain domain : domains) {
			domainNames.add(domain.name);
		}
		return domainNames;
	}

	private List<DomainZone> populateDomainZonesFromDB() {
		return DomainZone.findAll();
	}

	private void setTempLastCheckedSite(String url) {
		Setting settings = Setting.findById((long) 1);
		settings.temp_last_viewed_yaca_site = url;
		settings.save();
	}

	private boolean checkDomainNameExists(String domainName) {
		boolean domainExists = false;
		if (domainsFromDb.contains(domainName)) {
			domainExists = true;
		}
		if (savedDomains.contains(domainName)) {
			domainExists = true;
		}
		return domainExists;
	}

	private DomainZone updateDomainZones(String url) {
		DomainZone urlDomainZone = null;
		boolean zoneExists = false;

		String urlWoWWW = CommonUtils.removeWWW(url);
		String zoneName = urlWoWWW.substring(urlWoWWW.indexOf(".") + 1);

		for (DomainZone zone : domainZones) {
			if (zone.name.equals(zoneName)) {
				urlDomainZone = zone;
				zoneExists = true;
				zone.domains_count = zone.domains_count + 1;
				zone.save();
				break;
			}
		}

		if (!zoneExists) {
			DomainZone domainZone = new DomainZone(zoneName, 1);
			domainZone.create();
			DomainZone newZone = DomainZone.find("name", zoneName).first();
			domainZones.add(newZone);
			urlDomainZone = newZone;
		}

		return urlDomainZone;
	}

	private void saveException(String currentDomainName, Exception e) {
		Log log = Log.findById((long) 1);
		log.yaca_update_result = "Ошибка обновления домена: " + currentDomainName + "\n" + "Код ошибки: "
				+ e.toString();
		log.save();
	}

	private void restartJob() {
		new UpdateYaca(checkedDomainsCount).in(30);
	}

}
