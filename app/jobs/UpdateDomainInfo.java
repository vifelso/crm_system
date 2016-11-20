package jobs;

import java.util.Date;
import java.util.Map;

import common.CommonUtils;
import common.GlobalConstants;

import models.Domain;
import models.DomainZone;
import play.jobs.Job;

public class UpdateDomainInfo extends Job {

	private Long domainId;
	private boolean jobFinished = false;

	public UpdateDomainInfo(Long id) {
		this.domainId = id;
	}

	public void doJob() throws Exception {
		try {
			Domain domain = Domain.findById(domainId);
			String idnURL = domain.idn_url;
			domain.setYacaSitePageParams(idnURL.toUpperCase());
			domain.setPR(idnURL);
			domain.setWebArcivewCount(idnURL);
			domain.tic_updated = new Date();
			domain.params_updated = new Date();
			if (!this.jobFinished) {
				domain.save();
				String[] checkedZones = GlobalConstants.CHECKED_DOMAIN_ZONES;
				boolean isZoneInCheckedList = false;
				for (int k = 0; k < checkedZones.length; k++) {
					if (checkedZones[k].equals(domain.domain_zone.name)) {
						isZoneInCheckedList = true;
					}
				}
				if (isZoneInCheckedList) {
					CommonUtils.updateDomainWhois(domain);
				}
			}
		} catch (Exception e) {
			this.jobFinished = true;
			restartJob();
			e.printStackTrace();
		}
	}

	private void restartJob() {
		new UpdateDomainInfo(domainId).in(10);
	}
}
