package parsers.domains;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebArchiveParser {

	public static int getWebArchivesCount(String url) throws Exception {
		int webArchivesCount = 0;
		String query = "http://wayback.archive.org/web/*/" + url;
		Document doc = Jsoup.connect(query).timeout(60 * 1000).ignoreHttpErrors(true).get();
		Element metaElement = doc.getElementById("wbMeta");
		if (metaElement != null) {
			String archivesCountElement = metaElement.getElementsByTag("strong").get(0).text();
			String archivesCountString = archivesCountElement.substring(0, archivesCountElement.length() - 6);
			webArchivesCount = Integer.parseInt(archivesCountString.replaceAll(",", ""));
		}
		return webArchivesCount;

	}

}
