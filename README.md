#  CafeIs - 카페 주문 관리 시스템

<br>

##  프로젝트 소개

**CafeIs**는 카페 운영을 위한 키오스크 기반 주문 관리 시스템입니다.  
고객은 키오스크를 통해 메뉴를 확인하고 주문할 수 있으며, 실시간 매장 혼잡도를 확인할 수 있습니다.

<br>

## 🛠 기술 스택

### Backend
<div>
  <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.7-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
</div>

### Database
<div>
  <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white">
</div>

### Frontend
<div>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
</div>

### Tools
<div>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
</div>

<br>

##  주요 기능

###  고객 기능
- **메뉴 조회**: 카테고리별 상품 목록 확인
- **장바구니**: 상품 추가, 수량 조절, 삭제
- **주문하기**: 장바구니 기반 주문 생성
- **혼잡도 확인**: 실시간 매장 혼잡도 모니터링

###  관리 기능
- **주문 관리**: 주문 상태 변경, 픽업 시간 확정
- **상품 관리**: 카테고리별 상품 조회
- **지점 관리**: 매장별 주문 처리

<br>

## 시연 화면
https://youtu.be/2RTKNyqXONI


### API 문서 (Swagger)
```
http://localhost:8080/swagger-ui.html
```

<br>

##  시스템 아키텍처

###  ERD
```
![ERD](https://github.com/user-attachments/assets/2903ed7e-65dd-4610-856d-957a33fd5966.PNG)
```

###  파일 구조
```
src/main/java/com/example/cafeis/
├── config/          # 설정 (Security, Swagger)
├── controller/      # REST API 컨트롤러
├── domain/          # JPA 엔티티
├── dto/            # 데이터 전송 객체
├── enum/           # 열거형 상수
├── exception/      # 커스텀 예외
├── mapper/         # 엔티티-DTO 변환
├── repository/     # JPA Repository
└── service/        # 비즈니스 로직
```

<br>

##  팀 소개

| 역할 | 이름 | 담당 업무                           |
|------|------|---------------------------------|
| Backend | 개발자 | Spring Boot 기반 API 개발, JPA 설계, MariaDB 설계 및 최적화 |
| Frontend | 개발자 | 키오스크 UI/UX 구현                   |


<br>

## 실행 방법

### 요구사항
- Java 17
- MariaDB
- Gradle

### 실행 단계

1. **프로젝트 클론**
```bash
git clone https://github.com/[username]/cafeis.git
cd cafeis
```

2. **데이터베이스 설정**
```yaml
# src/main/resources/application-local.yml 생성
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/testdb
    username: your_username
    password: your_password
```

3. **프로젝트 실행**
```bash
./gradlew clean build
./gradlew bootRun
```

4. **접속 URL**
- 키오스크: http://localhost:8080/coffee-order-static.html
- API 문서: http://localhost:8080/swagger-ui.html
- 혼잡도: http://localhost:8080/congestion/home

<br>




<br>

---

<div align="center">
  <p>© 2025 CafeIs. All rights reserved.</p>
</div>

