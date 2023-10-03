# Comma ( 2023 - 2 ~ 2023 - 9 )

> 다른 사람과 음악을 공유하고 감정을 기록하는 웹 서비스

<h2>Project Introduce</h2>

총 4명의 개발진과 4명의 기획자 겸 디자이너가 함께 제작한 서비스로 기존 음악 재생 서비스의 기능을 확장하여 제작된 프로젝트입니다.
테스트 주도 개발 방식을 통해 소프트웨어의 품질을 향상시키고 커뮤니케이션 능력을 기를 수 있는 프로젝트가 되었습니다.
협업 툴과 형상 관리 도구를 이용해 팀원과 효율적인 협업 방법과 , 팀원 간 의견 충돌 및 갈등을 해결하는 방법을 알게되었습니다.

<h2>프로젝트 개요 & 주요 기능</h2>

기존 음악 재생 어플리케이션은 자신 만의 플레이리스트를 만들고 나만의 시간을 갖는 시스템을 발전시켜, 현재 내가 듣고있는 음악에 대해 현재 느끼고 있는 감정을 기록하고, 
더 나아가 내 감정을 공유하기 위해 다른 사용자에게 플레이리스트를 추천하는 기능을 더했습니다. <br/><br/>
따라서 이 서비스는 사용자가 음악을 들으며 어떤 감정을 느꼈는지 아카이빙이 가능하며 자신이 현재 느끼는 감정의 깊이를 더하고자 합니다.<br/>
누군가에게 추천해주는 이 단순한 Flow가 우리의 매일 같은 하루 속 , 쉼표가 되어줄 것이라 생각합니다. <br/>

- 음악 : 음악 탐색 , 가수 탐색 , 음악 재생 , 음악 아카이빙 ( 감정 기록 )
- 플레이리스트 : 플레이리스트 생성 및 수정
- 추천 : 사용자가 설정한 시간에 플레이리스트 추천 ( 푸시알림 )
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

