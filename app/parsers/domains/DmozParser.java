package parsers.domains;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DmozParser {

	public static int getDmozCategoriesCount(String url) throws Exception {
		int categoriesCount = 0;
		url = clearURL(url);
		String query = "http://www.dmoz.org/search?q=" + url;
		Document doc = Jsoup.connect(query).timeout(60 * 100).get();
		Elements categoriesHeaders = doc.getElementsByTag("h3");
		if (categoriesHeaders.size() > 0) {
			String categoriesString = categoriesHeaders.get(0).getElementsByTag("small").get(0).text();
			categoriesCount = Integer.parseInt(categoriesString.substring(categoriesString.indexOf("of ") + 3,
					categoriesString.length() - 1));
		}

		return categoriesCount;
	}

	private static String clearURL(String url) {
		if (url.startsWith("www.")) {
			url = url.substring(4, url.length());
		}
		return url;
	}

}
