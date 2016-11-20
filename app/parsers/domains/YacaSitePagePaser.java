package parsers.domains;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.CommonUtils;

public class YacaSitePagePaser {

	public static int parseTic(Document doc, String url) throws Exception {
		Element highlightImage = doc.getElementsByAttributeValueEnding("src", "arr-hilite.gif").get(0);
		Element ticTD = highlightImage.parent().nextElementSibling().nextElementSibling().nextElementSibling();
		int tic = -1;
		if (ticTD.text().length() > 0) {
			tic = Integer.parseInt(ticTD.text());
		} else {
			tic = YandexBarParser.parseTicFromBar(url);
		}
		return tic;
	}

	public static String parseMainCategory(Document doc) {
		String mainCategory = "";
		Elements rubricPathes = doc.getElementsByClass("b-path__link");
		for (int i = 1; i < rubricPathes.size(); i++) {
			mainCategory = mainCategory + rubricPathes.get(i).text();
			if (i < rubricPathes.size() - 1) {
				mainCategory = mainCategory + " / ";
			}
		}
		return mainCategory;
	}

	public static String parseAdditionalCategories(Document doc) {
		String additionalCategories = "";
		Elements categoriesDivs = doc.getElementsByClass("b-cy_links");
		if (categoriesDivs.size() > 0) {
			Element categoriesDiv = categoriesDivs.get(0);
			Elements additionalLinks = categoriesDiv.getElementsByTag("a");
			for (int i = 0; i < additionalLinks.size(); i++) {
				additionalCategories = additionalCategories + additionalLinks.get(i).text().substring(10);
				if (i < additionalLinks.size() - 1) {
					additionalCategories = additionalCategories + "\n";
				}
			}
		}
		return additionalCategories;
	}

	public static String parseYacaPageLink(Document doc) {
		String pageLink = "";
		Elements aTags = doc.getElementsByClass("b-cy_goto");
		if (aTags.size() > 0) {
			Element aTag = aTags.get(0).getElementsByTag("a").get(0);
			pageLink = aTag.attr("href");
		}
		return pageLink;
	}

}
