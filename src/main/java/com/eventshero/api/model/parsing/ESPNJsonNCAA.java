package com.eventshero.api.model.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eventshero.api.model.parsing.ESPNJson.TeamResult.TeamRow;

public class ESPNJsonNCAA extends ESPNJson {

	protected List<TeamRow> getTeams(String url) throws IOException {
		Document doc = Jsoup.connect(url).timeout(10000).get();
		List<TeamRow> result = new ArrayList<TeamRow>();
		Elements selects = doc.getElementsByClass("medium-logos");
		for (Element select : selects) {
			TeamRow row = null;
			Elements options = select.getElementsByTag("a");
			for (Element option : options) {
				if (option.className().equalsIgnoreCase("bi")) {
					row = new TeamRow();
					row.name = option.html();
				}
				if (option.html().equalsIgnoreCase("schedule")) {
					String teamId = option.attr("href").split("=")[1];
					row.value = "7_" + teamId;
					row.logoUrl = "http://a.espncdn.com/combiner/i?img=/i/teamlogos/ncaa/500/"+teamId+".png?w=50&h=50&transparent=true";
					row.url = "http://espn.go.com" + option.attr("href");
					result.add(row);
				}
			}
		}
		return result;
	}
	
}
