package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import models.Domain;

import common.CommonUtils;

public class ComTrParser {
	
	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.metu.edu.tr", 43);
		s.getOutputStream().write((domainUrl + "\r\n").getBytes("iso-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "iso-8859-1"));
		while (true) {
			String line = br.readLine();
			if (line != null) {
				if (line.startsWith("Created on..............:")) {
					String formatedLine = line.substring(25).trim();
					String dateString = formatedLine.substring(0, 11);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd", new Locale("en,US"));
					domain.created = format.parse(dateString);
				}
				if (line.startsWith("Expires on..............:")) {
					String formatedLine = line.substring(25).trim();
					String dateString = formatedLine.substring(0, 11);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd", new Locale("en,US"));
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
