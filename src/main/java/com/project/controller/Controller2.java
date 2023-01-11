/* "Index.HTML"에서 받은 category 값으로 URL 주소를 구분하고, servicekey, page 값으로 완성한 Open-API의 데이터를 불러와
 * "Json"형식의 객체로 분활 시킨 후 "Specialized" 엔티티클래스 모델에 맞춰 매핑 하여 "Repository(SpecializedRepository)"로 저장
 * 그리고 "list"라는 액션 명을 가진 액션 재요청*/

package com.project.controller;

import java.io.BufferedInputStream; 
import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import com.project.domain.Specialized;
import com.project.model.Dto;
import com.project.repository.SpecializedRepository;

//import lombok.RequiredArgsConstructor;



@org.springframework.stereotype.Controller
public class Controller2 {
	@Autowired
	private SpecializedRepository Repository;
	JSONArray jArray = new JSONArray();
	@GetMapping("/api11")	
	public Object docList(@RequestParam("serviceKey") String serviceKey,@RequestParam("page") Integer page,@RequestParam("category") String category,
			@RequestParam("data_api") String data_api,@RequestParam("startdate１")LocalDateTime start_date, @RequestParam("enddate１")LocalDateTime end_date) {
		for(int i=0; i<3;i++) {//2780
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.minusYears(22)
				.minusMonths(11)
				.minusDays(18);
		LocalDateTime startdate1 = date;
		LocalDateTime enddate1 = date.plusDays(2);
		startdate1 = date.plusDays(2);
		enddate1 = date.plusDays(2);
		return "redirect:/api";
		}
		return "redirect:/api";
	}
	
	
	@GetMapping("/api11")
	public String load_save(@RequestParam("serviceKey") String serviceKey,@RequestParam("page") Integer page,@RequestParam("category") String category,
			@RequestParam("data_api") String data_api,@RequestParam("startdate１")LocalDateTime start_date, @RequestParam("enddate１")LocalDateTime end_date) {
		//@RequestParam을 이용해서 index.html에서 입력값을 받아온다.

		String result = ""; //result 초기화
		char quotes = '"'; // 매핑시 ""안에 "을 넣기 위해 선언
		String Requestcategory= category;
		String title ="";
		String subject = "";
		String description = "";
		String publisher = "";
		String contributors = "";
		String date ="";
		String language = "";
		String identifier = "";
		String format ="";
		String relation = "";
		String coverage= "";
		String right= "";
		int j=0;
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonObject3= new JSONObject();
		JSONArray jArray =new JSONArray();
		ArrayList<String> pitches = new ArrayList<>();
		String[] mappinglist = new String[20];
			try {
				String RequestserviceKey= serviceKey;// 받아온 Service key
				mappinglist[0] ="Title";
				mappinglist[1] ="SubTitle1";
				mappinglist[2] ="SubTitle2";
				mappinglist[3] ="SubTitle3";
				mappinglist[4] ="";
				mappinglist[5] ="DataContents";
				mappinglist[6] ="";
				mappinglist[7] ="MinisterCode";
				mappinglist[8] ="";
				mappinglist[9] ="";
				mappinglist[10] ="ModifyDate";
				mappinglist[11] ="ApproveDate";
				mappinglist[12] ="NewsItemId";
				mappinglist[13] ="OriginalUrl";
				mappinglist[14] ="";
				mappinglist[15] ="";
				mappinglist[16] ="FileName";
				mappinglist[17] ="FileUrl";
				mappinglist[18] ="";
				mappinglist[19] ="";
				
		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
//				if(page>=968||page<=0) {
//					model.addAttribute("error_name", "데이터 수집 ERROR~!!");
//					model.addAttribute("error_code", "EF_R_003");
//					model.addAttribute("error_reason", "정확한 페이지를 입력해주시기 바랍니다.");
//					model.addAttribute("error_page", "요청하신 Page :"+page);
//					model.addAttribute("error_pagenum", "행정규칙의 페이지 수 범위는 1 ~ 967쪽입니다.");
//					return "index";	 
//				}
				// URI를 URL객체로 저장
				StringBuffer result1= new StringBuffer();

				
				
				//원래 코드
//				URL url = new URL("http://apis.data.go.kr/1371000/pressReleaseService/pressReleaseList"
//						+ "?serviceKey="+RequestserviceKey
//						+ "&startDate="+start_date
//						+ "&endDate="+end_date);
				
				
				
				String startdate = start_date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
				String enddate = start_date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
				
				
				
				
				
				
				
				
				
				
				
				
				
				//기업요구사항 전체 데이터셋 저장용 코드
				URL url = new URL("http://apis.data.go.kr/1371000/pressReleaseService/pressReleaseList"
				+ "?serviceKey="+RequestserviceKey
				+ "&startDate="+startdate
				+ "&endDate="+enddate);
				
				
				
				
				
				
				
				
				
				//http://www.law.go.kr/DRF/lawSearch.do?OC=helena0809&target=admrul&query=%ED%95%99%EA%B5%90&type=XML
				//URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+serviceKey+"&target=law&type=XML"+ /*url 주소*/
				//"&page="+page);

				//https://www.law.go.kr/DRF/lawSearch.do?OC=helena0809&target=law&type=XML&page=1
				// 버퍼 데이터(응답 메세지)를 읽어서 result에 저장
				// result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음
				BufferedReader bf;
	            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	            urlConnection.connect();
	            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
	            bf = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
	            String returnLine;
	            while((returnLine = bf.readLine()) != null) {
	                result1.append(returnLine);
	            }
	          jsonObject = XML.toJSONObject(result1.toString());
	          jsonObject2 = jsonObject.getJSONObject("response");
	          jsonObject3 = jsonObject2.getJSONObject("body");
	          jArray= jsonObject3.getJSONArray("NewsItem");   
				for(int i=0; i<jArray.length(); i++){
					j++;
					JSONObject item = (JSONObject)jArray.get(i);
					
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("Title").toString()+quotes+"}");
					
						if(!item.get("SubTitle1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle1")+quotes+"}]"));
						}else if(!item.get("SubTitle2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle2")+quotes+"}]"));
						}else if(!item.get("SubTitle3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle3")+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("Title")+quotes+"}]"));
						}
					
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("DataContents")+quotes+"}")); 
					
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode").toString()+quotes+"}");
					
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); 
					
						date = ("{"+quotes+"modified"+quotes+":"+quotes+item.get("ModifyDate").toString()+","+"available:"+item.get("ApproveDate").toString()+quotes+"}");
					
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); 
					
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("NewsItemId").toString()+","+"view:"+item.get("OriginalUrl").toString()+quotes+"}");
					
						format = (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					
						relation = ("{"+quotes+"related"+quotes+":["+quotes+item.get("FileName").toString()+quotes+","+quotes+item.get("FileUrl").toString()+quotes+"]}");
					
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					
					
					Specialized infoObj= new Specialized(i+(long)1,
							(title.toString()),
							(subject.toString()),
							(description.toString()),
							(publisher.toString()),
							(contributors.toString()),
							(date.toString()),
							(language.toString()),
							(identifier.toString()),
							(format.toString()),
							(relation.toString()),
							(coverage.toString()),
							(right.toString()));

					Repository.save(infoObj);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				
				 
			}
		

	    	return "redirect:/api"; //"list"라는 이름을 가직 액션을 찾아 리턴 
	    }
		
}
