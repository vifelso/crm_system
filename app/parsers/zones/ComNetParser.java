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

public class ComNetParser {

	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.internic.net", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("   Creation Date: ")) {
					String dateString = line.substring(18).trim();
					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en,US"));
					domain.created = format.parse(dateString);
				}
				if (line.startsWith("   Expiration Date: ")) {
					String dateString = line.substring(20).trim();
					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en,US"));
					domain.paid_till = format.parse(dateString);
				}
				if (line.startsWith("   Registrar: ")) {
					String registratorId = line.substring(14).trim();
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