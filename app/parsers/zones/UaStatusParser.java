package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import models.Domain;

import common.CommonUtils;

public class UaStatusParser {

	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		if (!"whois.com.ua".equals(domainUrl)) {
			Socket s = new Socket("whois.com.ua", 43);
			s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
			while (true) {
				String line = br.readLine();
				if (line != null) {
					if (line.startsWith("status:")) {
						String statusLine = line.trim();
						String dateString = statusLine.substring(statusLine.lastIndexOf(" ") + 1, statusLine.lastIndexOf(" ") + 9);
						if (!"00000000".equals(dateString)) {
							SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
							domain.paid_till = format.parse(dateString);
						}
					}
				} else {
					break;
				}
			}
			s.close();
		}
		return domain;
	}

}
