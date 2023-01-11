package com.project.repository

/* "DtoRepository"에서 사용할 MySQL의 쿼리문을 변수를 생성.*/

class DtoSql {

	
	public static final String SELECT = """
		SELECT * FROM if_dk_item

	""";
	
	public static final String publisher = """
    SELECT * FROM if_dk_item
	where META_PUBLISHER LIKE CONCAT ('%',:META_PUBLISHER,'%')
	
	""";
	//{"org":"교육부"}
	// SQL의 select문의 쿼리를 SELECT라는 변수로 생성
	// where META_PUBLISHER = :META_PUBLISHER
	
}
/*
"SELECT * FROM if_dk_item where META_PUBLISHER  like '%"+":META_PUBLISHER"+"%'";*/