![comma ERD](https://github.com/5tr1ker/Comma-Back-end/assets/49367338/5042ac0c-55fe-4d2a-9cd0-b38ee7d26bac)

- 데이터베이스 정규화를 통해 이상 현상 제거 및 최적화를 했습니다.
  
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

> 테스트 주도 개발 방법론을 이용하여 소프트웨어 품질을 향상시켰습니다.

- 200 여개가 넘는 테스트 코드를 Red , Green , Refactor 사이클을 따라 작성했습니다.
- Controller Test 에서 Spring RestDocs 를 이용하여 API 문서를 제작했습니다. 

<h2>SSE ( Server Send Event ) & Spring Scheduler 푸시 알람</h2>

> SSE 를 이용하여 사용자 푸시 알림을 구현했습니다.

- 로그아웃 상태의 사용자에게 전송된 데이터는 제거 ( 유실 ) 합니다.
- Spring Scheduler 를 통해 10분마다 특정 시간에 설정된 알림을 계속 조회합니다.
- 단방향 통신을 이용하여 리소스 낭비를 최소화했습니다.

<h2>OAuth2.0 & JWT</h2>

> 구글 & 네이버 & 카카오 소셜 서버를 이용해 불필요한 회원가입을 줄이고 , JWT을 이용해 사용자 인증 정보를 클라이언트에 보관합니다.

- Access Token과 Refresh Token은 웹 브라우저 쿠키에 저장하며 httpOnly 옵션으로 보안처리 했습니다

<h2>CI / CD</h2>

> Jenkins 를 이용하여 지속적인 배포를 구성 , Github Action 을 통해 지속적인 통합을 구성

- Github Action 을 통해 테스트에 통과되지 않을 경우 Merge가 되지 않습니다.
- 병합된 코드는 Jenkins WebHook 을 통해 AWS 에 호스팅됩니다.
- AWS 서버는 프론트엔드 개발자가 테스트를 용이하게 하기 위해 가동했습니다.
 
<h2>1 . 로그인 / 회원가입</h2>

> 소셜 계정으로 회원가입을 하거나 , 별도로 가입할 수 있습니다.

 <img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/ded146d5-9a1e-4247-aceb-b40d36d8707d" width="25%">

 <img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/2af3c76c-998a-4e6f-b512-142647fc57a9" width="25%">

- OAuth 2.0 를 통해 회원가입 과정을 생략할 수 있습니다.
- 로그인 후 발급되는 Access Token 과 Refresh Token 은 다음과 같이 보관합니다. 
  - AccessToken과 RefreshToken은 쿠키에 보관하나 HTTPOnly 옵션을 추가해 클라이언트에서 자바스크립트로 쿠키 접근을 제한합니다. 
  - 로그아웃시 Access Token, Refresh Token 쿠키 삭제합니다.
 
<h2>2 . 음악 탐색 ( 탐색 , 탐색 결과 ) </h2>

> Spotify API 를 활용해 트랙을 검색합니다.

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/78508ef8-78b7-4083-83fe-ee2454387381" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/2bc3d4cb-01a8-421b-9870-dce8c76f9cb4" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/cf1bf79d-8572-444c-a3b7-38a09c92a4eb" width="25%">

- Spotify API 라이브러리를 활용해 음악을 검색합니다.
- 최적화를 위해 검색된 트랙은 좋아요나 플레이리스트에 추가되지 않은 한 DB에 저장되지 않습니다.

<h2>3 . 가수 탐색</h2>

> Spotify API 를 활용해 가수를 검색합니다.

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/2ca7f398-7bcd-419c-98b0-54022f1e23eb" width="25%">

- Spotify API 라이브러리를 활용해 가수를 검색합니다.

<h2>4 . 사용자 탐색</h2>

> Spotify API 를 활용해 사용자를 검색합니다.

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/fd32dc64-7a59-4568-bc04-fb397e15313b" width="25%">

- 사용자를 탐색하고 팔로우할 수 있습니다.
- 조회된 사용자의 플레이리스트와 아카이브 , 좋아하는 장르 및 가수를 조회할 수 있습니다. 

<h2>5 . 음악 재생 및 아카이브 작성</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/9190525b-f3ea-4778-af3d-95cb5d196140" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/5f7cff8e-91f5-459e-b70b-4249592cdcd5" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/1369a60d-2146-473c-b841-f0adeb9ca10a" width="25%">

- Spotify API 의 재생 URL 을 통해 클라이언트 쪽에서 음악을 재생합니다.
- 음악이 모두 재생될 경우에 자동으로 다음 트랙을 재생합니다.
- 아카이브를 통해 현재 듣고 있는 플레이리스트에 대해 내 감정을 기록합니다.

<h2>6 . 플레이리스트 ( 메인 , 수정 , 알림 시간 수정 ) </h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/d8924a95-072a-4cbe-b42f-8d0c1f6f39eb" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/30b80e2e-b390-4744-bf5c-894347b43b45" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/c566d833-19b1-403c-ac0c-e9b7c4b3c0a7" width="25%">

- 플레이리스트 이름을 설정하고 곡을 저장, 수정 , 추가할 수 있습니다.
- 플레이리스트에 있는 곡을 선택할 경우 음악 재생화면으로 이동됩니다.
- 알림이 설정된 플레이리스트는 설정한 시간에 푸시 알림을 통해 사용자에게 알려줍니다. ( SSE 기반 )
  
<h2>7 . 플레이리스트 추천하기</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/992cdcf5-8bcc-41a1-9b24-6d05f4278b20" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/6b6ea381-7384-4291-af8a-6919c7c25e11" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/de2b10fa-510c-4542-b33a-66a86e585e39" width="25%">

- 친구 & 익명에게 플레이리스트를 추천할 수 있으며 추천 받은 플레이리스트는 하단에 표시됩니다.
- 내가 추천한 플레이리스트는 추천 받은 사용자가 몇번 들었는지 확인할 수 있습니다.

<h2>8 . 추천받은 플레이리스트</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/2ff764ad-0c9d-4b99-827c-0580d2c601dd" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/41f85eb2-04bc-4bc0-b64e-69fafae32293" width="25%">

<h2>9 . 프로필 ( 메인 , 내 플레이리스트 , 내 아카이브 , 수정 )</h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/3d653df0-2361-41f7-bf08-d32af3a46f74" width="20%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/97455709-8be7-4b2a-b72f-716fc1f65ba5" width="20%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/1b8fb493-3abc-47bd-a877-57e9962a9b40" width="20%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/fce23845-c3a6-4780-89a2-6e4d5fee97f5" width="20%">

- 현재 팔로우 수 및 나와 관련된 정보들을 한눈에 보기 쉽게 출력했습니다. ( 내가 팔로우한 가수 및 사용자 , 내가 만든 플레이리스트 )
- 특정 날짜마다 작성된 아카이브를 보기 쉽게 정렬 및 표기합니다.

<h2>10 . 프로필 수정 </h2>

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/c1292367-7c01-4d38-876a-447bb969a80d" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/3085e23d-1378-4b77-8716-620fe5272746" width="25%">

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/7003aa16-6e91-4afb-8691-b64d00ca1e54" width="25%">

<h2>11 . 알림</h2>

> SSE 를 통해 푸시 알림을 구현했습니다.

<img src="https://github.com/5tr1ker/Comma-Back-end/assets/49367338/50e9fa3c-a073-43e4-814d-38d5245def52" width="25%">

- 사용자가 설정한 플레이리스트 알림 시간이 되었을 때 푸시 알람을 통해 사용자에게 알려줍니다. 
