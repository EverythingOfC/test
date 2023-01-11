package com.project.repository;

/* 미리 생성한 MySQL 쿼리문으로 데이터를 찾아와 "DtoRowMapper"를 통해 "Dto"클래스 모델에 맞춰 데이터를 매핑하는 함수 생성.*/

import java.util.List;

//import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.project.model.Dto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DtoRepository { 
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	//JDBC의 '?'방식 대신 명명된 매개변수로 사용할 수 있게 하는 라이브러리
	private final DtoRowMapper dtoRowMapper; //DtoRowMapper클래스에서 만든 함수를 사용하기위해 임폴트
	
	public DtoRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.dtoRowMapper= new DtoRowMapper(); //"dtoRowMapper"라는 DtoRowMapper모댈을 생성
	}
	
	public List<Dto> findList(){ 
		log.debug("findList query= {}", DtoSql.SELECT); //"findList"라는 쿼리문을 DtoSql라는 groovy클래스의 SELECT함수로 지정 
		
		
		return namedParameterJdbcTemplate.query(DtoSql.SELECT, EmptySqlParameterSource.INSTANCE
				, this.dtoRowMapper); 
		/* JDBC의 명명된 매개변수 쿼리문은 DtoSql이라는 groovy클래스의 SELECT 변수를 통해 "dtoRowMapper"라는 현재 생성된 객체를
		 * "EmptySqlParameterSource"의 배열로 리턴*/
	}
	public List<Dto> findPublisher(String META_PUBLISHER) {
		String qry =DtoSql.publisher;

		SqlParameterSource param = new MapSqlParameterSource("META_PUBLISHER",META_PUBLISHER);
		
		return namedParameterJdbcTemplate.query(qry, param, this.dtoRowMapper);
	}
	
	
	
}
