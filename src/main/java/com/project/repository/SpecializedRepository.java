package com.project.repository;

/* "Controller"를 통해 만든 엔티티클래스 모델을 "JpaRepository"라는 라이브러리를 통해 DB생성 후 저장 */

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.domain.Specialized;

public interface SpecializedRepository extends JpaRepository<Specialized, Long>{//JpaRepository< 엔티티 ID 유형>
//"JpaRepository"라는 인터페스이스를 사용하여 엔티티클레스의 값을 DB에 저장
}

