package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;

import models.Domain;

import common.CommonUtils;

public class UaExpiresParser {
	
	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.com.ua", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("expires:")) {
					String expires = line.substring(8).trim();
					String dateString = expires.substring(0, 10);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
