package common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

import parsers.zones.AeroParser;
import parsers.zones.AmParser;
import parsers.zones.BizParser;
import parsers.zones.ByParser;
import parsers.zones.CcParser;
import parsers.zones.CoIlParser;
import parsers.zones.ComTrParser;
import parsers.zones.CzParser;
import parsers.zones.EduParser;
import parsers.zones.EeParser;
import parsers.zones.FiParser;
import parsers.zones.FmParser;
import parsers.zones.InParser;
import parsers.zones.InfoParser;
import parsers.zones.ComNetParser;
import parsers.zones.ItParser;
import parsers.zones.MdParser;
import parsers.zones.MeParser;
import parsers.zones.MobiParser;
import parsers.zones.NicRuParser;
import parsers.zones.OrgParser;
import parsers.zones.PlParser;
import parsers.zones.ProParser;
import parsers.zones.RuSuRfParser;
import parsers.zones.TarvelParser;
import parsers.zones.TvParser;
import parsers.zones.UaExpiresParser;
import parsers.zones.UaRexpiresParser;
import parsers.zones.UaStatusParser;
import parsers.zones.UsParser;
import parsers.zones.UzParser;
import parsers.zones.WsParser;

import models.Domain;
import models.DomainStatus;
import models.DomainZone;
import models.RegistratorName;

public class CommonUtils {

