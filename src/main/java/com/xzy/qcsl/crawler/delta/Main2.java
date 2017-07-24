package com.xzy.qcsl.crawler.delta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author xiangzy
 * @date 2015-8-19
 * 
 */
public class Main2 {

	private static final String dir = new File("").getAbsolutePath();

	private static final String SEARCH_URL = "https://zh.delta.com/air-shopping/searchFlights.action";

	private static final String SEARCH_DATE = "shopping_departureDate_0";
	private static final String SEARCH_ORG_CITY = "shopping_originCity_0";
	private static final String SEARCH_DEST_CITY = "shopping_destinationCity_0";
	private static final String SEARCH_SUBMIT = "submitAdvanced";

	private static final String FIND_DATE = "departureDate";
	private static final String FIND_ORG_CITY = "departureCity0";
	private static final String FIND_DEST_CITY = "destinationCity0";
	private static final String FIND_SUBMIT = "modifySearchBtn";

	private static final SimpleDateFormat sdfX = new SimpleDateFormat(
			"yyyy/M/d");
	private static final SimpleDateFormat sdfG = new SimpleDateFormat(
			"yyyy-MM-dd");

	private ChromeDriver driver;

	private boolean isFind = false;

	// 当前第几行
	private int rowNumM = 0;
	private int rowNumS = 0;

	private Workbook tempWorkbookM;
	private WritableWorkbook workbookM;
	private WritableSheet sheetM;

	private Workbook tempWorkbookS;
	private WritableWorkbook workbookS;
	private WritableSheet sheetS;

	// 模板行
	private List<String> tempRowM = new ArrayList<String>();
	private List<String> tempRowS = new ArrayList<String>();

	private int days;// 天数
	private double cnt;// 普通舱常量 里程*cnt=价格
	private double gwcnt;// 公务舱常量
	private int passnum;// 乘客数
	private int startDay;// 从第几天开始
	private boolean onlyonce;// 直飞
	private int waitTime;
	private int pauseCount;
	private int pauseTime;

	private List<String> citys = new ArrayList<String>();

	private List<String> tmpRowList = new ArrayList<String>();

	private boolean isM;

	private String filePathM;
	private String filePathS;
	
	private int mCount = 0;
	private int sCount =0;
	
	private float dollarRate = 10;//美元汇率
	
	private boolean searchForAwards;

