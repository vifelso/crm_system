package jobs.scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import common.CommonUtils;
import common.GlobalConstants;

import models.Domain;
import models.DomainZone;
import models.Log;
import models.Setting;
import parsers.domains.YacaSitePagePaser;
import play.jobs.Job;
import play.jobs.On;

public class UpdateTic extends Job {

	private List<Domain> domains;
	private boolean jobFinished = false;
	private int checkedDomainsCount;

	public UpdateTic(int checkedDomainsCount) {
		this.checkedDomainsCount = checkedDomainsCount;
	}

	public void doJob() {
		initJob();
		System.out.println("4. Update tic: left domains count=" + domains.size());
		for (Domain checkedDomain : domains) {
			try {
				if (!this.jobFinished) {
					Domain domain = Domain.findById(checkedDomain.id);
					System.out.println("4. Update tic: domain=" + domain.name);
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
			log.tic_update_result = "Завершено: " + sdf.format(new Date()) + " / " + "Проверено доменов: "
					+ checkedDomainsCount;
			log.save();
			new UpdateYaca(0).in(60);
		}
	}

	private void initJob() {
		int update_before_days = ((Setting) Setting.findById((long) 1)).update_domain_days_before_free;
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + update_before_days);
		this.domains = Domain.find(
				"paid_till < ? and DATE(tic_updated) < CURDATE() order by id asc", cal.getTime())
				.fetch();
	}

	private void saveException(Domain domain, Exception e) {
		Log log = Log.findById((long) 1);
		log.tic_update_result = "Ошибка обновления домена: " + domain.name + "\n" + "Код ошибки: " + e.toString();
		log.save();
	}

	private void restartJob() {
		new UpdateTic(checkedDomainsCount).in(30);
	}

}
