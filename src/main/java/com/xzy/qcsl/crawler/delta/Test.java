package com.xzy.qcsl.crawler.delta;

import java.io.File;



/**
 * @author xiangzy
 * @date 2015-8-30
 *
 */
public class Test {

	/**
	 * 
	 */
	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File directory = new File("");
		System.out.println(directory.getAbsolutePath());

	}
	private static void test1() {
//		try {
//			String url = "http://zh.delta.com/air-shopping/findFlights.action";
//			Connection conn = Jsoup.connect(url);
//			conn.timeout(30000);
//			conn.data("action", "findFlights");
//			conn.data("pageName", "advanceSearchPage");
//			conn.data("branchingOptions", "");
//			conn.data("tripType", "ONE_WAY");
//			conn.data("cacheKey", "10446f0a-4c1c-4fee-beb4-b1ddbd687cd4");
//			conn.data("preferenceItinId",
//					"_PVG-LAX-08/20/2015-Return Date-oneway-2-false-true-undefined-undefined");
//			conn.data("priceSchedule", "price");
//			conn.data("originCity", "PVG");
//			conn.data("destinationCity", "LAX");
//			conn.data("originAirportRadius", "50");
//			conn.data("destinationAirportRadius", "50");
//			conn.data("departureDate", "08/20/2015");
//			conn.data("departureTime", "AT");
//			conn.data("returnDate", "月/日/年");
//			conn.data("returnTime", "AT");
//			conn.data("flexDays", "3");
//			conn.data("originCity[1]", "PVG");
//			conn.data("destinationCity[1]", "LAX");
//			conn.data("departureDate[1]", "08/20/2015");
//			conn.data("departureTime[1]", "AT");
//			conn.data("connectionCity[1]", "");
//			conn.data("originCity[2]", "");
//			conn.data("destinationCity[2]", "");
//			conn.data("departureDate[2]", "月/日/年");
//			conn.data("departureTime[2]", "AT");
//			conn.data("connectionCity[2]", "");
//			conn.data("originCity[3]", "");
//			conn.data("destinationCity[3]", "");
//			conn.data("departureDate[3]", "月/日/年");
//			conn.data("departureTime[3]", "AT");
//			conn.data("connectionCity[3]", "");
//			conn.data("originCity[4]", "");
//			conn.data("destinationCity[4]", "");
//			conn.data("departureDate[4]", "月/日/年");
//			conn.data("departureTime[4]", "AT");
//			conn.data("connectionCity[4]", "");
//			conn.data("originCity[5]", "");
//			conn.data("destinationCity[5]", "");
//			conn.data("departureDate[5]", "月/日/年");
//			conn.data("departureTime[5]", "AT");
//			conn.data("connectionCity[5]", "");
//			conn.data("originCity[6]", "");
//			conn.data("destinationCity[6]", "");
//			conn.data("departureDate[6]", "月/日/年");
//			conn.data("departureTime[6]", "AT");
//			conn.data("connectionCity[6]", "");
//			conn.data("paxCount", "2");
//			conn.data("meetingEventCode", "");
//			conn.data("searchByCabin", "TRUE");
//			conn.data("cabinFareClass", "economyBasic");
//			conn.data("deltaOnlySearch", "FALSE");
//			conn.data("deltaOnly", "deltaPartner");
//			conn.data("searchByFareClass", "E");
//			Document doc = conn.post();
//			System.out.println(doc.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
	
	private static void testHtmlUnit(){
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new NameValuePair("action", "findFlights"));
//		params.add(new NameValuePair("pageName", "advanceSearchPage"));
//		params.add(new NameValuePair("branchingOptions", ""));
//		params.add(new NameValuePair("tripType", "ONE_WAY"));
//		params.add(new NameValuePair("cacheKey",
//				"1a0b3b0e-5405-47db-8efa-c89472dff520"));
//		params.add(new NameValuePair(
//				"preferenceItinId",
//				"_PVG-LAX-09/27/2015-Return Date-oneway-2-undefined-undefined-undefined-undefined"));
//		params.add(new NameValuePair("priceSchedule", "price"));
//		params.add(new NameValuePair("awardTravel", "TRUE"));
//		params.add(new NameValuePair("originCity", "PVG"));
//		params.add(new NameValuePair("destinationCity", "LAX"));
//		params.add(new NameValuePair("originAirportRadius", "50"));
//		params.add(new NameValuePair("destinationAirportRadius", "50"));
//		params.add(new NameValuePair("departureDate", "09/27/2015"));
//		params.add(new NameValuePair("departureTime", "AT"));
//		params.add(new NameValuePair("returnDate", "月/日/年"));
//		params.add(new NameValuePair("returnTime", "AT"));
//		params.add(new NameValuePair("flexDays", "3"));
//		params.add(new NameValuePair("originCity[1]", "PVG"));
//		params.add(new NameValuePair("destinationCity[1]", "LAX"));
//		params.add(new NameValuePair("departureDate[1]", "09/27/2015"));
//		params.add(new NameValuePair("departureTime[1]", "AT"));
//		params.add(new NameValuePair("connectionCity[1]", ""));
//		params.add(new NameValuePair("originCity[2]", ""));
//		params.add(new NameValuePair("destinationCity[2]", ""));
//		params.add(new NameValuePair("departureDate[2]", "月/日/年"));
//		params.add(new NameValuePair("departureTime[2]", "AT"));
//		params.add(new NameValuePair("connectionCity[2]", ""));
//		params.add(new NameValuePair("originCity[3]", ""));
//		params.add(new NameValuePair("destinationCity[3]", ""));
//		params.add(new NameValuePair("departureDate[3]", "月/日/年"));
//		params.add(new NameValuePair("departureTime[3]", "AT"));
//		params.add(new NameValuePair("connectionCity[3]", ""));
//		params.add(new NameValuePair("originCity[4]", ""));
//		params.add(new NameValuePair("destinationCity[4]", ""));
//		params.add(new NameValuePair("departureDate[4]", "月/日/年"));
//		params.add(new NameValuePair("departureTime[4]", "AT"));
//		params.add(new NameValuePair("connectionCity[4]", ""));
//		params.add(new NameValuePair("originCity[5]", ""));
//		params.add(new NameValuePair("destinationCity[5]", ""));
//		params.add(new NameValuePair("departureDate[5]", "月/日/年"));
//		params.add(new NameValuePair("departureTime[5]", "AT"));
//		params.add(new NameValuePair("connectionCity[5]", ""));
//		params.add(new NameValuePair("originCity[6]", ""));
//		params.add(new NameValuePair("destinationCity[6]", ""));
//		params.add(new NameValuePair("departureDate[6]", "月/日/年"));
//		params.add(new NameValuePair("departureTime[6]", "AT"));
//		params.add(new NameValuePair("connectionCity[6]", ""));
//		params.add(new NameValuePair("paxCount", "2"));
//		params.add(new NameValuePair("meetingEventCode", ""));
//		params.add(new NameValuePair("searchByCabin", "TRUE"));
//		params.add(new NameValuePair("cabinFareClass", "economyBasic"));
//		params.add(new NameValuePair("deltaOnlySearch", "FALSE"));
//		params.add(new NameValuePair("deltaOnly", "deltaPartner"));
//		params.add(new NameValuePair("searchByFareClass", "E"));
//		
//		WebClient client = new WebClient(BrowserVersion.CHROME);
//		WebRequest req = new WebRequest(new java.net.URL(url));
//		req.setRequestParameters(params);
//		Page page = client.getPage(req);
//		String str = page.getWebResponse().getContentAsString();
//		System.out.println(str);
	}
}
