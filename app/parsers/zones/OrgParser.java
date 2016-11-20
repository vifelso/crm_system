package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.IDN;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import common.CommonUtils;
import common.GlobalConstants;

public class OrgParser {

	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.name);
		Document doc = Jsoup
				.connect(
						GlobalConstants.WHOIS_INTERNIC_PREFIX + IDN.toASCII(domainUrl)
								+ GlobalConstants.WHOIS_INTERNIC_POSTFIX).timeout(30 * 1000).get();
		Element descriptionElement = doc.select("pre").get(0);
		String descriptionText = descriptionElement.text();
		domain.created = parseCreatedDate(descriptionText);
		domain.paid_till = parsePaidTillDate(descriptionText);
		String registratorId = parseRegistrator(descriptionText);
		domain.registrator = CommonUtils.getRegistratorNameById(registratorId);
		return domain;
	}

	private static Date parseCreatedDate(String descriptionText) throws Exception {
		Date date = CommonUtils.getFakeDate();
		Matcher m = Pattern.compile("(Created On:)(.+?)(\n)").matcher(descriptionText);
		while (m.find()) {
			String dateString = m.group().substring(11, 22);
			SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en,US"));
			date = format.parse(dateString);
		}
		return date;
	}

	private static Date parsePaidTillDate(String descriptionText) throws Exception {
		Date date = CommonUtils.getFakeDate();
		Matcher m = Pattern.compile("(Expiration Date:)(.+?)(\n)").matcher(descriptionText);
		while (m.find()) {
			String dateString = m.group().substring(16, 27);
			SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en,US"));
			date = format.parse(dateString);
		}
		return date;
	}

	private static String parseRegistrator(String descriptionText) throws Exception {
		String registrator = "";
		Matcher m = Pattern.compile("(Sponsoring Registrar:)(.+?)(\n)").matcher(descriptionText);
		while (m.find()) {
			registrator = m.group().substring(21).trim();
		}
		return registrator;
	}

}
