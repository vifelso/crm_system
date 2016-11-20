package parsers.domains;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.GlobalConstants;

public class YandexIndexParser {

	/*public static String getYandexIndexCount(String url) throws Exception {
		String inIndex = "0";
		String query = GlobalConstants.YANDEX_IN_INDEX_URL + url + "&lr=0";
		Document doc = Jsoup
				.connect(query)
				.cookie("yandexuid", "1071327091328358176")
				.cookie("fuid01",
						"4f2d232031c85897.N35tT-NgihIimWs7rawlIjQcUBBDT_pXlBQ7dBkYrxJxhZCZ64-x3hYYIjsu1ZJYzayZlPFBtv5sR7RvoErEaYearemN9rLaK2pHqny9hWfWjsK8LcwXSdXMj0UIKO9K")
				.cookie("spravka",
						"dD0xMzI4MzU5ODU2O2k9MTc4LjEyMy4xMTMuMjA0O3U9MTMyODM1OTg1NjE4Nzc2NDEyMTtoPTk4YzdhZDI5Mzk2MTE4MTBmODdlMmFmYzg0ZTQwYzkw")
				.cookie("yandex_gid", "213")
				.cookie("yp", "2147483647.ygo.155%3A213")
				.cookie("yabs-frequency", "/3/Tm805_0BFCki1WNm2oD5hGO5y0ik/")
				.cookie("aw", "1_UeJxi5GAAgf8AAAAA//8DAAFHAQkA#A#")
				.cookie("t", "p")
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0) Gecko/20100101 Firefox/10.0")
				.timeout(30 * 1000).get();
		String logoText = doc.getElementsByClass("b-head-logo__text").get(0).text();
		if (!logoText.contains("Найдётся")) {
			inIndex = logoText.substring(8, logoText.indexOf(" отв"));
		}
		return inIndex;
	}*/
}
