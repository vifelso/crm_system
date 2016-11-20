package parsers.zones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import models.Domain;

import common.CommonUtils;

public class TvParser {

	public static Domain parseWhois(Domain domain) throws Exception {
		String domainUrl = CommonUtils.removeWWW(domain.idn_url);
		Socket s = new Socket("whois.nic.tv", 43);
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
