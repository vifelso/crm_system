package parsers.zones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.GlobalConstants;
import common.CommonUtils;

import models.Domain;
import models.DomainZone;
import models.RegistratorName;

public class RuSuRfParser {

	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		if ("www.ru".equals(domain.name)) {
			domainUrl = domain.idn_url;
		}
		Socket s = new Socket("whois.tcinet.ru", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("created:")) {
					String dateString = line.substring(8).trim();
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					domain.created = format.parse(dateString);
				}
				if (line.startsWith("paid-till:")) {
					String dateString = line.substring(10).trim();
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					domain.paid_till = format.parse(dateString);
				}
				if (line.startsWith("free-date:")) {
					String dateString = line.substring(10).trim();
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					domain.free_date = format.parse(dateString);
				}
				if (line.startsWith("registrar:")) {
					String registratorId = line.substring(10).trim();
					domain.registrator = CommonUtils.getRegistratorNameById(registratorId);
				}
			} else {
				break;
			}
		}
		s.close();
		return domain;
	}
}