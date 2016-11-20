package jobs.scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import models.Domain;
import models.Log;
import models.Setting;
import play.jobs.Job;
import play.jobs.On;

@On("0 0 0 * * ?")
public class UpdateDeletedDomains extends Job {

	private List<Domain> domains;
	private boolean jobFinished = false;
	private int checkedDomainsCount;

	public UpdateDeletedDomains(int checkedDomainsCount) {
		this.checkedDomainsCount = checkedDomainsCount;
	}

	public void doJob() {
		initJob();
		System.out.println("1. Update deleted domains: left domains count=" + domains.size());
		for (Domain checkedDomain : domains) {
			try {
				if (!this.jobFinished) {
					Domain domain = Domain.findById(checkedDomain.id);
					System.out.println("1. Update deleted domains: domain=" + domain.name);
					domain.setYacaSitePageParams(checkedDomain.idn_url.toUpperCase());
					domain.tic_updated = new Date();
					domain.save();
					checkedDomainsCount = checkedDomainsCount + 1;
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				saveException(checkedDomain, e);
				this.jobFinished = true;
				restartJob();
				e.printStackTrace();
			}
		}

		if (!this.jobFinished) {
			Log log = Log.findById((long) 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			log.deleted_domains_update_result = "Завершено: " + sdf.format(new Date()) + " / " + "Проверено доменов: "
					+ checkedDomainsCount;
			log.save();
			new UpdateWhois(0).in(60);
		}
	}

	private void initJob() {
		int interval = ((Setting) Setting.findById((long) 1)).update_del_domains_days_interval;
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - interval);
		this.domains = Domain.find("DATE(tic_updated) < ? and status_id = 5 order by id asc", cal.getTime()).fetch();
	}

	private void saveException(Domain domain, Exception e) {
		Log log = Log.findById((long) 1);
		log.deleted_domains_update_result = "Ошибка обновления домена: " + domain.name + "\n" + "Код ошибки: "
				+ e.toString();
		log.save();
	}

	private void restartJob() {
		new UpdateDeletedDomains(checkedDomainsCount).in(30);
	}
}