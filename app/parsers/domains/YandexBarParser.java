package parsers.domains;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import common.GlobalConstants;

public class YandexBarParser {

	public static int parseTicFromBar(String url) throws Exception {
		int tic = -1;
		String query = GlobalConstants.YANDEX_BAR_URL + url;
		Document doc = Jsoup.connect(query)
				.cookie(GlobalConstants.YACA_REGION_ID_PARAM, GlobalConstants.YACA_MOSCOW_REGION_ID).timeout(15 * 1000)
				.get();
		String ticString = doc.getElementsByTag("tcy").get(0).attr("value");
		tic = Integer.parseInt(ticString);
		return tic;
	}

}
