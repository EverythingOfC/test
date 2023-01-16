/* "Index.HTML"에서 받은 category 값으로 URL 주소를 구분하고, servicekey, page 값으로 완성한 Open-API의 데이터를 불러와
 * "Json"형식의 객체로 분활 시킨 후 "Specialized" 엔티티클래스 모델에 맞춰 매핑 하여 "Repository(SpecializedRepository)"로 저장
 * 그리고 "list"라는 액션 명을 가진 액션 재요청*/
package com.project.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.domain.Specialized;
import com.project.repository.SpecializedRepository;

@org.springframework.stereotype.Controller
public class Controller {
	@Autowired
	private SpecializedRepository Repository;
	JSONArray jArray = new JSONArray();

	@GetMapping("/api")
	public String load_save(@RequestParam("serviceKey") String serviceKey,@RequestParam(value = "page",defaultValue = "1") Integer page,@RequestParam("category") String category,
			@RequestParam(value = "data_api",defaultValue = "data") String data_api,@RequestParam("startdate")String start_date, @RequestParam("enddate")String end_date,Model model) {
		// @RequestParam을 통해 index.html의 form태그 name값을 받아온다.

		model.addAttribute("data", "");
		char quotes = '"'; // 매핑시 ""안에 "을 넣기 위해 선언
		String Requestcategory= category;
		String title = null;
		String subject = null;
		String description = null;
		String publisher = null;
		String contributors = null;
		String date = null;
		String language = null;
		String identifier = null;
		String format =null;
		String relation = null;
		String coverage= null;
		String right= null;
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonObject3= new JSONObject();
		JSONArray jArray = new JSONArray();
		String RequestserviceKey= serviceKey; // 받아온 Service key
		List<String> pitches = null;	// 칼럼명 리스트
		String[] menu = {"Title","Subject","Description","Publisher","Contributors","Date","Language","Identifier","Format","Relation","Coverage","right"};

		int Requestpage = page;
		int j = 0;

		if(Requestcategory.equals("현행법령")){	//if문을 통해서 데이터에 따라 실행되는 Mapping코드를 분리
			String[] mappinglist = {"법령명한글","법령약칭명","","","","","",	// 7개
					"소관부처명","","","공포일자","시행일자","법령일련번호","법령상세링크"	// 7개
					,"","","제개정구분명","","",""};	// 6개
			try {
		        pitches = new ArrayList<>(Arrays.asList(mappinglist));	// 칼럼명을 한글로 담음.
				if(page>=263||page<=0) {
					model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
					model.addAttribute("error_code", "CODE : EF_R_003");
					model.addAttribute("error_reason", "사유 : 정확한 페이지를 입력해주시기 바랍니다.");
					model.addAttribute("error_page", "요청하신 Page :"+page);
					model.addAttribute("error_pagenum", "현행법령의 페이지 수 범위는 1 ~ 262쪽입니다.");
					return "index";
				}
				URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+RequestserviceKey+"&target=law&type=XML"+ 	// url 주소
						"&page="+Requestpage);	// URI를 객체로 저장

				BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));  // 버퍼를 읽어 bf에 저장
				String result = bf.readLine(); // result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음
				 						// readLine() : 한 줄을 읽어 String으로 반환.
				jsonObject = XML.toJSONObject(result.toString()); //result 값을 XMLtoJson를 사용하여 json형식으로 변환
				jsonObject2 = jsonObject.getJSONObject("LawSearch");	//key: LawSearch의 Value 값을 JSONObject 객체 jsonObject2로 받습니다.
			    jArray = jsonObject2.getJSONArray("law");	 //key: law의 Value 값을 JSONArray 객체 jArray로 받습니다.
				for(int i=0; i<jArray.length(); i++,j++){ //for문을 통해 JsonArray의 수 만큼 리스트 안의 객체를 분리
					JSONObject item = (JSONObject)jArray.get(i);  // 원하는 항목명을 Parsing 해서 형식에 맞춰 변수에 저장
					int count = 0;	// 오류 없이 지날 때마다 count증가
					try {
						title = "{"+quotes+"org"+quotes+":"+quotes+item.get("법령명한글").toString()+quotes+"}"; count++;
						String temp = !item.get("법령약칭명").equals("") ? "법령약칭명" : "법령명한글";	// 있으면 약칭명으로, 없으면 한글로
						subject = "{"+quotes+"org"+quotes+":"+quotes+item.get(temp).toString()+quotes+"}"; count++;
						description = "{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("법령명한글")+quotes+"}}"; count++;
						publisher = "{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명").toString()+quotes+"}"; count++;
						contributors = "[{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]"; count++;
						date = "{"+quotes+"issued"+quotes+":"+quotes+item.get("시행일자").toString()+quotes+","+quotes+"created"+quotes+":"+quotes+item.get("공포일자").toString()+quotes+"}"; count++;
						language = "{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"; count++;
						identifier = "{"+quotes+"site"+quotes+":"+quotes+item.get("법령일련번호").toString()+quotes+","+quotes+"url"+quotes+":"+quotes+item.get("법령상세링크").toString()+quotes+"}"; count++;
						format = "{"+quotes+"org"+quotes+":"+quotes+quotes+"}"; count++;
						relation = "{"+quotes+"isPartOF"+quotes+":"+quotes+item.get("제개정구분명").toString()+quotes+"}"; count++;
						coverage= "{"+quotes+"org"+quotes+":"+quotes+quotes+"}"; count++;
						right= "{"+quotes+"org"+quotes+":"+quotes+quotes+"}";
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE :  EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: " + menu[count]);
						return "index";}

					Specialized infoObj= new Specialized(i+(long)1,	// 저장한 변수를 Specialized 객체에 저장
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
		}else if(Requestcategory.equals("행정규칙")) {
			try {
				String[] mappinglist = {"행정규칙명","","","","","",""	// 7개
				,"소관부처명","","","시행일자","생성일자","행정규칙ID","행정규칙상세링크"	// 7개
				,"","","제개정구분명","","",""};	// 6개

		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				if(page>=968||page<=0) {
					model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
					model.addAttribute("error_code", "CODE : EF_R_003");
					model.addAttribute("error_reason", "사유 : 정확한 페이지를 입력해주시기 바랍니다.");
					model.addAttribute("error_page", "요청하신 Page : "+page);
					model.addAttribute("error_pagenum", "행정규칙의 페이지 수 범위는 1 ~ 967쪽입니다.");
					return "index";
				}
				URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+RequestserviceKey+"&target=admrul&query=학교&type=XML"+ /*url 주소*/
						"&page="+Requestpage);

				// result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음
				BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));	// 버퍼 데이터(응답 메세지)를 읽어서 result에 저장
				String result = bf.readLine();
				jsonObject = XML.toJSONObject(result.toString());
				jsonObject2 = jsonObject.getJSONObject("AdmRulSearch");
				jArray = jsonObject2.getJSONArray("admrul");
				for(int i=0; i<jArray.length(); i++,j++){
					JSONObject item = (JSONObject)jArray.get(i);
					int count = 0;
					try {
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("행정규칙명").toString()+quotes+"}"); count++;
						subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("행정규칙명").toString()+quotes+"}]")); count++;
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("행정규칙명")+quotes+"}}")); count++;
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명").toString()+quotes+"}"); count++;
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); count++;
						date = ("{"+quotes+"issued"+quotes+":"+quotes+item.get("시행일자").toString()+quotes+","+quotes+"created"+quotes+":"+quotes+item.get("생성일자").toString()+quotes+"}"); count++;
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); count++;
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("행정규칙ID").toString()+","+"url:"+item.get("행정규칙상세링크").toString()+quotes+"}"); count++;
						format = (("{\"org\":\""+"\"}")); count++;
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+item.get("제개정구분명").toString()+quotes+"}"); count++;
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}")); count++;
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: "+menu[count]);	// 인덱스에 해당하는 항목 조회
						return "index";
					}
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
				model.addAttribute("error_name", "데이터 수집 ERROR~!!");
				model.addAttribute("error_code", "CODE : EF_R_003");
				model.addAttribute("error_reason", "사유: 정확한 인증키를 입력해주시기 바랍니다.");
				model.addAttribute("error_key", "요청하신 Key: "+serviceKey);
				model.addAttribute("error_page", "요청하신 Page: "+page);
				return "index";
			}
		}else if(Requestcategory.equals("전문자료")) { // @RequestParam로 받아온 Requestcategory의 값으로 Mapping 코드 구분.
			try {

				String[] mappinglist = {"전문자료 메인 제목","주제1","주제2","주제3","전문자료 문서 타입","전문자료 문서 새요약"  // 6개
				,"전문자료 문서 목차","전문자료 부서 코드","전문자료 문서 저자","전문자료 등록자","전문자료 등록 일자"	// 5개
				,"전문자료 승인 일자","전문자료 문서 아이디","","","","","","",""}; 	// 9개
		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				if(page>=2401||page<=0) {
					model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
					model.addAttribute("error_code", "CODE : EF_R_003");
					model.addAttribute("error_reason", "정확한 페이지를 입력해주시기 바랍니다.");
					model.addAttribute("error_page", "요청하신 Page :"+page);
					model.addAttribute("error_pagenum", "전문자료의 페이지 수 범위는 1 ~ 2400쪽입니다.");
					return "index";
				}
				URL url = new URL("https://api.odcloud.kr/api/15092231/v1/uddi:f485c10f-f5d2-4a00-a993-b85d929565ec"+  // url 주소
						"?page="+Requestpage+ /*page 수*/ "&perPage=20"+ /*출력할 데이터 수*/ "&serviceKey="+RequestserviceKey /*인증키*/);

				BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")); //Buffer를 사용하여 url의 데이터를 출력
				String result = bf.readLine(); //출력한 데이터 한 줄 씩 읽기
				JSONObject obj = new JSONObject(result.toString()); //result 값을 Json 형태의 Object(객체)로 분리
				jArray = (JSONArray) obj.get("data"); // 분리된 Object에서 "data"라는 객체를 Json형태의 리스트로 분리

				for(int i=0; i<jArray.length(); i++,j++){ //
					JSONObject item = (JSONObject)jArray.get(i); //for문을 통해 JsonArray의 수 만큼 리스트 안의 객체를 분리
					int count = 0;
					try {
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 메인 제목").toString()+quotes+"}");	count++;
						if(!item.get("주제1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("주제1").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}else if(!item.get("주제2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("주제2").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}else if(!item.get("주제3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("주제3").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 메인 제목").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						} count++;

						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 문서 새요약").toString()+quotes+
								"}"+","+quotes+"toc"+quotes+":"+"{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 문서 목차")+quotes+"}}")); count++;
						publisher = (("{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 부서 코드")+quotes+"}"));	count++;
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 등록자")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}"+","+
								+quotes+"affiliation"+quotes+":"+quotes+"[{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 문서 저자")+quotes+"}]]"));	count++;
						date = (("{"+quotes+"registration"+quotes+":"+quotes+item.get("전문자료 등록 일자").toString().substring(0, 16)+quotes+","+quotes+"approval"+quotes+":"+quotes+item.get("전문자료 승인 일자").toString().substring(0, 16)+quotes+"}")); count++;
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}");	count++;
						identifier = (("{"+quotes+"id"+quotes+":"+quotes+item.get("전문자료 문서 아이디").toString().substring(0, 4)+quotes+"}"));	count++;
						format = (("{"+quotes+"media"+quotes+":"+quotes+"text"+quotes+"}"));	count++;
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+quotes+"}");	count++;
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));	count++;
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));	count++;
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: " + menu[count]);
						return "index";
					}
					Specialized infoObj= new Specialized(i+(long)1, //분리한 객체에서 "전문자료 메인 제목" 등에 해당되는 객체를 우리가 원하는 값으로 매핑.
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
					Repository.save(infoObj); //매핑한 값을 Repository에 저장
				}
			}catch(Exception e) {
				model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
				model.addAttribute("error_code", "CODE : EF_R_003");
				model.addAttribute("error_reason", "사유 : 정확한 인증키를 입력해주시기 바랍니다.");
				model.addAttribute("error_key", "요청하신 Key: "+serviceKey);
				model.addAttribute("error_page", "요청하신 Page :"+page);
				return "index";

			}
		} else if(Requestcategory.equals("정책뉴스")) {
			String[] mappinglist = {"Title","SubTitle1","SubTitle2","SubTitle3","","DataContents"  // 6개
					,"","MinisterCode","","","ModifyDate","ApproveDate"	 // 6개
					,"NewsItemId","OriginalUrl","OriginalimgUrl","","","","",""}; 	// 8개
			try {

		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				StringBuffer result1= new StringBuffer();
				URL url = new URL("http://apis.data.go.kr/1371000/policyNewsService/policyNewsList?"
						+ "serviceKey="+RequestserviceKey + "&startDate="+start_date + "&endDate="+end_date);	// URI를 객체로 저장
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.connect();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
				BufferedReader bf = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
				String returnLine = null;
				while((returnLine = bf.readLine()) != null) {
					result1.append(returnLine);
				}

				// 버퍼 데이터(응답 메세지)를 읽어서 result에 저장
				jsonObject = XML.toJSONObject(result1.toString());  //result 값을 XMLtoJson를 사용하여 json형식으로 바꾼뒤 json형태의 Object(객체)로 분리
				jsonObject2 = jsonObject.getJSONObject("response");	 //분리된 객체를 다시 json형태의 Object(객체)로 분리
				jsonObject3 = jsonObject2.getJSONObject("body");
				jArray= jsonObject3.getJSONArray("NewsItem");

				for(int i=0; i<jArray.length(); i++,j++){
					int count = 0;
					JSONObject item = (JSONObject)jArray.get(i);
					try {
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("Title").toString()+quotes+"}");	count++;
						if(!item.get("SubTitle1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle1")+quotes+"}]"));
						}else if(!item.get("SubTitle2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle2")+quotes+"}]"));
						}else if(!item.get("SubTitle3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle3")+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("Title")+quotes+"}]"));
						}	count++;
						description = (("{"+quotes+"summary"+quotes+":"+quotes+"org"+quotes+":"+quotes+item.get("DataContents")+quotes+"}")); count++;
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode").toString()+quotes+"}"); count++;
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); count++;
						date = ("{"+quotes+"modified"+quotes+":"+quotes+item.get("ModifyDate").toString()+","+"available:"+item.get("ApproveDate").toString()+quotes+"}"); count++;
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}");	count++;
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("NewsItemId").toString()+quotes+","+quotes+"view"+quotes+":"+quotes+item.get("OriginalUrl").toString()+quotes+","+quotes+"thumbs"+quotes+":"+quotes+item.get("OriginalimgUrl").toString()+quotes+"}"); count++;
						format = (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}")); count++;
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+quotes+"}");	count++;
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));	count++;
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: " + menu[count]);
						return "index";
					}
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
				model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
				model.addAttribute("error_code", "CODE : EF_R_003");
				model.addAttribute("error_reason", "사유: 정확한 인증키를 입력해주시기 바랍니다.");
				model.addAttribute("error_key", "요청하신 Key: "+serviceKey);
				model.addAttribute("error_page", "요청하신 Page: "+page);
				return "index";
			}
		}else if(Requestcategory.equals("보도자료")) {
			String[] mappinglist = {"Title","SubTitle1","SubTitle2","SubTitle3","","DataContents"  // 6개
					,"","MinisterCode","","","ModifyDate","ApproveDate"	 // 6개
					,"NewsItemId","OriginalUrl","","","FileName","FileUrl","",""}; 	// 8개
			try {

		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				StringBuffer result1= new StringBuffer();

				URL url = new URL("http://apis.data.go.kr/1371000/pressReleaseService/pressReleaseList"
				+ "?serviceKey="+RequestserviceKey
				+ "&startDate="+start_date
				+ "&endDate="+end_date);

				// result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음

	            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	            urlConnection.connect();
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
	            String returnLine = null;

	            while((returnLine = bf.readLine()) != null) {
	                result1.append(returnLine);
	            }
				  jsonObject = XML.toJSONObject(result1.toString());
				  jsonObject2 = jsonObject.getJSONObject("response");
				  jsonObject3 = jsonObject2.getJSONObject("body");
				  jArray= jsonObject3.getJSONArray("NewsItem");
				for(int i=0; i<jArray.length(); i++,j++){
					JSONObject item = (JSONObject)jArray.get(i);
					int count=0;
					try {
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("Title").toString()+quotes+"}"); count++;
						if(!item.get("SubTitle1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle1")+quotes+"}]"));
						}else if(!item.get("SubTitle2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle2")+quotes+"}]"));
						}else if(!item.get("SubTitle3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle3")+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("Title")+quotes+"}]"));
						}	count++;
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("DataContents")+quotes+"}")); count++;
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode").toString()+quotes+"}"); count++;
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); count++;
						date = ("{"+quotes+"modified"+quotes+":"+quotes+item.get("ModifyDate").toString()+","+"available:"+item.get("ApproveDate").toString()+quotes+"}"); count++;
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); count++;
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("NewsItemId").toString()+","+"view:"+item.get("OriginalUrl").toString()+quotes+"}"); count++;
						format = (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}")); count++;
						relation = ("{"+quotes+"related"+quotes+":["+quotes+item.get("FileName").toString()+quotes+","+quotes+item.get("FileUrl").toString()+quotes+"]}"); count++;
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}")); count++;
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: "+menu[count]);
						return "index";
					}
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
			}catch(Exception e) {
				model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
				model.addAttribute("error_code", "CODE : EF_R_003");
				model.addAttribute("error_reason", "사유: 정확한 인증키를 입력해주시기 바랍니다.");
				model.addAttribute("error_key", "요청하신 Key: "+serviceKey);
				model.addAttribute("error_page", "요청하신 Page: "+page);
				e.printStackTrace();
				return "index";
			}
		}else{
			model.addAttribute("error_name", "ERROR : 매핑 ERROR~!!");
			model.addAttribute("error_code", "CODE : EF_R_002");
			model.addAttribute("error_reason", "현행법령, 행정규칙, 전문자료, 정책뉴스, 보도자료 이외의 데이터를 입력하셨습니다.");
			return "index";
		}

		if(data_api.equals("data")) {
			// 매핑된 데이터f
			model.addAttribute("title", title);
			model.addAttribute("subject", subject);
			model.addAttribute("description", description);
			model.addAttribute("publisher", publisher);
			model.addAttribute("contributors", contributors);
			model.addAttribute("date", date);
			model.addAttribute("language", language);
			model.addAttribute("identifier", identifier);
			model.addAttribute("format", format);
			model.addAttribute("relation", relation);
			model.addAttribute("coverage", coverage);
			model.addAttribute("right", right);

			// 매핑된 데이터 제목
			model.addAttribute("title2", "title : ");
			model.addAttribute("subject2", "subject : ");
			model.addAttribute("description2", "description : ");
			model.addAttribute("publisher2", "publisher : ");
			model.addAttribute("contributors2", "contributors : ");
			model.addAttribute("date2", "date : ");
			model.addAttribute("language2", "language : ");
			model.addAttribute("identifier2", "identifier :");
			model.addAttribute("format2", "format : ");
			model.addAttribute("relation2", "relation : ");
			model.addAttribute("coverage2", "coverage : ");
			model.addAttribute("right2", "right : " );

			Map<String,String> map = new HashMap<>();
			Map<String,String> map2 = new HashMap<>();

			JSONObject rawdata=(JSONObject) jArray.get(j-1);

			map.put("rawdata_title",pitches.get(0));
			map.put("rawdata_subject1",pitches.get(1));
			map.put("rawdata_subject2",pitches.get(2));
			map.put("rawdata_subject3",pitches.get(3));
			map.put("rawdata_subject4",pitches.get(4));
			map.put("rawdata_description1",pitches.get(5));
			map.put("rawdata_description2",pitches.get(6));
			map.put("rawdata_publisher",pitches.get(7));
			map.put("rawdata_contributors1",pitches.get(8));
			map.put("rawdata_contributors2",pitches.get(9));
			map.put("rawdata_date1",pitches.get(10));
			map.put("rawdata_date2",pitches.get(11));
			map.put("rawdata_identifier1",pitches.get(12));
			map.put("rawdata_identifier2",pitches.get(13));
			map.put("rawdata_identifier3",pitches.get(14));
			map.put("rawdata_format",pitches.get(15));
			map.put("rawdata_relation1",pitches.get(16));
			map.put("rawdata_relation2",pitches.get(17));
			map.put("rawdata_coverage",pitches.get(18));
			map.put("rawdata_right",pitches.get(19));

			map2.put("rawdata2_title",pitches.get(0));
			map2.put("rawdata2_subject1",pitches.get(1));
			map2.put("rawdata2_subject2",pitches.get(2));
			map2.put("rawdata2_subject3",pitches.get(3));
			map2.put("rawdata2_subject4",pitches.get(4));
			map2.put("rawdata2_description1",pitches.get(5));
			map2.put("rawdata2_description2",pitches.get(6));
			map2.put("rawdata2_publisher",pitches.get(7));
			map2.put("rawdata2_contributors1",pitches.get(8));
			map2.put("rawdata2_contributors2",pitches.get(9));
			map2.put("rawdata2_date1",pitches.get(10));
			map2.put("rawdata2_date2",pitches.get(11));
			map2.put("rawdata2_identifier1",pitches.get(12));
			map2.put("rawdata2_identifier2",pitches.get(13));
			map2.put("rawdata2_identifier3",pitches.get(14));
			map2.put("rawdata2_format",pitches.get(15));
			map2.put("rawdata2_relation1",pitches.get(16));
			map2.put("rawdata2_relation2",pitches.get(17));
			map2.put("rawdata2_coverage",pitches.get(18));
			map2.put("rawdata2_right",pitches.get(19));

			int i = 0;
			for (String key : map.keySet()){	 // map의 key값을 모두 얻어와서 key값에 해당 값들을 순차적으로 저장
				String value= String.valueOf(rawdata.get(pitches.get(i++)));	// rawdata 칼럼의 값들을 모두 저장
				if(!pitches.get(i).equals(""))
					map.put(key,value);	// rawdata의 칼럼 데이터들을 순차적으로 저장
				model.addAttribute(key,value);
			}

			i = 0;
			for(String key2 : map2.keySet()){	// map2의 key값을 모두 얻어와서 key값에 해당 값들을 순차적으로 저장
				model.addAttribute(key2,pitches.get(i++));
			}

			Map<String,String> colon = new HashMap<>();
			for(i=0;i<20;i++){	// 20번 반복해서 들어감
				colon.put("rawdata_Colon"+i,"");
				if(!pitches.get(i).equals("")){
					colon.put("rawdata_Colon"+i,":");
				}
				model.addAttribute("rawdata_Colon"+i,colon.get(i));	// key: rawdata_Colon+ i,   value: "" or ":"
			}

			model.addAttribute("mapping_1", "[ 매핑전 데이터 예시 ]");
			model.addAttribute("mapping_2", "[ 매핑후 데이터 예시 ]");

			return "index"; //"list"라는 이름을 가직 액션을 찾아 리턴
	    }else {
	    	return "redirect:/list"; //"list"라는 이름을 가진 액션을 찾아 리턴
		}
	}
}
