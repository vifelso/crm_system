package jobs;

import java.util.ArrayList;
import java.util.List;

import common.CommonUtils;

import models.Domain;
import models.DomainZone;
import models.DomainZoneStatus;
import parsers.zones.RuSuRfParser;
import play.jobs.Job;

public class ParseDomainZoneInfo extends Job {

	private DomainZone domainZone;
	private boolean startFromBegining;
	private List<Domain> domains;
	private int startDomainNumber;
	private boolean isJobFinished = false;

	public ParseDomainZoneInfo(DomainZone zone, boolean startFromBegining) {
		this.domainZone = zone;
		this.startFromBegining = startFromBegining;
	}

	public void doJob() {
		initJob();
		for (int i = startDomainNumber; i < domains.size(); i++) {
			Domain domain = domains.get(i);
			try {
				if (!this.isJobFinished) {
					System.out.println("Domain=" + domain.name);
					CommonUtils.updateDomainWhois(domain);
					recalculateCheckedDomainsCount();
					Thread.sleep(2200);
				}
			} catch (Exception e) {
				setExceptionStatus();
				this.isJobFinished = true;
				restartJob();
				e.printStackTrace();
			}
		}

		if (!this.isJobFinished) {
			makeDomainZoneChecked();
			System.out.println("ZONE PARSING FINISHED");
		}
	}

	private void initJob() {
		this.domains = Domain.find("domain_zone_id = ? order by id asc", this.domainZone.id).fetch();
		updateDomainZoneDomainsCount(domains.size());
		if (startFromBegining) {
			DomainZone zone = DomainZone.findById(this.domainZone.id);
			zone.checked_domains_count = 0;
			zone.save();
			this.domainZone = zone;
		}
		this.startDomainNumber = this.domainZone.checked_domains_count;
	}

	private void updateDomainZoneDomainsCount(int domainsCount) {
		DomainZone zone = DomainZone.findById(this.domainZone.id);
		zone.domains_count = domainsCount;
		zone.save();
		this.domainZone = zone;
	}

	private void recalculateCheckedDomainsCount() {
		DomainZone zone = DomainZone.findById(this.domainZone.id);
		zone.checked_domains_count = zone.checked_domains_count + 1;
		zone.save();
		this.domainZone = zone;
	}

	private void setExceptionStatus() {
		DomainZone zone = DomainZone.findById(this.domainZone.id);
		zone.status = new DomainZoneStatus(5);
		zone.save();
	}

	private void restartJob() {
		new ParseDomainZoneInfo(this.domainZone, false).in(30);
	}

	private void makeDomainZoneChecked() {
		DomainZone zone = DomainZone.findById(this.domainZone.id);
		zone.status = new DomainZoneStatus(6);
		zone.save();
	}

}
