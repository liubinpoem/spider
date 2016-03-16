package bbs_spider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class bbs_spider {
	// start url of second hand page
	public static final String url = "https://bbs.sjtu.edu.cn/bbstdoc,board,Secondhand.html";

	// the prefix of each link
	public static final String prefix_second_hand = "https://bbs.sjtu.edu.cn/";

	// prefix of the pages that contains the items of tiezi
	public static final String prefix_links_page = "https://bbs.sjtu.edu.cn/bbstdoc,board,Secondhand,page,";

	public static final String[] keywords = { "捷安特", "美利达", "山地", "公路", "电动", "小龟", "大龟","自行车"};

	public static int getPageNumber(String start_url) throws IOException {
		int page_number = 0;
		Connection con = Jsoup.connect(start_url);
		Document bbs_doc = con.get();
		Object[] as = bbs_doc.getElementsByTag("a").toArray();
		String page_link = Jsoup.parse(as[as.length - 6].toString()).getElementsByTag("a").attr("href");
		String page_num = page_link.substring(page_link.length() - 9, page_link.length() - 5);
		// System.out.println(page_link);
		// System.out.println(page_num);
		page_number = Integer.parseInt(page_num);
		return page_number;
	}

	public static List<tiezi_info> getFirstPageUrls(String start_url) throws IOException {
		List<tiezi_info> tiezi_list = new LinkedList<tiezi_info>();
		Connection con = Jsoup.connect(start_url);
		Document bbs_doc = con.get();
		Elements ele = bbs_doc.getElementsByTag("tr");
		int flag = 0;
		for (Element element : ele) {
			if ((flag >= 1) && (flag < ele.size() - 4)) {
				Object[] tds = element.getElementsByTag("td").toArray();
				String author = Jsoup.parse(tds[2].toString()).text();
				String date = Jsoup.parse(tds[3].toString()).text();
				String title = Jsoup.parse(tds[4].toString()).text();
				String link = prefix_second_hand + Jsoup.parse(tds[4].toString()).getElementsByTag("a").attr("href");
				// System.out.println(author + '\t' + date + '\t' + title + '\t'
				// + link);
				tiezi_info tiezi = new tiezi_info(title, author, date, link);
				boolean filter_result = filter(title, keywords);
				if (filter_result) {
					tiezi_list.add(tiezi);
				}
			}
			flag++;
		}
		return tiezi_list;
	}

	public static List<tiezi_info> getNonFirstPageUrls(String page_url) throws IOException {
		List<tiezi_info> tiezi_list = new LinkedList<tiezi_info>();
		Connection con = Jsoup.connect(page_url);
		Document bbs_doc = con.get();
		Elements ele = bbs_doc.getElementsByTag("tr");
		int flag = 0;
		for (Element element : ele) {
			if ((flag >= 1)) {
				Object[] tds = element.getElementsByTag("td").toArray();
				String author = Jsoup.parse(tds[2].toString()).text();
				String date = Jsoup.parse(tds[3].toString()).text();
				String title = Jsoup.parse(tds[4].toString()).text();
				String link = prefix_second_hand + Jsoup.parse(tds[4].toString()).getElementsByTag("a").attr("href");
				// System.out.println(author + '\t' + date + '\t' + title + '\t'
				// + link);
				tiezi_info tiezi = new tiezi_info(title, author, date, link);
				boolean filter_result = filter(title, keywords);
				if (filter_result) {
					tiezi_list.add(tiezi);
				}
			}
			flag++;
		}
		return tiezi_list;
	}

	public static boolean filter(String title, String[] keywords) {
		boolean flag = false;
		for (int i = 0; i < keywords.length; i++) {
			if ((title.contains(keywords[i])) && (title.contains("出"))) {
				if (!title.contains("已出")) {
					flag = true;
				}
				// System.out.println(title+"\t"+keywords[i]);
			}
		}
		return flag;
	}

	public static List<tiezi_info> Run(int pages_num) throws IOException {
		int page_number = getPageNumber(url);
		List<String> first_list = new LinkedList<String>();
		List<tiezi_info> second_list = new LinkedList<tiezi_info>();
		for (int i = 1; i < pages_num; i++) {
			first_list.add(prefix_links_page + String.valueOf(page_number - i) + ".html");
		}

		second_list.addAll(getFirstPageUrls(url));

		for (String link : first_list) {
			second_list.addAll(getNonFirstPageUrls(link));
		}

		for (tiezi_info tiezi : second_list) {
			tiezi.Details();
		}

		return second_list;

	}

	public static void main(String args[]) throws IOException, InterruptedException {
		for (int i = 0; i < 100000000; i++) {
			Run(15);
			Random rd = new Random();
			int sleeptime = rd.nextInt(600000) + 60000;
			System.out.println("sleep_time:"+sleeptime/60000+"minutes");
			Thread.sleep(sleeptime);
		}
	}
}
