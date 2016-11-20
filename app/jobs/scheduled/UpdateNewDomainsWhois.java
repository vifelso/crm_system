package jobs.scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import models.Domain;
import models.DomainZone;
import models.Log;
import models.Setting;

import common.CommonUtils;
import common.GlobalConstants;

import play.jobs.Job;
import play.jobs.On;

public class UpdateNewDomainsWhois extends Job {
	private List<Domain> domains;
	private boolean jobFinished = false;
	private int checkedDomainsCount;

	public UpdateNewDomainsWhois(int checkedDomainsCount) {
		this.checkedDomainsCount = checkedDomainsCount;
	}

	public void doJob() {
		initJob();
		System.out.println("6. Update new domains whois: left domains count=" + domains.size());
		for (Domain domain : domains) {
			try {
				if (!this.jobFinished) {
					String[] checkedZones = GlobalConstants.CHECKED_DOMAIN_ZONES;
					boolean isZoneInCheckedList = false;
					for (int i = 0; i < checkedZones.length; i++) {
						if (checkedZones[i].equals(domain.domain_zone.name)) {
							System.out.println("6. Update new domains whois: domain=" + domain.name);
							CommonUtils.updateDomainWhois(domain);
							DomainZone zone = DomainZone.find("name", domain.domain_zone.name).first();
							zone.checked_domains_count = zone.checked_domains_count + 1;
							zone.save();
							checkedDomainsCount = checkedDomainsCount + 1;
							isZoneInCheckedList = true;
							break;
						}
					}
					if (isZoneInCheckedList) {
						Thread.sleep(2500);
					}
				}
			} catch (Exception e) {
				saveException(domain, e);
				this.jobFinished = true;
				restartJob();
				e.printStackTrace();
			}
		}

		if (!this.jobFinished) {
			Log log = Log.findById((long) 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			log.newdomains_whois_update_result = "Завершено: " + sdf.format(new Date()) + " / "
					+ "Проверено доменов: " + checkedDomainsCount;
			log.save();
			System.out.println("SCHEDULED JOBS FINISHED");
		}
	}

	private void initJob() {
		this.domains = Domain.find("paid_till = '3000-01-01'").fetch();
	}

	private void saveException(Domain domain, Exception e) {
		Log log = Log.findById((long) 1);
		log.newdomains_whois_update_result = "Ошибка обновления домена: " + domain.name + "\n" + "Код ошибки: "
				+ e.toString();
		log.save();
	}

	private void restartJob() {
		new UpdateWhois(checkedDomainsCount).in(30);
	}

}
