# Comma
> 다른 사람과 음악을 공유하고 감정을 기록하는 웹 서비스

<img src="https://github.com/5tr1ker/5tr1ker/assets/49367338/66009156-8935-4a61-8c9c-e41c6c41c627" width="25%"> 
<img src="https://github.com/5tr1ker/5tr1ker/assets/49367338/782dfe01-eec9-40a4-b2af-cb1c29f9b52d" width="25%">

<h2>Project Introduce</h2>
Comma는 누군가가 추천해준 음악을 재생할 수 있으며 , 누군가에게 음악을 추천해줄 수 있습니다. 누군가에게 추천해주는 이 단순한 Flow가 우리의 매일 같은 하루 속 , 쉼표가 되어줄 것이라 생각합니다. <br/>

사용자는 언제 어디서 음악을 들으며 어떤 감정을 느꼈는지 아카이빙이 가능하며 자신이 현재 느끼는 감정의 깊이를 더하고자 합니다.<br/>

<h3># 주요 기능</h3>
프로젝트 주요 기능은 다음과 같습니다.<br/><br/>

- 음악 : 음악 탐색 , 음악 재생 , 음악 아카이빙 ( 감정 기록 ) , 플레이리스트 생성 및 추천
- 사용자 : OAuth2.0 ( 네이버 , 카카오 , 구글 ) , 회원가입 및 로그인 , 팔로우

<h2>Project Structure</h2>

> React ( SPA ) + Spring Boot ( Gradle ) 의 어플리케이션 구조

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
  - Jenkins / Github Action
  - Swagger & RestDocs
  - AWS S3 & EC2
 
<h2>Spring Security</h2>

> 권한에 따라 접근할 수 있는 API 를 분리했습니다.

- CSRF : disable
- password encryption : BCryptPasswordEncoder
- Session Creation Policy : STATELESS
- JwtAuthenticationFilter : before UsernamePasswordAuthenticationFilter.class

<h2>JPA & QueryDSL</h2>
> 객체 중심 설계와 반복적인 CRUD를 Spring Data JPA 로 최소화해 비즈니스 로직에 집중합니다.

- QueryDSL : 복잡한 JPQL 작성시 발생할 수 있는 문법오류를 컴파일 시점에 빠르게 찾아냅니다.</br>

<h2>OAuth2.0 & JWT</h2>

> 구글 & 네이버 & 카카오 소셜 서버를 이용해 불필요한 회원가입을 줄이고 , JWT Token을 이용해 인증 시스템을 구현했습니다.

- OAuth & JWT 구조는 다음과 같습니다.
![image](https://github.com/5tr1ker/mystory/assets/49367338/8efff265-cfb9-499a-b98d-6a3b9ff40ffd)

- Access Token과 Refresh Token은 클라이언트에 httpOnly , Secure 옵션으로 보안처리 했습니다

<h2>CI / CD</h2>

> Jenkins 를 이용하여 지속적인 배포 서버를 구성 , Github Action 을 통해 지속적인 통합을 구성

- Github Action 을 통해 테스트에 통과되지 않을 경우 Merge가 되지 않습니다.
- 병합된 코드는 Jenkins WebHook 을 통해 AWS 에 호스팅됩니다.

<h2>AWS EC2 & ELB </h2>

> 전체 프로젝트 인프라 구성

![image](https://github.com/5tr1ker/mystory/assets/49367338/9c3cacb3-7723-42e4-9a76-ee9105450e1f)

- Route 53 를 통해 도메인으로 오는 요청을 ELB 에게 전달합니다.
- 도메인은 SSL 인증서를 이용해 https 보안설정을 했습니다.
- ELB ( Application Load Balance ) 를 활용해 OAuth2.0 을 처리하기 위해 경로 기반 포워딩을 했습니다.
  - OAuth2.0 요청으로 오는 /oauth2/authorization/* 과 /login/oauth2/code/* 로 오는 요청은 직접 백엔드 서버와 연결됩니다.
  - 그 외 요청은 모두 클라이언트 서버 로 연결됩니다. [상세히](https://velog.io/@tjseocld/AWS-AWS-EC2-ALB-%ED%99%9C%EC%9A%A9)
 