	private boolean initSelect() {
		// 选择单程
		try {
			WebElement ele = driver.findElement(By.id("one_way"));
			if (!ele.isSelected()) {
				ele.sendKeys(Keys.SPACE);
			}

			// 选择里程
			if (searchForAwards) {
				ele = driver.findElement(By.id("searchForAwards"));
				ele.click();
			} else {
				ele = driver.findElement(By.id("searchForRevenue"));
				ele.click();
			}

			// 乘客数
			ele = driver.findElement(By.id("passengers"));
			ele.sendKeys(this.passnum + "");

			// 仅限直达
			if (this.onlyonce) {
				ele = driver.findElement(By.id("nonstopFlightsOnly"));
				if (!ele.isSelected()) {
					ele.sendKeys(Keys.SPACE);
				}

			}
			return true;
		} catch (Exception e) {
			driver.get(SEARCH_URL);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
			}
			return false;
		}

	}

	private void start() throws IOException {
		try {
			
			

			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

			driver = new ChromeDriver();

			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

			boolean debug = false;
			if (!debug) {
				driver.get("http://120.24.77.20:8080/login/loginAuth.do");
				boolean loginOk = false;
				while (!loginOk) {
					try {

						driver.findElement(By.id("login_auth_ok"));
						if (driver.getCurrentUrl().contains("120.24.77.20")) {
							loginOk = true;
						}

					} catch (Exception e) {

					}

				}
			}
			
			getDollarRate();
			

			this.filePathM = dir + "/输出Excel/里程转机" + getTargetFileName();
			this.filePathS = dir + "/输出Excel/里程直飞" + getTargetFileName();
			this.searchForAwards = true;
			this.handle();

//			this.isFind = false;
//			this.rowNumM = 0;
//			this.rowNumS = 0;
//
//
//			this.filePathM = dir + "/输出Excel/现金转机" + getTargetFileName();
//			this.filePathS = dir + "/输出Excel/现金直飞" + getTargetFileName();
//			this.searchForAwards = false;
//			this.handle();

			if (driver != null) {
				try {
					driver.quit();
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {

			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
		}

	}

	/**
	 * 
	 */
	private void getDollarRate() {	
		try{
			this.driver.get("https://www.baidu.com/s?wd=%E7%BE%8E%E5%85%83%E5%85%91%E6%8D%A2");
			Document doc = Jsoup.parse(this.driver.getPageSource());
			/**
			 * <div class="op_exrate_result">
                   <div>1美元=6.3450人民币元</div>                
                   <div>1人民币元=0.1576美元</div>
               </div>
			 */
			Elements eles = doc.getElementsByClass("op_exrate_result");
			if(eles.size()>0){
				Element e = eles.get(0).child(0);
				String str = e.text().replace("1美元=", "").replace("人民币元", "");
				dollarRate = Float.parseFloat(str);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void handle() {

		try {
			driver.get(SEARCH_URL);

			boolean err = true;
			while (err) {
				err = !this.initSelect();
			}

			this.openExcel();

			long t1 = System.currentTimeMillis();
			int count = 0;
			for (String city : citys) {
				String cs[] = city.split(",");
				String orgCity = cs[0];
				String destCity = cs[1];
				int priceDiv = Integer.parseInt(cs[2]);
				this.setOrgCity(orgCity);
				this.setDestCity(destCity);

				Calendar c = Calendar.getInstance();
				if (this.startDay != 0) {
					c.add(Calendar.DATE, this.startDay);
				}
				for (int i = 0; i < this.days; i++) {
					count++;
					if (count % this.pauseCount == 0) {
						Thread.sleep(this.pauseTime * 1000);
					}
					this.searchOneDay(c, orgCity, destCity, priceDiv);
					c.add(Calendar.DATE, 1);
					if (!this.isFind) {
						boolean err1 = true;
						while (err1) {
							err1 = !this.initSelect();
                            try{
								this.setOrgCity(orgCity);
								this.setDestCity(destCity);
                            }catch(Exception e ){
                            	err1 = true;
                            }
						}
					}

				}

			}

			long t2 = System.currentTimeMillis();
			System.out.println("耗时" + (t2 - t1) / 1000);

		} catch (Exception e) {
			e.printStackTrace();
			File file = new File(dir + "/错误"
					+ new SimpleDateFormat("MMddHHmm").format(new Date())
					+ ".txt");
			PrintStream p;
			try {
				p = new PrintStream(file);
				e.printStackTrace(p);
				p.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {
			this.closeExcel();
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void openExcel() throws Exception {

		this.tempWorkbookM = Workbook.getWorkbook(Main2.class
				.getResourceAsStream("/转机模板.xls"));
		this.workbookM = Workbook.createWorkbook(new File(filePathM),
				tempWorkbookM);
		this.sheetM = workbookM.getSheet(0);
		Cell[] cells = this.sheetM.getRow(1);
		for (Cell cell : cells) {
			this.tempRowM.add(cell.getContents());
		}

		this.tempWorkbookS = Workbook.getWorkbook(Main2.class
				.getResourceAsStream("/直飞模板.xls"));
		this.workbookS = Workbook.createWorkbook(new File(this.filePathS),
				tempWorkbookS);
		this.sheetS = workbookS.getSheet(0);
		cells = this.sheetS.getRow(1);
		for (Cell cell : cells) {
			this.tempRowS.add(cell.getContents());
		}

	}

	private void closeExcel() {
		try {
			workbookM.write();
			workbookM.close();
			tempWorkbookM.close();
			if(this.rowNumM>0){
				this.convert2003To2007(filePathM);
			}
			new File(filePathM).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			workbookS.write();
			workbookS.close();
			tempWorkbookS.close();
			if(this.rowNumS>0){
				this.convert2003To2007(filePathS);
			}
			new File(filePathS).delete();

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void searchOneDay(Calendar c, String orgCity, String destCity,
			int priceDiv) {

		try {

			this.setDate(c);
			this.submit();

			try {
				Thread.sleep(this.waitTime * 1000);
			} catch (InterruptedException e) {
			}

			List<WebElement> list = driver
					.findElementsByXPath("//*[@class='fareDetails' or @id='shopping_originCity_0']");
			if (list.size() == 0) {
				this.isFind = false;
			} else {
				WebElement ele = list.get(0);
				this.isFind = ele.getTagName().equals("table");
			}

		} catch (Exception e) {
			this.isFind = false;
			e.printStackTrace();
		}

		if (!this.isFind) {
			System.err.println("没有找到");
		} else {
			this.parsePage(c, orgCity, destCity, priceDiv);
		}
	}

	private void parsePage(Calendar c, String orgCity, String destCity,
			int priceDiv) {
		String pageSource = driver.getPageSource();
		Document doc = Jsoup.parse(pageSource);
		Elements eles = doc.getElementsByClass("fareDetails");
		for (Element element : eles) {
			try {
				this.parseFlight(element, c, orgCity, destCity, priceDiv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void parseFlight(Element fEle, Calendar c, String orgCity,
			String destCity, int priceDiv) throws RowsExceededException,
			WriteException {

		System.out.println();
		// MU 5183, MU 583
		Elements fltNumList = fEle.getElementsByClass("fltNumDetails");

		this.tmpRowList.clear();
		if (fltNumList.size() == 1) {// 直飞
			this.isM = false;
			this.rowNumS++;
			// copy模板行
			for (int i = 0; i < this.tempRowS.size(); i++) {
				Label l = new Label(i, this.rowNumS, this.tempRowS.get(i));
				this.sheetS.addCell(l);
				this.tmpRowList.add(this.tempRowS.get(i));
			}

		} else if (fltNumList.size() == 2) {// 转机
			this.isM = true;

			Elements overEles = fEle.getElementsByClass("layOver");
			if (overEles.size() == 0) {

				return; // 经停多次
			}

			this.rowNumM++;
			// copy模板行
			for (int i = 0; i < this.tempRowM.size(); i++) {
				Label l = new Label(i, this.rowNumM, this.tempRowM.get(i));
				this.sheetM.addCell(l);
				this.tmpRowList.add(this.tempRowM.get(i));
			}
		} else {
			return; // 航班数大于2（转机2次及以上）
		}

		for (int i = 0; i < fltNumList.size(); i++) {
			Element ele = fltNumList.get(i);

			// String flight = ele.text().replace(",", "");
			String flight = "";
			List<TextNode> textList = ele.child(0).textNodes();
			if (textList.size() != 0) {
				flight = textList.get(0).text().replace(",", "");
			}

			String tmp[] = flight.split(" ");
			if (i == 0) {
				if (this.isM) {
					this.out("第一航空公司", tmp[0], "C");
					this.out(
							"第一航班号",
							flight.replace(" ", "").replace(((char) 160) + "",
									""), "J");
				} else {
					this.out("航空公司", tmp[0], "A");
					this.out("航班号", flight.replace(" ", ""), "U");
				}
			} else if (i == 1) {
				this.out("第二程适用航班类型", "适用", "K");
				this.out("第二航空公司", tmp[0], "D");
				this.out("第二航班号", flight.replace(" ", ""), "L");
			}
		}

		// 出发机场和到达机场从网页读取
		orgCity = fEle.getElementsByClass("originCity").get(0).text().trim();
		destCity = fEle.getElementsByClass("destinationCity").get(0).text()
				.trim();

		String tdate = sdfX.format(c.getTime());
		if (this.isM) {

			this.out("起飞机场", orgCity, "F");
			this.out("到达机场", destCity, "H");
			this.out("旅行开始日期", tdate, "M");
			this.out("旅行结束日期", tdate, "N");
			this.out("销售开始日期", "2015-09-01", "P");
			this.out("销售结束日期", sdfG.format(c.getTime()), "Q");

		} else {
			this.out("起飞机场", orgCity, "B");
			this.out("到达机场", destCity, "C");
			this.out("旅行开始日期", tdate, "D");
			this.out("旅行结束日期", tdate, "E");
			this.out("销售开始日期", "2015-09-01", "F");
			this.out("销售结束日期", sdfX.format(c.getTime()), "G");
		}

		if (this.isM) {
			Elements overEles = fEle.getElementsByClass("layOver");

			String layover = overEles.get(0).children().get(0).text().trim();
			this.out("中转机场", layover, "G");

		}

		// 普通舱、商务舱价格
		Elements priceEles = fEle.getElementsByClass("priceHolder");
		for (int i = 0; i < priceEles.size(); i++) {

			Element priceEle = priceEles.get(i);
			Element td = priceEle.parent();
			int index = td.siblingIndex();

			Elements cntBigs = priceEle.getElementsByClass("tblCntBigTxt");

			int passnum = 4;
			Elements setsLefts = td.getElementsByClass("seatsLeft");
			if (setsLefts.size() > 0) {
				String numStr = setsLefts.get(0).text()
						.replace("left at this price", "").replace("保留该价格", "").trim();
				passnum = Integer.parseInt(numStr);
			}

			if (cntBigs.size() > 0) {
				// 里程点数 或金额
				String bigStr = cntBigs.get(0).text().replaceAll(",", "");
				int cnt = Math.round(Float.parseFloat(bigStr.replace("¥", "").replace("$", "").trim()));
				if(bigStr.contains("$")){
					cnt *=this.dollarRate; //美元
				}
				Elements cntSmalls = priceEle.getElementsByClass("tblCntSmallTxt");
				int div = 0;
				if (cntSmalls.size() > 0) {
					// <span class="tblCntSmallTxt">+ <sup>¥</sup>
					// 252<sup></sup></span>
					String tempStr = cntSmalls.get(0).text();
					if ((tempStr.contains("¥") ||tempStr.contains("$")) && tempStr.contains("+")) {// 只处理人民币
						String smallStr = tempStr.replace("¥", "").replace("$", "").replace("+", "")
								.replace(",", "").trim();
						if(tempStr.contains("$")){
							div = Math.round(Float.parseFloat(smallStr)*dollarRate) + priceDiv;
						}else{
							div =  Math.round(Float.parseFloat(smallStr)) + priceDiv;
						}
						
						// System.out.println(tempStr);

					}
				}

				if (index == 2) {// 经济舱
					long num = cnt;// 现金
					if (this.searchForAwards) {// 里程
						num = round(Math.round(cnt * this.cnt) + div);
					}
					if (this.isM) {
						this.out("销售票面价", num + "", "V");
						this.out("第一程适用舱位", "Y", "S");
						this.out("第二程适用舱位", "Y", "T");
						this.out("乘客数", passnum + "", "AJ");
					} else {
						this.out("销售票面价", num + "", "L");
						this.out("适用舱位", "Y", "I");
						this.out("可售座位数", passnum + "", "AF");
					}
				} else if (index >= 3) {// 商务舱

					if (priceEles.size() == 2) {// 复制一行
						System.out.println();
						WritableSheet sheet = this.isM ? this.sheetM
								: this.sheetS;
						int rowNum = this.isM ? ++this.rowNumM : ++this.rowNumS;

						for (int j = 0; j < this.tmpRowList.size(); j++) {
							sheet.addCell(new Label(j, rowNum, this.tmpRowList
									.get(j)));
						}

					}
					long num = cnt;// 现金
					if (this.searchForAwards) {// 里程
						num = round(Math.round(cnt * this.gwcnt) + div);
					}
					if (this.isM) {
						this.out("销售票面价", num + "", "V");
						this.out("第一程适用舱位", "J", "S");
						this.out("第二程适用舱位", "J", "T");
						this.out("乘客数", passnum + "", "AJ");
					} else {
						this.out("销售票面价", num + "", "L");
						this.out("适用舱位", "J", "I");
						this.out("可售座位数", passnum + "", "AF");
					}
				}

				String code = orgCity + destCity + cnt;
				if (this.isM) {
					this.out("外部政策id", code, "B");
				} else {
					this.out("外部政策id", code, "S");
				}
			}
		}

	}

	private void setOrgCity(String s) {
		WebElement ele = null;
		if (isFind) {
			ele = driver.findElement(By.id(FIND_ORG_CITY));
		} else {
			ele = driver.findElement(By.id(SEARCH_ORG_CITY));
		}
		ele.clear();
		ele.sendKeys(s);
	}

	private void setDestCity(String s) {
		WebElement ele = null;
		if (isFind) {
			ele = driver.findElement(By.id(FIND_DEST_CITY));
		} else {
			ele = driver.findElement(By.id(SEARCH_DEST_CITY));
		}
		ele.clear();
		ele.sendKeys(s);
	}

	private void setDate(Calendar c) {
		if (isFind) {
			WebElement ele = driver.findElement(By.id(FIND_DATE));
			String day = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
			ele.clear();
			ele.sendKeys(day);

		} else {
			WebElement ele = driver.findElement(By.id(SEARCH_DATE));
			String day = new SimpleDateFormat("MMddyyyy").format(c.getTime());
			ele.clear();
			ele.sendKeys(day);
		}

	}

	private void submit() {
		if (isFind) {
			driver.findElement(By.className(FIND_SUBMIT)).submit();
		} else {
			driver.findElement(By.id(SEARCH_SUBMIT)).submit();
		}
	}

	private void out(String name, String value, String index) {

		int rowNum = this.rowNumM;
		WritableSheet sheet = this.sheetM;
		if (!this.isM) {
			rowNum = this.rowNumS;
			sheet = this.sheetS;
		}

		System.out.print(" " + name + " " + index + ":" + value);
		int col = 0;
		if (index.length() == 1) {
			col = (int) (index.charAt(0)) - 65;
		} else {
			col = 26 + (int) (index.charAt(1)) - 65;
		}
		Label l = new Label(col, rowNum, value);
		try {
			sheet.addCell(l);
			this.tmpRowList.set(col, value);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void loadConfig() throws Exception {
		System.out.println("开始读取配置文件...");
		Properties prop = new Properties();
		prop.load(Main2.class.getResourceAsStream("/配置.properties"));
		this.startDay = Integer.parseInt(prop.getProperty("startday", "0"));
		System.out.println("推后几天开始查询:" + this.startDay);
		this.days = Integer.parseInt(prop.getProperty("days", "2"));
		System.out.println("查询天数:" + days);
		this.cnt = Double.parseDouble(prop.getProperty("cnt", "1"));
		System.out.println("普通舱价格常量:" + cnt);
		this.gwcnt = Double.parseDouble(prop.getProperty("gwcnt", "1"));
		System.out.println("公务舱价格常量:" + gwcnt);
		this.passnum = Integer.parseInt(prop.getProperty("passnum", "2"));
		System.out.println("乘客数:" + passnum);
		this.onlyonce = "1".equals(prop.getProperty("onlyonce", "0"));
		System.out.println("只查询直飞：" + onlyonce);

		this.waitTime = Integer.parseInt(prop.getProperty("waitTime", "5"));
		System.out.println("每次查询等待多少秒：" + waitTime);
		this.pauseCount = Integer
				.parseInt(prop.getProperty("pauseCount", "30"));
		System.out.println("多少次暂停：" + pauseCount);
		this.pauseTime = Integer.parseInt(prop.getProperty("pauseTime", "60"));
		System.out.println("暂停多少秒：" + pauseTime);

		System.out.println("航线:");
		Workbook b = Workbook.getWorkbook(Main2.class
				.getResourceAsStream("/航线.xls"));
		Sheet s = b.getSheet(0);
		for (int i = 1; i < s.getRows(); i++) {
			Cell[] cells = s.getRow(i);
			if (cells.length < 3) {
				continue;
			}
			String c0 = cells[0].getContents();// 出发
			String c1 = cells[1].getContents();// 到达
			String c2 = cells[2].getContents();// 价格差
			if (c0 != null && c1 != null && !c0.equals("") && !c1.equals("")) {
				this.citys.add(c0 + "," + c1 + "," + c2);
				System.out.println(c0 + "," + c1 + "," + Integer.parseInt(c2));
			}

		}
		System.out.println("读取配置完成");
	}

	public void convert2003To2007(String path) throws Exception {
		FileInputStream in = new FileInputStream(path);
		FileOutputStream out = new FileOutputStream(path.replace(".xls",
				".xlsx"));
		HSSFWorkbook workbookIn = new HSSFWorkbook(in);
		XSSFWorkbook wokbookOut = new XSSFWorkbook();

		HSSFSheet sheetIn = workbookIn.getSheetAt(0);
		XSSFSheet sheetOut = wokbookOut.createSheet();

		for (int i = 0; i <= sheetIn.getLastRowNum(); i++) {

			HSSFRow rowIn = sheetIn.getRow(i);
			XSSFRow rowOut = sheetOut.createRow(i);
			for (int j = 0; j < rowIn.getLastCellNum(); j++) {
				try{
					HSSFCell cellIn = rowIn.getCell(j);
					XSSFCell cellOut = rowOut.createCell(j);
					cellOut.setCellValue(cellIn.getStringCellValue());
					if (i == 0) {
						sheetOut.setColumnWidth(j, sheetIn.getColumnWidth(j));
					}
				}catch(Exception e){
					
				}
			}
		}

		wokbookOut.write(out);

		wokbookOut.close();
		workbookIn.close();
		out.close();
		in.close();

		

	}

	public static void main(String[] args) throws Exception {
		System.out.println("***********************************");
		System.out.println("**         11-13 15:39	          **");
		System.out.println("************************************");
		Main2 web = new Main2();
		web.loadConfig();

		web.start();

	}

	public static long round(long n) {
		long num = n % 10;
		long m;
		if (num <= 4) {
			m = n - num;
		} else {
			m = n + (10 - num);
		}
		return m;
	}

	public static String getTargetFileName() {
		return "结果_" + new SimpleDateFormat("MMddHHmm").format(new Date())
				+ ".xls";

	}

}
