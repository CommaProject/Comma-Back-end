# Comma ( 2023 - 2 ~ 2023 - 8 )
> 다른 사람과 음악을 공유하고 감정을 기록하는 웹 서비스

<img src="https://github.com/5tr1ker/5tr1ker/assets/49367338/66009156-8935-4a61-8c9c-e41c6c41c627" width="25%"> 
<img src="https://github.com/5tr1ker/5tr1ker/assets/49367338/782dfe01-eec9-40a4-b2af-cb1c29f9b52d" width="25%">

<h2>Project Introduce</h2>

Comma는 누군가가 추천해준 음악을 재생할 수 있으며 , 누군가에게 음악을 추천해줄 수 있습니다. 누군가에게 추천해주는 이 단순한 Flow가 우리의 매일 같은 하루 속 , 쉼표가 되어줄 것이라 생각합니다. <br/>

사용자는 언제 어디서 음악을 들으며 어떤 감정을 느꼈는지 아카이빙이 가능하며 자신이 현재 느끼는 감정의 깊이를 더하고자 합니다.<br/>

<h3>주요 기능</h3>
프로젝트 주요 기능은 다음과 같습니다.<br/><br/>

- 음악 재생 : 음악 탐색 , 가수 탐색 , 음악 재생 , 음악 아카이빙 ( 감정 기록 ) , 플레이리스트 생성 및 추천
- 사용자 : OAuth2.0 ( 네이버 , 카카오 , 구글 ) , 회원가입 및 로그인 , 팔로우

<h2>Project Structure</h2>

> React + Spring Boot 의 어플리케이션 구조

- Front-End
  - Typescript (Programming Language)
  - React (UI Framework)
  - Jotai (Global State)
  - React-query (Server State Management)
  - Styled-components (Styling)
  - Axios (HTTP Client)
  - Husky, commitlint (Code Convention)

- Back-End
  - Spring Boot
  - Spring Security ( OAuth 2.0 & JWT )
  - JPA ( QueryDSL )
  - HTTP API ( Rest API )
  - Lombok
  - MySQL ( RDBMS )
  - Docker
  - TDD ( JUnit 5 )
  - Jenkins / Github Action
  - Swagger & RestDocs
  - AWS S3 & EC2

<h2>ERD</h2>

![image](https://github.com/5tr1ker/Comma-Back-end/assets/49367338/78c4a07f-f515-4a20-8e02-37dac9183010)
 
<h2>Spring Security</h2>

> 권한에 따라 접근할 수 있는 API 를 분리했습니다.

- CSRF : disable
- password encryption : BCryptPasswordEncoder
- Session Creation Policy : STATELESS
- JwtAuthenticationFilter : before UsernamePasswordAuthenticationFilter.class

<h2>JPA & QueryDSL</h2>

> 객체 중심 설계와 반복적인 CRUD를 Spring Data JPA 로 최소화해 비즈니스 로직에 집중합니다.

- QueryDSL : 복잡한 JPQL 작성시 발생할 수 있는 문법오류를 컴파일 시점에 빠르게 찾아냅니다.</br>

<h2>TDD & Spring RestDocs</h2>

> 테스트 주도 개발 방식을 이용해 안정적인 코드를 생산합니다.

- Red , Green , Refactor 사이클을 따라 테스트 코드를 작성합니다.
- Controller 계층에서 Spring RestDocs 를 이용하여 API 문서를 제작했습니다. 

<h2>OAuth2.0 & JWT</h2>

> 구글 & 네이버 & 카카오 소셜 서버를 이용해 불필요한 회원가입을 줄이고 , JWT을 이용해 사용자 인증 정보를 클라이언트에 보관합니다.

- Access Token과 Refresh Token은 웹 브라우저 쿠키에 저장하며 httpOnly , Secure 옵션으로 보안처리 했습니다

<h2>CI / CD</h2>

> Jenkins 를 이용하여 지속적인 배포를 구성 , Github Action 을 통해 지속적인 통합을 구성

- Github Action 을 통해 테스트에 통과되지 않을 경우 Merge가 되지 않습니다.
- 병합된 코드는 Jenkins WebHook 을 통해 AWS 에 호스팅됩니다.

<h2>AWS EC2 & ELB </h2>

> 전체 프로젝트 인프라 구성

![image](https://github.com/5tr1ker/mystory/assets/49367338/9c3cacb3-7723-42e4-9a76-ee9105450e1f)

- Route 53 도메인으로 오는 요청을 ELB 에게 전달합니다.
- SSL 인증서를 이용해 https 보안설정을 했습니다.
- ELB ( Application Load Balance ) 경로 기반 포트포워딩을 활용해 OAuth2.0 서버와 클라이언트 서버의 트래픽 분산 처리를 했습니다. 
  - OAuth2.0 요청으로 오는 /oauth2/authorization/* 과 /login/oauth2/code/* 로 오는 요청은 직접 백엔드 서버와 연결됩니다.
  - 그 외 요청은 모두 클라이언트 서버 로 연결됩니다. [상세히](https://velog.io/@tjseocld/AWS-AWS-EC2-ALB-%ED%99%9C%EC%9A%A9)
 
<h2>1 . 로그인 / 회원가입</h2>

> 소셜 계정으로 회원가입을 하거나 , 별도로 가입할 수 있습니다.

 <img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/ded146d5-9a1e-4247-aceb-b40d36d8707d" width="25%">

- OAuth 2.0 를 통해 회원가입 과정을 생략할 수 있습니다.
- 로그인 후 발급되는 Access Token 과 Refresh Token 은 다음과 같이 보관합니다. 
  - AccessToken과 RefreshToken은 쿠키에 보관하나 Security , HTTPOnly 옵션을 추가해서 서버와 클라이언트 간 https 통신 및 자바스크립트로 쿠키 접근을 제한합니다. 
  - 로그아웃시 Access Token, Refresh Token 쿠키 삭제합니다.
 
<h2>2 . 음악 탐색</h2>

> Spotify API 를 활용해 트랙을 검색합니다.

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/78508ef8-78b7-4083-83fe-ee2454387381" width="25%">

- 최적화를 위해 검색된 트랙은 좋아요나 플레이리스트에 추가되지 않은 한 DB에 저장되지 않습니다.

<h2>3 . 사용자 탐색</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/fb59c9be-0418-489f-8884-5fe2a9458511" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/fd32dc64-7a59-4568-bc04-fb397e15313b" width="25%">

- 사용자를 탐색하고 팔로우할 수 있습니다.
- 조회된 사용자의 플레이리스트와 아카이브 , 좋아하는 장르 및 가수를 조회할 수 있습니다. 

<h2>4 . 음악 재생 </h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/8697ba13-2627-48b6-b493-5b3f04da6afa" width="25%">

- Spotify API 의 재생 URL 을 통해 클라이언트 쪽에서 음악을 재생합니다.
- 음악이 모두 재생될 경우에 자동으로 다음 트랙을 재생합니다.

<h2>5 . 플레이리스트</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/30b80e2e-b390-4744-bf5c-894347b43b45" width="25%">

- 플레이리스트 이름을 설정하고 곡을 저장, 수정할 수 있습니다.
- 플레이리스트에 있는 곡을 선택할 경우 음악 재생화면으로 이동됩니다.
  
<h2>6 . 플레이리스트 추천</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/992cdcf5-8bcc-41a1-9b24-6d05f4278b20" width="25%">

- 친구 & 익명에게 플레이리스트를 추천할 수 있으며 추천 받은 플레이리스트는 하단 메뉴에 표시됩니다.
- 내가 추천한 플레이리스트를 들은 횟수를 확인할 수 있습니다.

