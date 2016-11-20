package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;

import models.Domain;

import common.CommonUtils;

public class EeParser {

	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.eenet.ee", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("registered:")) {
					String registered = line.substring(11).trim();
					String dateString = registered.substring(0, 10);
					SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
					domain.created = format.parse(dateString);
				}
				if (line.startsWith("expire:")) {
					String expire = line.substring(7).trim();
					String dateString = expire.substring(0, 10);
					SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
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
