package jobs;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.GlobalConstants;
import common.CommonUtils;

import models.Domain;
import models.DomainZone;
import models.RubricURL;
import models.RubricURLStatus;
import models.User;

import parsers.domains.CatalogPageParser;
import parsers.domains.YacaSitePagePaser;
import play.jobs.Job;
import play.libs.F.Promise;
import sun.net.idn.Punycode;

public class ParseYacaPages extends Job {

	private RubricURL rubric;
	private String parsingURL;
	private boolean startFromBegining;
	private int startParsingPage;
	private int endParsingPage;
	private int startParsingSiteNumber;
	private List<String> domainsFromDb;
	private List<DomainZone> domainZones;
	private List<String> savedDomains;
	private boolean isJobFinished = false;

	public ParseYacaPages(RubricURL rubric, String parsingURL, boolean startFromBegining) {
		this.rubric = rubric;
		this.parsingURL = parsingURL;
		this.startFromBegining = startFromBegining;
	}

	public void doJob() {
		initJob();
		calculateParsingPageIndexes();
		parseRubricUrl(this.parsingURL);
		if (!this.isJobFinished) {
			makeRubricUrlChecked();
		}
	}

	private void initJob() {
		domainsFromDb = populateDomainsFromDB();
		domainZones = populateDomainZonesFromDB();
		savedDomains = new ArrayList<String>();

		if (startFromBegining) {
			RubricURL rubricURL = RubricURL.findById(this.rubric.id);
			rubricURL.checked_sites_count = 0;
			rubricURL.checked_sites_percent = 0;
			rubricURL.saved_domains_count = 0;
			rubricURL.save();
			this.rubric = rubricURL;
		}
	}

	private void calculateParsingPageIndexes() {
		this.startParsingPage = (int) Math.floor(this.rubric.checked_sites_count / 10);

		if (this.rubric.sites_count <= 1000) {
			this.endParsingPage = (int) Math.ceil(this.rubric.sites_count / 10) + 1;
		} else {
			this.endParsingPage = 100;
		}
		int chislo = this.rubric.checked_sites_count;
		if (chislo > 10) {
			int ostatok = chislo % 10;
			this.startParsingSiteNumber = ostatok;
		} else {
			this.startParsingSiteNumber = chislo;
		}

	}

	private void parseRubricUrl(String parsingURL) {
		for (int i = startParsingPage; i < endParsingPage; i++) {
			if (!this.isJobFinished) {
				Document doc;
				try {
					doc = Jsoup.connect(parsingURL + this.rubric.URL + "/" + i + ".html").timeout(30 * 1000)
							.cookie(GlobalConstants.YACA_REGION_ID_PARAM, GlobalConstants.YACA_MOSCOW_REGION_ID).get();
					Elements sites = CatalogPageParser.parseSitesElements(doc);
					for (int j = startParsingSiteNumber; j < sites.size(); j++) {
						Element siteElement = sites.get(j);
						if (!this.isJobFinished) {
							String url = CatalogPageParser.parseUrl(siteElement);
							System.out.println("url=" + url + " PURL=" + parsingURL + i);
							if (checkDomainNameExists(url) || !CommonUtils.isDomainNameApplicable(url)) {
								updateRubricCheckedSites();
								continue;
							}
							String idnURL = IDN.toASCII(url);
							Domain domain = new Domain(url, idnURL);
							domain.setCatalogPageParams(siteElement);
							domain.setYacaSitePageParams(idnURL.toUpperCase());

							domain.pr = -1;
							domain.web_archive_count = -1;
							domain.in_index = "-1";

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
							updateRubricCheckedSitesAndDomains();
						}
					}
					startParsingSiteNumber = 0;
				} catch (Exception e) {
					setExceptionStatus();
					this.isJobFinished = true;
					restartJob();
					e.printStackTrace();
				}
			}
		}
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

	private void updateRubricCheckedSites() {
		RubricURL rubric = RubricURL.findById(this.rubric.id);
		rubric.checked_sites_count = rubric.checked_sites_count + 1;
		rubric.checked_sites_percent = (int) (((double) rubric.checked_sites_count / (double) this.rubric.sites_count) * 100);
		rubric.save();
		this.rubric = rubric;
	}

	private void updateRubricCheckedSitesAndDomains() {
		RubricURL rubric = RubricURL.findById(this.rubric.id);
		rubric.checked_sites_count = rubric.checked_sites_count + 1;
		rubric.checked_sites_percent = (int) (((double) rubric.checked_sites_count / (double) this.rubric.sites_count) * 100);
		rubric.saved_domains_count = rubric.saved_domains_count + 1;
		rubric.save();
		this.rubric = rubric;
	}

	private void setExceptionStatus() {
		RubricURL rubric = RubricURL.findById(this.rubric.id);
		rubric.status = new RubricURLStatus(3);
		rubric.save();
	}

	private void makeRubricUrlChecked() {
		RubricURL rubric = RubricURL.findById(this.rubric.id);
		if (rubric.sites_count <= 1000) {
			if (rubric.checked_sites_percent < 100) {
				rubric.status = new RubricURLStatus(4);
			} else {
				rubric.status = new RubricURLStatus(5);
			}
		} else {
			if (rubric.parsing_status.id == 1) {
				rubric.status = new RubricURLStatus(4);
			}
			if (rubric.parsing_status.id == 2) {
				rubric.status = new RubricURLStatus(4);
			}
			if (rubric.parsing_status.id == 3) {
				rubric.status = new RubricURLStatus(5);
				rubric.checked_sites_percent = 100;
			}
		}
		rubric.save();
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

	private void restartJob() {
		new ParseYacaPages(this.rubric, this.parsingURL, false).in(30);
	}

}
