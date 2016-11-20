package parsers.domains;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CatalogPageParser {
	
	public static Elements parseSitesElements(Document doc) {
		return doc.getElementsByClass("b-result__item");
	}
	
	public static String parseUrl(Element siteElement) {
		return siteElement.getElementsByClass("b-result__url").text();
	}
	
	public static String parseDescription(Element siteElement) {
		return siteElement.getElementsByClass("b-result__head").tagName("a").get(0).text();
	}
	
	public static String parseRegion(Element siteElement) {
		return siteElement.getElementsByClass("b-result__region").text();
	}
	
}
