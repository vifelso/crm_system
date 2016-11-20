package jobs.scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jobs.ParseDomainZoneInfo;

import models.Domain;
import models.DomainZone;
import models.Log;
import models.Setting;

import common.CommonUtils;
import common.GlobalConstants;

import play.jobs.Job;
import play.jobs.On;

public class UpdateWhois extends Job {

	private List<Domain> domains;
	private boolean jobFinished = false;
	private int checkedDomainsCount;

	public UpdateWhois(int checkedDomainsCount) {
		this.checkedDomainsCount = checkedDomainsCount;
	}

	public void doJob() {
		initJob();
		System.out.println("2. Update whois: left domains count=" + domains.size());
		for (Domain domain : domains) {
			try {
				if (!this.jobFinished) {
					String[] checkedZones = GlobalConstants.CHECKED_DOMAIN_ZONES;
					for (int i = 0; i < checkedZones.length; i++) {
						if (checkedZones[i].equals(domain.domain_zone.name)) {
							System.out.println("2. Update whois: domain=" + domain.name);
							CommonUtils.updateDomainWhois(domain);
							checkedDomainsCount = checkedDomainsCount + 1;
						}
					}
					Thread.sleep(2500);
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
			log.whois_update_result = "Завершено: " + sdf.format(new Date()) + " / " + "Проверено доменов: "
					+ checkedDomainsCount;
			log.save();
			new UpdateParams(0).in(60);
		}
	}

	private void initJob() {
		int update_before_days = ((Setting) Setting.findById((long) 1)).update_domain_days_before_free;
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + update_before_days);
		this.domains = Domain.find(
				"paid_till < ? and DATE(whois_updated) < CURDATE() order by id asc", cal.getTime())
				.fetch();
	}

	private void saveException(Domain domain, Exception e) {
		Log log = Log.findById((long) 1);
		log.whois_update_result = "Ошибка обновления домена: " + domain.name + "\n" + "Код ошибки: " + e.toString();
		log.save();
	}

	private void restartJob() {
		new UpdateWhois(checkedDomainsCount).in(30);
	}

}
