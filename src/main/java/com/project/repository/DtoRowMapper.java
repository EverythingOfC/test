package com.project.repository;

/* DB에서 가지고 올 데이터를 "Dto"클래스 모델에 맞춰 매핑*/

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.model.Dto;

public class DtoRowMapper implements RowMapper<Dto> {
	// Dto클래스의 모델을 원하는 형태의 값으로 반환하기 위해
	
	@Override
	public Dto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Dto dto = new Dto(); //Dto의 모델을 새로 지정.
		dto.setTitle(rs.getString("META_TITLE")); //새로 지정한 모델의 "title"값을 DB에 저장된 "META_TITLE" 값으로 저장
		
		dto.setSubjects(rs.getString("META_SUBJECT")); //이하 동문	
		
		dto.setDescription(rs.getString("META_DESC"));
		
		dto.setPublisher(rs.getString("META_PUBLISHER"));
				
		dto.setContributors(rs.getString("META_CONTRIBUTOR"));
		
		dto.setDate(rs.getString("META_DATE"));
		
		dto.setLanguage(rs.getString("META_LANGUAGE"));
		
		dto.setIdentifier(rs.getString("META_IDENTIFIER"));
		
		dto.setFormat(rs.getString("META_FORMAT"));
		
		dto.setRelation(rs.getString("META_RELATION"));
		return dto;
	}
}