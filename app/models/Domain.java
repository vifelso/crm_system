package models;

import parsers.domains.CatalogPageParser;
import parsers.domains.DmozParser;
import parsers.domains.PageRankParser;
import parsers.domains.WebArchiveParser;
import parsers.domains.YacaSitePagePaser;
import parsers.domains.YandexBarParser;
import parsers.domains.YandexIndexParser;
import play.*;
import play.data.binding.As;
import play.data.binding.NoBinding;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import common.GlobalConstants;

import controllers.CRUD.Hidden;
import controllers.CRUD.Label;
import controllers.CRUD.PermissionCheck;

import java.io.IOException;
import java.util.*;

@Entity
@Table(name = "domain")
public class Domain extends Model {

	@Hidden
	@Column(insertable = false, updatable = false)
	public Long id;

	@Required
	@Column(nullable = false, unique = true)
	@Label
	public String name;

	@Label
	@Column(nullable = false)
	public int tic;

	@Label
	public int pr;

	@As("yyyy-MM-dd")
	@Column(columnDefinition = "date", nullable = false)
	@Label
	public Date created;

	@As("yyyy-MM-dd")
	@Column(columnDefinition = "date", nullable = false)
	@Label
	public Date paid_till;

	@As("yyyy-MM-dd")
	@Column(columnDefinition = "date", nullable = false)
	@Label
	public Date free_date;

	@Label
	public String main_category;

	@Hidden
	public String catalog_page;

	@MaxSize(1000)
	@Column(columnDefinition = "varchar(1000)")
	@Label
	public String additional_categories;

	@PermissionCheck(GlobalConstants.PERM_DOMAINS_EDIT)
	public String in_index;
	
	@PermissionCheck(GlobalConstants.PERM_DOMAINS_EDIT)
	public int site_traffic;

	@Label
	@Column(nullable = false)
	public String description;

	@Label
	public String region;

	@ManyToOne
	@JoinColumn(name = "web_archive_status_id")
	@PermissionCheck(GlobalConstants.PERM_DOMAINS_EDIT)
	public WebArchiveStatus web_archive_status;

	@Label
	@Column(columnDefinition = "int(11) default 0")
	public int web_archive_count;

	@ManyToOne
	@JoinColumn(name = "registrator_id", columnDefinition = "bigint(20)")
	@Label
	public RegistratorName registrator;

	@Required
	@ManyToOne
	@JoinColumn(name = "status_id", nullable = false, columnDefinition = "bigint(20) default 1")
	@Label
	public DomainStatus status;

	@As("yyyy-MM-dd HH:mm")
	@Label
	public Date whois_updated;

	@As("yyyy-MM-dd HH:mm")
	@Label
	public Date tic_updated;

	@As("yyyy-MM-dd HH:mm")
	@Label
	public Date params_updated;

	@OneToMany(mappedBy = "domain")
	@Hidden
	public List<Order> orders;

	@ManyToOne
	@JoinColumn(name = "domain_zone_id", nullable = false, columnDefinition = "bigint(20)")
	@Hidden
	public DomainZone domain_zone;

	@Hidden
	public String idn_url;

	@PostLoad
	public void postLoad() {
		if (this.registrator != null) {
			Query query = JPA
					.em()
					.createNativeQuery(
							"select registrator_id from registrator_registrator_name where registrator_name_id = :registrator_name_id");
			query.setParameter("registrator_name_id", this.registrator.id);
			List registratorNameIds = query.getResultList();
			if (registratorNameIds.size() > 0) {
				Object registratorId = registratorNameIds.get(0);
				Registrator registrator_site = Registrator.findById(Long.parseLong(registratorId.toString()));
				String site = registrator_site.site;
				String registratorLink = "<a href='#' onclick='openModal(this);' id='/registrators/"
						+ registrator_site.id
						+ "'>"
						+ site
						+ "</a>"
						+ "<a href='http://"
						+ site
						+ "' target='_blank' class='external-link'><img src='/public/images/icons/external-link.png' title='Перейти на сайт'></a>";
				this.registrator.name = registratorLink;
			}
		}
	}

	public Domain() {
	}
	
	public Domain(String url) {
		this.name = url;
	}

	public Domain(String url, String idnURL) {
		this.name = url;
		this.idn_url = idnURL;
		this.status = new DomainStatus(1);
	}

	public void setCatalogPageParams(Element siteElement) {
		this.description = CatalogPageParser.parseDescription(siteElement);
		this.region = CatalogPageParser.parseRegion(siteElement);
	}

	public void setYacaSitePageParams(String url) throws Exception {
		Document doc = Jsoup.connect(GlobalConstants.YACA_CHECK_SITE_URL + url).timeout(10 * 1000)
				.cookie(GlobalConstants.YACA_REGION_ID_PARAM, GlobalConstants.YACA_MOSCOW_REGION_ID).get();
		String pageContent = doc.getElementsByClass("l-page__content").get(0).text();
		if (!pageContent.contains(GlobalConstants.YACA_SITE_ABSENT)) {
			this.main_category = YacaSitePagePaser.parseMainCategory(doc);
			this.additional_categories = YacaSitePagePaser.parseAdditionalCategories(doc);
			this.tic = YacaSitePagePaser.parseTic(doc, url);
			this.catalog_page = YacaSitePagePaser.parseYacaPageLink(doc);
			if (this.status.id == 5) {
				this.status = new DomainStatus(1);
			}
		} else {
			this.tic = YandexBarParser.parseTicFromBar(url);
			if (this.status.id != 6) {
				this.status = new DomainStatus(5);
			}
		}
	}

	public void setPR(String url) throws Exception {
		this.pr = PageRankParser.getPR(url);
	}

	public void setWebArcivewCount(String url) throws Exception {
		this.web_archive_count = WebArchiveParser.getWebArchivesCount(url);
	}

	public String toString() {
		String deletedDomain = "";
		if (this.status != null && new Long(5).equals(this.status.id)) {
			deletedDomain = "Домен не в каталоге: ";
		}
		return deletedDomain + name;
	}

}
