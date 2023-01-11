/* "Index.HTML"에서 받은 category 값으로 URL 주소를 구분하고, servicekey, page 값으로 완성한 Open-API의 데이터를 불러와
 * "Json"형식의 객체로 분활 시킨 후 "Specialized" 엔티티클래스 모델에 맞춰 매핑 하여 "Repository(SpecializedRepository)"로 저장
 * 그리고 "list"라는 액션 명을 가진 액션 재요청*/

package com.project.controller;

import java.io.BufferedInputStream; 
import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

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
import com.project.repository.SpecializedRepository;

//import lombok.RequiredArgsConstructor;



@org.springframework.stereotype.Controller
public class Controller {
	@Autowired
	private SpecializedRepository Repository;
	JSONArray jArray = new JSONArray();
    
	@GetMapping("/api")
	public String load_save(@RequestParam("serviceKey") String serviceKey,@RequestParam("page") Integer page,@RequestParam("category") String category,
			@RequestParam("data_api") String data_api,@RequestParam("startdate")String start_date, @RequestParam("enddate")String end_date,Model model) {
		//@RequestParam을 이용해서 index.html에서 입력값을 받아온다.
		model.addAttribute("data", "");
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
		if(Requestcategory.equals("현행법령")){//if 문을 사용하여 데이터에 따라 실행되는 Mapping코드를 나눈다.
			try {
				String RequestserviceKey= serviceKey;// 받아온 Service key
				mappinglist[0] ="법령명한글";
				mappinglist[1] ="법령약칭명";
				mappinglist[2] ="";
				mappinglist[3] ="";
				mappinglist[4] ="";
				mappinglist[5] ="";
				mappinglist[6] ="";
				mappinglist[7] ="소관부처명";
				mappinglist[8] ="";
				mappinglist[9] ="";
				mappinglist[10] ="공포일자";
				mappinglist[11] ="시행일자";
				mappinglist[12] ="법령일련번호";
				mappinglist[13] ="법령상세링크";
				mappinglist[14] ="";
				mappinglist[15] ="";
				mappinglist[16] ="제개정구분명";
				mappinglist[17] ="";
				mappinglist[18] ="";
				mappinglist[19] ="";
				
		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				int Requestpage= page;// 받아온 page수
				if(page>=263||page<=0) {
					model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
					model.addAttribute("error_code", "CODE : EF_R_003");
					model.addAttribute("error_reason", "사유 : 정확한 페이지를 입력해주시기 바랍니다.");
					model.addAttribute("error_page", "요청하신 Page :"+page);
					model.addAttribute("error_pagenum", "현행법령의 페이지 수 범위는 1 ~ 262쪽입니다.");
					return "index";	 
				}



				URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+RequestserviceKey+"&target=law&type=XML"+ /*url 주소*/
						"&page="+Requestpage);	// URI를 URL객체로 저장
				BufferedReader bf; // 버퍼 데이터(응답 메세지)를 읽어서 result에 저장
				bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));// 
				result = bf.readLine(); // result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음 //readLine() : 한 줄을 읽어 String으로 반환.
				jsonObject = XML.toJSONObject(result.toString()); //result 값을 XMLtoJson를 사용하여 json형식으로 변환
				jsonObject2 = jsonObject.getJSONObject("LawSearch");//key: LawSearch의 Value 값을 JSONObject 객체 jsonObject2로 받습니다.
			    jArray = jsonObject2.getJSONArray("law"); //key: law의 Value 값을 JSONArray 객체 jArray로 받습니다.
				for(int i=0; i<jArray.length(); i++){ //for문을 통해 JsonArray의 수 만큼 리스트 안의 객체를 분리
					j++;
					JSONObject item = (JSONObject)jArray.get(i);
					// 원하는 항목명을 Parsing 해서 형식에 맞춰 변수에 저장  
					try {
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("법령명한글").toString()+quotes+"}");   
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE :  EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Title");
						return "index";}
					try {
						if(!item.get("법령약칭명").equals("")) {
							subject = (("{"+quotes+"org"+quotes+":"+quotes+item.get("법령약칭명").toString()+quotes+"}"));
						}else{
							subject = (("{"+quotes+"org"+quotes+":"+quotes+item.get("법령명한글").toString()+quotes+"}"));
						}
						
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Subject");
						return "index";}
					try { 
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("법령명한글")+quotes+"}}")); 
						//{"summary":{"org":"제1장 서 론 제2장 새로운 패러다임과 미래 농업기술 제3장 농업의 현황과 전망 제4장 관련 정책과 사례 분석 제5장 충북 농업의 미래성장 분야 제6장 충북 농업의 미래성장분야 육성 방안"}}
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Description");
						return "index";}
					try {
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Publisher");
						return "index";}
					try {
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Contributors");
						return "index";}
					try {
						date = ("{"+quotes+"issued"+quotes+":"+quotes+item.get("시행일자").toString()+quotes+","+quotes+"created"+quotes+":"+quotes+item.get("공포일자").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Date");
						return "index";}
					try {
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Language");
						return "index";}
					try {
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("법령일련번호").toString()+quotes+","+quotes+"url"+quotes+":"+quotes+item.get("법령상세링크").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Identifier");
						return "index";}
					try {
						format = (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Format");
						return "index";}
					try {
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+item.get("제개정구분명").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Relation");
						return "index";}
					try {
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Coverage");
						return "index";}
					try {
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Right");
						return "index";}
//					raw_title =item.get("법령명한글").toString();
//					raw_subject = item.get("법령약칭명").toString();
//					raw_description = item.get("법령명한글").toString();
//					raw_publisher = item.get("소관부처명").toString();
//					raw_contributors = item.get("소관부처명").toString();
//					raw_date_1 =item.get("시행일자").toString();
//					raw_date_2 =item.get("공포일자").toString();
//					raw_language = "";
//					raw_identifier_1 = item.get("법령명한글").toString();
//					raw_identifier_2 = item.get("법령명한글").toString();
//					raw_format =item.get("법령명한글").toString();
//					raw_relation = item.get("법령명한글").toString();
//					raw_coverage= item.get("법령명한글").toString();
//					raw_right= item.get("법령명한글").toString();

					// 저장한 변수를 Specialized 객체에 저장
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
				model.addAttribute("error_reason", "사유 : 정확한 인증키를 입력해주시기 바랍니다.");
				model.addAttribute("error_key", "요청하신 Key : "+serviceKey);
				model.addAttribute("error_page", "요청하신 Page : "+page);
				return "index";  

			}
		}else if(Requestcategory.equals("행정규칙")) {
			try {
				String RequestserviceKey= serviceKey;// 받아온 Service key
				mappinglist[0] ="행정규칙명";
				mappinglist[1] ="";
				mappinglist[2] ="";
				mappinglist[3] ="";
				mappinglist[4] ="";
				mappinglist[5] ="";
				mappinglist[6] ="";
				mappinglist[7] ="소관부처명";
				mappinglist[8] ="";
				mappinglist[9] ="";
				mappinglist[10] ="시행일자";
				mappinglist[11] ="생성일자";
				mappinglist[12] ="행정규칙ID";
				mappinglist[13] ="행정규칙상세링크";
				mappinglist[14] ="";
				mappinglist[15] ="";
				mappinglist[16] ="제개정구분명";
				mappinglist[17] ="";
				mappinglist[18] ="";
				mappinglist[19] ="";
				
		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				int Requestpage= page;
				if(page>=968||page<=0) {
					model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
					model.addAttribute("error_code", "CODE : EF_R_003");
					model.addAttribute("error_reason", "사유 : 정확한 페이지를 입력해주시기 바랍니다.");
					model.addAttribute("error_page", "요청하신 Page : "+page);
					model.addAttribute("error_pagenum", "행정규칙의 페이지 수 범위는 1 ~ 967쪽입니다.");
					return "index";	 
				}
				// URI를 URL객체로 저장

				URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+RequestserviceKey+"&target=admrul&query=학교&type=XML"+ /*url 주소*/
						"&page="+Requestpage);
				//http://www.law.go.kr/DRF/lawSearch.do?OC=helena0809&target=admrul&query=%ED%95%99%EA%B5%90&type=XML
				//URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+serviceKey+"&target=law&type=XML"+ /*url 주소*/
				//"&page="+page);

				//https://www.law.go.kr/DRF/lawSearch.do?OC=helena0809&target=law&type=XML&page=1
				// 버퍼 데이터(응답 메세지)를 읽어서 result에 저장
				// result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음
				BufferedReader bf;
				bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				result = bf.readLine();
				jsonObject = XML.toJSONObject(result.toString());
				jsonObject2 = jsonObject.getJSONObject("AdmRulSearch");
				jArray = jsonObject2.getJSONArray("admrul");      
				for(int i=0; i<jArray.length(); i++){
					j++;
					JSONObject item = (JSONObject)jArray.get(i);
					try { 
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("행정규칙명").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Title");
						return "index";}
					try {
						subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("행정규칙명").toString()+quotes+"}]"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Subject");
						return "index";}
					try { 
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("행정규칙명")+quotes+"}}"));
						
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Description");
						return "index";}
					try {
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Publisher");
						return "index";}
					try {
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("소관부처명")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Contributors");
						return "index";}
					try {
						date = ("{"+quotes+"issued"+quotes+":"+quotes+item.get("시행일자").toString()+quotes+","+quotes+"created"+quotes+":"+quotes+item.get("생성일자").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Date");
						return "index";}
					try {
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Language");
						return "index";}
					try {
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("행정규칙ID").toString()+","+"url:"+item.get("행정규칙상세링크").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Identifier");
						return "index";}
					try {
						format = (("{\"org\":\""+"\"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Format");
						return "index";}
					try {
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+item.get("제개정구분명").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Relation");
						return "index";}
					try {
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Coverage");
						return "index";}
					try {
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Right");
						return "index";}
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
				String RequestserviceKey= serviceKey;// 받아온 Service key
				mappinglist[0] ="전문자료 메인 제목";
				mappinglist[1] ="주제1";
				mappinglist[2] ="주제2";
				mappinglist[3] ="주제3";
				mappinglist[4] ="전문자료 문서 타입";
				mappinglist[5] ="전문자료 문서 새요약";
				mappinglist[6] ="전문자료 문서 목차";
				mappinglist[7] ="전문자료 부서 코드";
				mappinglist[8] ="전문자료 문서 저자";
				mappinglist[9] ="전문자료 등록자";
				mappinglist[10] ="전문자료 등록 일자";
				mappinglist[11] ="전문자료 승인 일자";
				mappinglist[12] ="전문자료 문서 아이디";
				mappinglist[13] ="";
				mappinglist[14] ="";
				mappinglist[15] ="";
				mappinglist[16] ="";
				mappinglist[17] ="";
				mappinglist[18] ="";
				mappinglist[19] ="";
				
		        pitches = new ArrayList<>(Arrays.asList(mappinglist));
				int Requestpage= page;// 받아온 page수
				if(page>=2401||page<=0) {
					model.addAttribute("error_name", "ERROR : 데이터 수집 ERROR~!!");
					model.addAttribute("error_code", "CODE : EF_R_003");
					model.addAttribute("error_reason", "정확한 페이지를 입력해주시기 바랍니다.");
					model.addAttribute("error_page", "요청하신 Page :"+page);
					model.addAttribute("error_pagenum", "전문자료의 페이지 수 범위는 1 ~ 2400쪽입니다.");
					return "index";	 
				}
				// URI를 URL객체로 저장
				URL url = new URL("https://api.odcloud.kr/api/15092231/v1/uddi:f485c10f-f5d2-4a00-a993-b85d929565ec"+ /*url 주소*/
						"?page="+Requestpage+ /*page 수*/
						"&perPage=20"+ /*출력할 데이터 수*/
						"&serviceKey="+RequestserviceKey /*인증키*/);

				/* "https://api.odcloud.kr/api/15092231/v1/uddi:f485c10f-f5d2-4a00-a993-b85d929565ec/?page=Requestpage
				 * /&perPage=10/&serviceKey=RequestserviceKey" */

				//Buffer를 사용하여 url의 데이터를 출력
				BufferedReader bf;
				bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				result = bf.readLine(); //출력한 데이터 한 줄 씩 읽기

				JSONObject obj = new JSONObject(result.toString()); //result 값을 Json 형태의 Object(객체)로 분리
				jArray = (JSONArray) obj.get("data"); //분리된 Object에서 "data"라는 객체를 Json형태의 리스트로 분리

				for(int i=0; i<jArray.length(); i++){ //
					j++;
					JSONObject item = (JSONObject)jArray.get(i); //for문을 통해 JsonArray의 수 만큼 리스트 안의 객체를 분리
					try {
						//title = (("\"org\":\""+item.get("전문자료 메인 제목").toString()+"\""));
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 메인 제목").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Title");
						return "index";}
					try {
						if(!item.get("주제1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("주제1").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}else if(!item.get("주제2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("주제2").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}else if(!item.get("주제3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("주제3").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 메인 제목").toString()+quotes+","+quotes+item.get("전문자료 문서 타입").toString()+quotes+"}]"));
						}
//						subject = (("[{\"org\":\""+item.get("주제1")+",\""+item.get("주제2")+"\""+
//								",\""+item.get("주제3")+"\""+",\""+item.get("전문자료 문서 타입")+"\"}]"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Subject");
						return "index";}
					try {
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 문서 새요약").toString()+quotes+
								"}"+","+quotes+"toc"+quotes+":"+"{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 문서 목차")+quotes+"}}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Description");
						return "index";}
					try {
						publisher = (("{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 부서 코드")+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Publisher");
						return "index";} 
					try { 
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 등록자")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}"+","+
								+quotes+"affiliation"+quotes+":"+quotes+"[{"+quotes+"org"+quotes+":"+quotes+item.get("전문자료 문서 저자")+quotes+"}]]"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Contributors");
						return "index";}
					try { 
						date = (("{"+quotes+"registration"+quotes+":"+quotes+item.get("전문자료 등록 일자").toString().substring(0, 16)+quotes+","+quotes+"approval"+quotes+":"+quotes+item.get("전문자료 승인 일자").toString().substring(0, 16)+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Date");
						return "index";}
					try { 
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Language");
						return "index";}
					try { 
						identifier = (("{"+quotes+"id"+quotes+":"+quotes+item.get("전문자료 문서 아이디").toString().substring(0, 4)+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Identifier");
						return "index";}
					try { 
						format = (("{"+quotes+"media"+quotes+":"+quotes+"text"+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Format");
						return "index";}
					try { 
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Relation");
						return "index";}
					try { 
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Coverage");
						return "index";}
					try { 
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Right");
						return "index";}

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
		}else if(Requestcategory.equals("정책뉴스")) {
			
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
				mappinglist[14] ="OriginalimgUrl";
				mappinglist[15] ="";
				mappinglist[16] ="";
				mappinglist[17] ="";
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
				StringBuffer result1= new StringBuffer();
				// URI를 URL객체로 저장
				URL url = new URL("http://apis.data.go.kr/1371000/policyNewsService/policyNewsList?"
						+ "serviceKey="+RequestserviceKey
						+ "&startDate="+start_date
						+ "&endDate="+end_date); 
				
				//URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+RequestserviceKey+"&target=law&type=XML"+ /*url 주소*/
						//"&page="+Requestpage);
				//https://apis.data.go.kr/1371000/policyNewsService/policyNewsList?serviceKey=Ar6tat092yzvcQYUO8wCqMtbnxBjQwCGjNL4BsE6kmpaWXECc153R13Nj2eUkD%2FHQw3Zi9YroLnnTzc4q1L%2Flw%3D%3D&startDate=20211201&endDate=20211203
			            BufferedReader bf;
			            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			            urlConnection.connect();
			            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
			            bf = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
			            String returnLine;
			            while((returnLine = bf.readLine()) != null) {
			                result1.append(returnLine);
			            }

				//http://www.law.go.kr/DRF/lawSearch.do?OC=helena0809&target=admrul&query=%ED%95%99%EA%B5%90&type=XML
				//URL url = new URL("http://www.law.go.kr/DRF/lawSearch.do?OC="+serviceKey+"&target=law&type=XML"+ /*url 주소*/
				//"&page="+page);

				//https://www.law.go.kr/DRF/lawSearch.do?OC=helena0809&target=law&type=XML&page=1
				// 버퍼 데이터(응답 메세지)를 읽어서 result에 저장
				// result에는 XML 형식의 응답 데이터가 String으로 저장되어 있음
			            jsonObject = XML.toJSONObject(result1.toString());
			            jsonObject2 = jsonObject.getJSONObject("response");
			            jsonObject3 = jsonObject2.getJSONObject("body");
			            jArray= jsonObject3.getJSONArray("NewsItem");     
//				JSONObject jsonObject = XML.toJSONObject(result.toString()); //result 값을 XMLtoJson를 사용하여 json형식으로 바꾼뒤 json형태의 Object(객체)로 분리
//				JSONObject jsonObject2 = jsonObject.getJSONObject("LawSearch");//분리된 객체를 다시 json형태의 Object(객체)로 분리
//				JSONArray jArray = jsonObject2.getJSONArray("law");
				for(int i=0; i<jArray.length(); i++){
					j++;
					JSONObject item = (JSONObject)jArray.get(i);
					try { 
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("Title").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Title");
						return "index";}
					try {
						if(!item.get("SubTitle1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle1")+quotes+"}]"));
						}else if(!item.get("SubTitle2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle2")+quotes+"}]"));
						}else if(!item.get("SubTitle3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle3")+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("Title")+quotes+"}]"));
						}
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Subject");
						return "index";}
					try { 
						description = (("{"+quotes+"summary"+quotes+":"+quotes+"org"+quotes+":"+quotes+item.get("DataContents")+quotes+"}")); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Description");
						return "index";}
					try {
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Publisher");
						return "index";}
					try {
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Contributors");
						return "index";}
					try {
						date = ("{"+quotes+"modified"+quotes+":"+quotes+item.get("ModifyDate").toString()+","+"available:"+item.get("ApproveDate").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Date");
						return "index";}
					try {
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Language");
						return "index";}
					try {
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("NewsItemId").toString()+quotes+","+quotes+"view"+quotes+":"+quotes+item.get("OriginalUrl").toString()+quotes+","+quotes+"thumbs"+quotes+":"+quotes+item.get("OriginalimgUrl").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Identifier");
						return "index";}
					try {
						format = (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Format");
						return "index";}
					try {
						relation = ("{"+quotes+"isPartOF"+quotes+":"+quotes+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Relation");
						return "index";}
					try {
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Coverage");
						return "index";}
					try {
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Right");
						return "index";}
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
				
				
				
				
				
				
				
			
				
				
				
				
				
				
				
				
				
				
				//기업요구사항 전체 데이터셋 저장용 코드
				URL url = new URL("http://apis.data.go.kr/1371000/pressReleaseService/pressReleaseList"
				+ "?serviceKey="+RequestserviceKey
				+ "&startDate="+start_date
				+ "&endDate="+end_date);
				
				
				
				
				
				
				
				
				
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
					try { 
						title = ("{"+quotes+"org"+quotes+":"+quotes+item.get("Title").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Title");
						return "index";}
					try {
						if(!item.get("SubTitle1").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle1")+quotes+"}]"));
						}else if(!item.get("SubTitle2").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle2")+quotes+"}]"));
						}else if(!item.get("SubTitle3").equals("")) {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("SubTitle3")+quotes+"}]"));
						}else {
							subject = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("Title")+quotes+"}]"));
						}
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Subject");
						return "index";}
					try { 
						description = (("{"+quotes+"summary"+quotes+":{"+quotes+"org"+quotes+":"+quotes+item.get("DataContents")+quotes+"}")); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Description");
						return "index";}
					try {
						publisher = ("{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Publisher");
						return "index";}
					try {
						contributors = (("[{"+quotes+"org"+quotes+":"+quotes+item.get("MinisterCode")+quotes+","+quotes+"role"+quotes+":"+quotes+"author"+quotes+"}]")); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Contributors");
						return "index";}
					try {
						date = ("{"+quotes+"modified"+quotes+":"+quotes+item.get("ModifyDate").toString()+","+"available:"+item.get("ApproveDate").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Date");
						return "index";}
					try {
						language = ("{"+quotes+"org"+quotes+":"+quotes+"ko"+quotes+"}"); 
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Language");
						return "index";}
					try {
						identifier = ("{"+quotes+"site"+quotes+":"+quotes+item.get("NewsItemId").toString()+","+"view:"+item.get("OriginalUrl").toString()+quotes+"}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Identifier");
						return "index";}
					try {
						format = (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Format");
						return "index";}
					try {
						relation = ("{"+quotes+"related"+quotes+":["+quotes+item.get("FileName").toString()+quotes+","+quotes+item.get("FileUrl").toString()+quotes+"]}");
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Relation");
						return "index";}
					try {
						coverage= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Coverage");
						return "index";}
					try {
						right= (("{"+quotes+"org"+quotes+":"+quotes+quotes+"}"));
					}catch(Exception e) {
						model.addAttribute("error_name", "ERROR : 증분 데이터 ERROR~!!");
						model.addAttribute("error_code", "CODE : EF_R_001");
						model.addAttribute("error_column", "수집 실패한 데이터항목: Right");
						return "index";}
					
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
		}
		else{
			model.addAttribute("error_name", "ERROR : 매핑 ERROR~!!");
			model.addAttribute("error_code", "CODE : EF_R_002");
			model.addAttribute("error_reason", "현행법령, 행정규칙, 전문자료, 정책뉴스, 보도자료 이외의 데이터를 입력하셨습니다.");
			return "index"; 
		}
		if(data_api.equals("data")) {
			// 매핑된 데이터 
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
			
			
			
			
			String rawdata2_title = mappinglist[0];
			String rawdata2_subject1 = mappinglist[1];
			String rawdata2_subject2 = mappinglist[2];
			String rawdata2_subject3 = mappinglist[3];
			String rawdata2_subject4 = mappinglist[4];
			String rawdata2_description1 = mappinglist[5];
			String rawdata2_description2 = mappinglist[6];
			String rawdata2_publisher = mappinglist[7];
			String rawdata2_contributors1 = mappinglist[8];
			String rawdata2_contributors2 = mappinglist[9];
			String rawdata2_date1 = mappinglist[10];
			String rawdata2_date2= mappinglist[11];
			String rawdata2_identifier1 = mappinglist[12];
			String rawdata2_identifier2 = mappinglist[13];
			String rawdata2_identifier3 = mappinglist[14];
			String rawdata2_format= mappinglist[15];
			String rawdata2_relation1 = mappinglist[16];
			String rawdata2_relation2 = mappinglist[17];
			String rawdata2_coverage = mappinglist[18];
			String rawdata2_right = mappinglist[19];
			

			JSONObject rawdata=(JSONObject) jArray.get(j-1);
			String rawdata_title ="";
			String rawdata_subject1 ="";
			String rawdata_subject2 ="";
			String rawdata_subject3 ="";
			String rawdata_subject4 ="";
            String rawdata_description1 = "";
			
			String rawdata_description2 = "";
	
			String rawdata_publisher = "";
			String rawdata_contributors1 = "";
			String rawdata_contributors2 = "";
			String rawdata_date1 = "";
			String rawdata_date2= "";
			String rawdata_identifier1 = "";
			String rawdata_identifier2 = "";
			String rawdata_identifier3 = "";
			String rawdata_format= "";
			String rawdata_relation1 = "";
			String rawdata_relation2 = "";
			String rawdata_coverage ="";
			String rawdata_right = "";
			
			
			
			if(!pitches.get(0).equals("")) {
			rawdata_title = String.valueOf(rawdata.get(pitches.get(0)));}
			if(!pitches.get(1).equals("")) {
			rawdata_subject1 =String.valueOf(rawdata.get(pitches.get(1)));}
			if(!pitches.get(2).equals("")) {
			rawdata_subject2 = String.valueOf(rawdata.get(pitches.get(2)));}
			if(!pitches.get(3).equals("")) {
			rawdata_subject3 = String.valueOf(rawdata.get(pitches.get(3)));}
			if(!pitches.get(4).equals("")) {
			rawdata_subject4 = String.valueOf(rawdata.get(pitches.get(4)));}
			if(!pitches.get(5).equals("")) {
			rawdata_description1 = String.valueOf(rawdata.get(pitches.get(5)));}
			if(!pitches.get(6).equals("")) {
			rawdata_description2 = String.valueOf(rawdata.get(pitches.get(6)));}
			if(!pitches.get(7).equals("")) {
			rawdata_publisher = String.valueOf(rawdata.get(pitches.get(7)));}
			if(!pitches.get(8).equals("")) {
			rawdata_contributors1 = String.valueOf(rawdata.get(pitches.get(8)));}
			if(!pitches.get(9).equals("")) {
			rawdata_contributors2 = String.valueOf(rawdata.get(pitches.get(9)));}
			if(!pitches.get(10).equals("")) {
			rawdata_date1 = String.valueOf(rawdata.get(pitches.get(10)));}
			if(!pitches.get(11).equals("")) {
			rawdata_date2= String.valueOf(rawdata.get(pitches.get(11)));}
			if(!pitches.get(12).equals("")) {
			rawdata_identifier1 =String.valueOf(rawdata.get(pitches.get(12)));}
			if(!pitches.get(13).equals("")) {
			rawdata_identifier2 = String.valueOf(rawdata.get(pitches.get(13)));}
			if(!pitches.get(14).equals("")) {
			rawdata_identifier3 = String.valueOf(rawdata.get(pitches.get(14)));}
			if(!pitches.get(15).equals("")) {
			rawdata_format= String.valueOf(rawdata.get(pitches.get(15)));}
			if(!pitches.get(16).equals("")) {
			rawdata_relation1 = String.valueOf(rawdata.get(pitches.get(16)));}
			if(!pitches.get(17).equals("")) {
			rawdata_relation2 = String.valueOf(rawdata.get(pitches.get(17)));}
			if(!pitches.get(18).equals("")) {
			rawdata_coverage =String.valueOf(rawdata.get(pitches.get(18)));}
			if(!pitches.get(19).equals("")) {
			rawdata_right = String.valueOf(rawdata.get(pitches.get(19)));}
			String rawdata_Colon0 ="";
			String rawdata_Colon1 ="";
			String rawdata_Colon2 ="";
			String rawdata_Colon3 ="";
			String rawdata_Colon4 ="";
			String rawdata_Colon5 ="";
			String rawdata_Colon6 ="";
			String rawdata_Colon7 ="";
			String rawdata_Colon8 ="";
			String rawdata_Colon9 ="";
			String rawdata_Colon10="";
			String rawdata_Colon11="";
			String rawdata_Colon12 ="";
			String rawdata_Colon13 ="";
			String rawdata_Colon14 ="";
			String rawdata_Colon15 ="";
			String rawdata_Colon16 ="";
			String rawdata_Colon17 ="";
			String rawdata_Colon18 ="";
			String rawdata_Colon19 ="";
			
			    if(!pitches.get(0).equals("")) {
				rawdata_Colon0 = ":";}
				if(!pitches.get(1).equals("")) {
				rawdata_Colon1 =":";}
				if(!pitches.get(2).equals("")) {
				rawdata_Colon2 = ":";}
				if(!pitches.get(3).equals("")) {
				rawdata_Colon3 = ":";}
				if(!pitches.get(4).equals("")) {
				rawdata_Colon4 = ":";}
				if(!pitches.get(5).equals("")) {
				rawdata_Colon5 = ":";}
				if(!pitches.get(6).equals("")) {
				rawdata_Colon6 = ":";}
				if(!pitches.get(7).equals("")) {
				rawdata_Colon7 = ":";}
				if(!pitches.get(8).equals("")) {
				rawdata_Colon8 = ":";}
				if(!pitches.get(9).equals("")) {
				rawdata_Colon9 = ":";}
				if(!pitches.get(10).equals("")) {
				rawdata_Colon10 = ":";}
				if(!pitches.get(11).equals("")) {
				rawdata_Colon11= ":";}
				if(!pitches.get(12).equals("")) {
				rawdata_Colon12 =":";}
				if(!pitches.get(13).equals("")) {
				rawdata_Colon13 = ":";}
				if(!pitches.get(14).equals("")) {
				rawdata_Colon14 = ":";}
				if(!pitches.get(15).equals("")) {
				rawdata_Colon15= ":";}
				if(!pitches.get(16).equals("")) {
				rawdata_Colon16 = ":";}
				if(!pitches.get(17).equals("")) {
				rawdata_Colon17 = ":";}
				if(!pitches.get(18).equals("")) {
				rawdata_Colon18 =":";}
				if(!pitches.get(19).equals("")) {
				rawdata_Colon19 = ":";}
			
//			for(int k=0; k<20; k++) {
//				if(!pitches.get(k).equals("")) {
//					rawdata_Colon =":";
//			}else {
//				rawdata_Colon ="";
//			}
//				}
			model.addAttribute("rawdata_Colon0",rawdata_Colon0);
			model.addAttribute("rawdata_Colon1",rawdata_Colon1);
			model.addAttribute("rawdata_Colon2",rawdata_Colon2);
			model.addAttribute("rawdata_Colon3",rawdata_Colon3);
			model.addAttribute("rawdata_Colon4",rawdata_Colon4);
			model.addAttribute("rawdata_Colon5",rawdata_Colon5);
			model.addAttribute("rawdata_Colon6",rawdata_Colon6);
			model.addAttribute("rawdata_Colon7",rawdata_Colon7);
			model.addAttribute("rawdata_Colon8",rawdata_Colon8);
			model.addAttribute("rawdata_Colon9",rawdata_Colon9);
			model.addAttribute("rawdata_Colon10",rawdata_Colon10);
			model.addAttribute("rawdata_Colon11",rawdata_Colon11);
			model.addAttribute("rawdata_Colon12",rawdata_Colon12);
			model.addAttribute("rawdata_Colon13",rawdata_Colon13);
			model.addAttribute("rawdata_Colon14",rawdata_Colon14);
			model.addAttribute("rawdata_Colon15",rawdata_Colon15);
			model.addAttribute("rawdata_Colon16",rawdata_Colon16);
			model.addAttribute("rawdata_Colon17",rawdata_Colon17);
			model.addAttribute("rawdata_Colon18",rawdata_Colon18);
			model.addAttribute("rawdata_Colon19",rawdata_Colon19);
			
			// 원본데이터 내용	
			model.addAttribute("rawdata_title", rawdata_title);
			model.addAttribute("rawdata_subject1", rawdata_subject1);
			model.addAttribute("rawdata_subject2", rawdata_subject2);
			model.addAttribute("rawdata_subject3", rawdata_subject3);
			model.addAttribute("rawdata_subject4", rawdata_subject4);
			model.addAttribute("rawdata_description1", rawdata_description1);
			model.addAttribute("rawdata_description2", rawdata_description2);
			model.addAttribute("rawdata_publisher", rawdata_publisher);
			model.addAttribute("rawdata_contributors1", rawdata_contributors1);
			model.addAttribute("rawdata_contributors2", rawdata_contributors2);
			model.addAttribute("rawdata_date1", rawdata_date1);
			model.addAttribute("rawdata_date2", rawdata_date2);
			model.addAttribute("rawdata_identifier1", rawdata_identifier1);
			model.addAttribute("rawdata_identifier2", rawdata_identifier2);
			model.addAttribute("rawdata_identifier3", rawdata_identifier3);
			model.addAttribute("rawdata_format", rawdata_format);
			model.addAttribute("rawdata_relation1", rawdata_relation1);
			model.addAttribute("rawdata_relation2", rawdata_relation2);
			model.addAttribute("rawdata_coverage", rawdata_coverage);
			model.addAttribute("rawdata_right", rawdata_right);
			
			//원본 데이터 제목
			model.addAttribute("rawdata2_title", rawdata2_title);
			model.addAttribute("rawdata2_subject1", rawdata2_subject1);
			model.addAttribute("rawdata2_subject2", rawdata2_subject2);
			model.addAttribute("rawdata2_subject3", rawdata2_subject3);
			model.addAttribute("rawdata2_subject4", rawdata2_subject4);
			model.addAttribute("rawdata2_description1", rawdata2_description1);
			model.addAttribute("rawdata2_description2", rawdata2_description2);
			model.addAttribute("rawdata2_publisher", rawdata2_publisher);
			model.addAttribute("rawdata2_contributors1", rawdata2_contributors1);
			model.addAttribute("rawdata2_contributors2", rawdata2_contributors2);
			model.addAttribute("rawdata2_date1", rawdata2_date1);
			model.addAttribute("rawdata2_date2", rawdata2_date2);
			model.addAttribute("rawdata2_identifier1", rawdata2_identifier1);
			model.addAttribute("rawdata2_identifier2", rawdata2_identifier2);
			model.addAttribute("rawdata2_identifier3", rawdata2_identifier3);
			model.addAttribute("rawdata2_format", rawdata2_format);
			model.addAttribute("rawdata2_relation1", rawdata2_relation1);
			model.addAttribute("rawdata2_relation2", rawdata2_relation2);
			model.addAttribute("rawdata2_coverage", rawdata2_coverage);
			model.addAttribute("rawdata2_right", rawdata2_right);
			
			model.addAttribute("mapping_1", "[ 매핑전 데이터 예시 ]");
			model.addAttribute("mapping_2", "[ 매핑후 데이터 예시 ]");

			return "index"; //"list"라는 이름을 가직 액션을 찾아 리턴 
	    }else {
	    	return "redirect:/list"; //"list"라는 이름을 가직 액션을 찾아 리턴 
	    }
		}
}
