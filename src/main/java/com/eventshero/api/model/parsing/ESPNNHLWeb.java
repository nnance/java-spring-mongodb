package com.eventshero.api.model.parsing;

public class ESPNNHLWeb extends ESPNWeb {

	protected int getSeasonStartMonth() {
		return 10;
	}

	protected int getSeasonStartYear() {
		return 2013;
	}
	
//	protected String getLogoUrl(Document doc, EventCalendar cal) {
//		return "http://lh6.ggpht.com/NCtejPKUkwN76y7JDbWk-iNl1N3Z77SPo-4I5BeAQwvt50rQdD-Ki-ZFTYzKz853zC6ATa5intSy-juKlSv8sA";
//	}

}
