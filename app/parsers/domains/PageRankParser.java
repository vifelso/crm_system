package parsers.domains;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageRankParser {

	public static int getPR(String url) throws Exception {
		String hash = checkSum(url);
		String queryURL = "http://toolbarqueries.google.com/tbr?client=navclient-auto&ch=" + hash
				+ "&features=Rank&q=info:" + url;
		Document doc = Jsoup.connect(queryURL).ignoreHttpErrors(true).get();
		int pr = -1;
		if (doc.body().text().startsWith("Rank")) {
			pr = Integer.parseInt(doc.body().text().substring(9));
		}
		return pr;
	}

	private static String checkSum(String url) {
		String seed = "Mining PageRank is AGAINST GOOGLEâ€™S TERMS OF SERVICE. Yes, Iâ€™m talking to you, scammer.";
		int key = 16909125;

		for (int i = 0; i < url.length(); i++) {
			key = key ^ (int) (seed.charAt(i % seed.length())) ^ (int) (url.charAt(i));
			key = key >>> 23 | key << 9;
		}

		return "8" + toHex8(key >>> (8 & 255)) + toHex8(key & 255);
	}

	private static String toHex8(int num) {
		if (num < 16)
			return "0" + Integer.toHexString(num);
		else
			return Integer.toHexString(num);
	}

}
