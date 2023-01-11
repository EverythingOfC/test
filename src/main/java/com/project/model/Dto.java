package com.project.model;

/* DB에서 가지고 올 데이터의 "Dto" 클래스*/

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Dto {
	private String title;	
	private String subjects;
	private String description;
	private String publisher;
	private String contributors;
	private String date;	
	private String language;	
	private String identifier;
	private String format;
	private String relation;
}