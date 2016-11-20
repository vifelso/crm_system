package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

import models.Domain;

import common.CommonUtils;

public class MobiParser {
	
	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.dotmobiregistry.net", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("Created On:")) {
					String dateString = line.substring(11).trim();
					domain.created = new Date(dateString);
				}
				if (line.startsWith("Expiration Date:")) {
					String dateString = line.substring(16).trim();
					domain.paid_till = new Date(dateString);
				}
			} else {
				break;
			}
		}
		s.close();
		return domain;
	}

}
