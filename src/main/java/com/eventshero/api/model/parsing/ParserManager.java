package com.eventshero.api.model.parsing;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class ParserManager {

	public static final String AOL_SPORTS = "AOL SportingNews";
	public static final String AOL_SPORTS_NCAAF = "AOL SportingNews NCAAF";
	public static final String AOL_SPORTS_NCAAB = "AOL SportingNews NCAAB";
	public static final String ESPN_WEB = "ESPN";
	public static final String THRILLCALL_WEB = "ThrillCall";
	public static final String EPISODE_WEB = "EpisodeCalendar";
	public static final String ESPN_NCAA = "ESPN NCAA";
	public static final String ESPN_NBA = "ESPN NBA";
	public static final String ESPN_NHL = "ESPN NHL";
	public static final String ESPN_NFL = "ESPN NFL";
	public static final String ESPN_MLB = "ESPN MLB";
	public static final String ESPN_NASCAR = "ESPN NASCAR";
	public static final String ESPN_JSON = "ESPN JSON";
	public static final String[] parsers = {ESPN_WEB,ESPN_NCAA,ESPN_NBA,ESPN_NHL,ESPN_NFL,ESPN_MLB,ESPN_NASCAR,THRILLCALL_WEB,EPISODE_WEB};

	public class ParserJson {
		@Expose public String name;
		public ParserJson(String name) {
			this.name = name;
		}
	}
	
	final public static CalendarParser getParser(String name) {
		CalendarParser result = null;
		if (name.equalsIgnoreCase(AOL_SPORTS))
			result = new AOLSportingNews();
		else if (name.equalsIgnoreCase(AOL_SPORTS_NCAAF))
			result = new AOLSportingNewsNCAAF();
		else if (name.equalsIgnoreCase(AOL_SPORTS_NCAAB))
			result = new AOLSportingNewsNCAAB();
		else if (name.equalsIgnoreCase(ESPN_WEB))
			result = new ESPNWeb();
		else if (name.equalsIgnoreCase(ESPN_NBA))
			result = new ESPNNBAWeb();
		else if (name.equalsIgnoreCase(ESPN_MLB))
			result = new ESPNMLBWeb();
		else if (name.equalsIgnoreCase(ESPN_NFL))
			result = new ESPNNFLWeb();
		else if (name.equalsIgnoreCase(ESPN_NHL))
			result = new ESPNNHLWeb();
		else if (name.equalsIgnoreCase(ESPN_NCAA))
			result = new ESPNNCAAWeb();
		else if (name.equalsIgnoreCase(ESPN_NASCAR))
			result = new ESPNNASCAR();
		else if (name.equalsIgnoreCase(ESPN_JSON))
			result = new ESPNJson();
		else if (name.equalsIgnoreCase(THRILLCALL_WEB))
			result = new ThrillCallWeb();
		else if (name.equalsIgnoreCase(EPISODE_WEB))
			result = new EpisodeCalendarWeb();
		return result;
	}
	
	final public Set<ParserJson> getList() {
		Set<ParserJson> results = new HashSet<ParserJson>();
		for (int i = 0; i < parsers.length; i++) {
			results.add(new ParserJson(parsers[i]));			
		}
		return results;
	}
}