	public static void updateDomainWhois(Domain domain) throws Exception {
		Domain parsedDomain = null;
		Date oldFreeDate = domain.paid_till;
		String dz = domain.domain_zone.name;
		if ("ru".equals(dz) || "su".equals(dz) || "рф".equals(dz)) {
			parsedDomain = RuSuRfParser.parseWhois(domain);
		}
		if ("com".equals(dz) || "net".equals(dz)) {
			parsedDomain = ComNetParser.parseWhois(domain);
		}
		if ("org".equals(dz)) {
			parsedDomain = OrgParser.parseWhois(domain);
		}
		if ("info".equals(dz)) {
			parsedDomain = InfoParser.parseWhois(domain);
		}
		if ("com.ua".equals(dz) || "ua".equals(dz) || "kiev.ua".equals(dz) || "org.ua".equals(dz)
				|| "gov.ua".equals(dz) || "od.ua".equals(dz) || "in.ua".equals(dz) || "dp.ua".equals(dz)
				|| "lviv.ua".equals(dz) || "ck.ua".equals(dz) || "odessa.ua".equals(dz) || "crimea.ua".equals(dz)
				|| "edu.ua".equals(dz) || "if.ua".equals(dz) || "cv.ua".equals(dz) || "ks.ua".equals(dz)
				|| "lutsk.ua".equals(dz) || "km.ua".equals(dz) || "sebastopol.ua".equals(dz) || "zt.ua".equals(dz)
				|| "kherson.ua".equals(dz) || "uz.ua".equals(dz) || "rv.ua".equals(dz) || "zhitomir.ua".equals(dz)) {
			parsedDomain = UaStatusParser.parseWhois(domain);
		}
		if ("dn.ua".equals(dz) || "kharkov.ua".equals(dz) || "zp.ua".equals(dz) || "lg.ua".equals(dz)
				|| "donetsk.ua".equals(dz) || "kh.ua".equals(dz) || "sumy.ua".equals(dz) || "lugansk.ua".equals(dz)) {
			parsedDomain = UaRexpiresParser.parseWhois(domain);
		}
		if ("pl.ua".equals(dz) || "poltava.ua".equals(dz) || "kr.ua".equals(dz)) {
			parsedDomain = UaExpiresParser.parseWhois(domain);
		}
		if ("by".equals(dz)) {
			parsedDomain = ByParser.parseWhois(domain);
		}
		if ("biz".equals(dz)) {
			parsedDomain = BizParser.parseWhois(domain);
		}
		if ("tv".equals(dz)) {
			parsedDomain = TvParser.parseWhois(domain);
		}
		if ("uz".equals(dz)) {
			parsedDomain = UzParser.parseWhois(domain);
		}
		if ("md".equals(dz)) {
			parsedDomain = MdParser.parseWhois(domain);
		}
		if ("ee".equals(dz)) {
			parsedDomain = EeParser.parseWhois(domain);
		}
		if ("am".equals(dz)) {
			parsedDomain = AmParser.parseWhois(domain);
		}
		if ("spb.ru".equals(dz) || "org.ru".equals(dz) || "com.ru".equals(dz) || "net.ru".equals(dz)
				|| "msk.ru".equals(dz) || "pp.ru".equals(dz)) {
			parsedDomain = NicRuParser.parseWhois(domain);
		}
		if ("cz".equals(dz)) {
			parsedDomain = CzParser.parseWhois(domain);
		}
		if ("ws".equals(dz)) {
			parsedDomain = WsParser.parseWhois(domain);
		}
		if ("co.il".equals(dz)) {
			parsedDomain = CoIlParser.parseWhois(domain);
		}
		if ("fm".equals(dz)) {
			parsedDomain = FmParser.parseWhois(domain);
		}
		if ("pro".equals(dz)) {
			parsedDomain = ProParser.parseWhois(domain);
		}
		if ("com.tr".equals(dz)) {
			parsedDomain = ComTrParser.parseWhois(domain);
		}
		if ("fi".equals(dz)) {
			parsedDomain = FiParser.parseWhois(domain);
		}
		if ("it".equals(dz)) {
			parsedDomain = ItParser.parseWhois(domain);
		}
		if ("travel".equals(dz)) {
			parsedDomain = TarvelParser.parseWhois(domain);
		}
		if ("me".equals(dz)) {
			parsedDomain = MeParser.parseWhois(domain);
		}
		if ("in".equals(dz)) {
			parsedDomain = InParser.parseWhois(domain);
		}
		if ("pl".equals(dz)) {
			parsedDomain = PlParser.parseWhois(domain);
		}
		if ("aero".equals(dz)) {
			parsedDomain = AeroParser.parseWhois(domain);
		}
		if ("edu".equals(dz)) {
			parsedDomain = EduParser.parseWhois(domain);
		}
		if ("us".equals(dz)) {
			parsedDomain = UsParser.parseWhois(domain);
		}
		if ("cc".equals(dz)) {
			parsedDomain = CcParser.parseWhois(domain);
		}
		if ("mobi".equals(dz)) {
			parsedDomain = MobiParser.parseWhois(domain);
		}
		Domain domainFromDB = Domain.findById(domain.id);
		domainFromDB.created = parsedDomain.created;
		domainFromDB.paid_till = parsedDomain.paid_till;
		domainFromDB.free_date = parsedDomain.free_date;
		domainFromDB.registrator = parsedDomain.registrator;
		domainFromDB.whois_updated = new Date();
		if (domain.status.id == 2 || domain.status.id == 3) {
			int result = oldFreeDate.compareTo(parsedDomain.paid_till);
			if (result != 0) {
				domainFromDB.status = new DomainStatus(1);
			}
		}
		domainFromDB.save();
	}

	public static RegistratorName getRegistratorNameById(String registratorId) {
		List<RegistratorName> namesFromDb = RegistratorName.findAll();
		RegistratorName registratorName = null;
		boolean registratorExists = false;
		for (RegistratorName rname : namesFromDb) {
			if (rname.canonical_name.equals(registratorId)) {
				registratorExists = true;
				registratorName = rname;
				break;
			}
		}

		if (!registratorExists) {
			RegistratorName newRegistratorName = new RegistratorName(registratorId);
			newRegistratorName.create();
			registratorName = RegistratorName.find("name", registratorId).first();
		}
		return registratorName;
	}

	public static boolean isDomainNameApplicable(String url) {
		boolean isDomainApplicable = true;
		if (url.contains("/")) {
			isDomainApplicable = false;
		}
		return isDomainApplicable;
	}

	public static String removeWWW(String url) {
		if (url.startsWith("www.")) {
			url = url.substring(4, url.length());
		}
		return url;
	}

	public static Date getFakeDate() {
		Calendar cal = new GregorianCalendar(3000, 00, 01);
		return cal.getTime();
	}

}
