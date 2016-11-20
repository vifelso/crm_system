package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import models.Domain;

import common.CommonUtils;

public class PlParser {
	
	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.dns.pl", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("created:")) {
					String formatedLine = line.substring(8).trim();
					String dateString = formatedLine.substring(0, 10);
					SimpleDateFormat format = new SimpleDateFormat("yyy.MM.dd");
					domain.created = format.parse(dateString);
				}
				if (line.startsWith("renewal date:")) {
					String formatedLine = line.substring(13).trim();
					String dateString = formatedLine.substring(0, 10);
					SimpleDateFormat format = new SimpleDateFormat("yyy.MM.dd");
					domain.paid_till = format.parse(dateString);
				}
			} else {
				break;
			}
		}
		s.close();
		return domain;
	}

}
