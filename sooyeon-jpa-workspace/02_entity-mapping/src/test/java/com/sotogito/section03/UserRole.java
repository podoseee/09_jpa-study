package com.sotogito.section03;

/**
 * 1. 상수 집합 정의 : 일정 범위 내에서만 가징 수 있는 값을 명확하게 표현
 * 2. 타입 안정성 보장 : enum으로 정해놓은 값만 들어올 수 있음, 다른 값이 들어올 때 컴파일 에러 발생
 * 3. 클래스처럼 동작 : 변수, 메서드, 생성자도 가질 수 있음
 * 4. 확장성 용이
 *
 *
 *     @Enumerated(EnumType.STRING) 사용 지향
 *
 */
public enum UserRole {

    ADMIN, /// Enum constant ordinal: 0
    USER; /// Enum constant ordinal: 1

}
