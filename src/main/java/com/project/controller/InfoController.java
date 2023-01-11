package com.project.controller;

/* "InfoService"에서 불러온 데이터들을 리스트 형식의 모델로 바꿔서 생성한 URL에 출력*/

import java.util.List;  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.Dto;
import com.project.service.InfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class InfoController {
	private InfoService infoService; //InFoService클래스에서 만든 함수를 사용하기위해 임폴트 
	
	@Autowired  // spring 4.3 버전 이상부터는 생략 가능
	public InfoController(InfoService infoService) {
		this.infoService = infoService;
	}
	@GetMapping("/list")// 액션 list로 보낸것을 받음
	public Object dtoList() {
		log.debug("/dtoList start"); //로그
		List<Dto> dtoList = infoService.getDtoList(); //InfoService의 getDtoList()라는 함수의 값을 Dto클래스의 모델을 리스트로 생성
		return dtoList; //생성한 리스트 모델을 리턴하여 출력
	}
	
	@GetMapping("/listPublisher")
	public Object Bypublisher(@RequestParam("META_PUBLISHER") String publiser ){ //@RequestParam("가져올 데이터의 이름") [데이터타입] [가져온데이터를 담을 변수]
		log.debug("publiser={}", publiser);
		List<Dto> dtoList = infoService.findPublisher(publiser);
		return dtoList;
	} 
	
	
}

