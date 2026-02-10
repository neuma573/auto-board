# Auto Board

다양한 기능을 지원하는 **생성형 게시판(웹 + API)** 프로젝트입니다.

- 게시판/게시글/댓글/좋아요 기능
- 이메일 인증 기반 회원가입 및 JWT 인증
- Google/Naver OAuth 로그인
- 파일 업로드(에디터 이미지 포함)
- Rate Limit, reCAPTCHA, 차단 필터 등 보안 기능
- Azure OpenAI 연동(콘텐츠 생성 보조)

## 기술 스택

### Backend
- Java 17
- Spring Boot 3.1.5
- Spring Web / Validation / Mail
- Spring Data JPA / QueryDSL / Envers
- Spring Data Redis / Spring Session Redis
- OpenFeign
- JWT (jjwt)
- Bucket4j (Rate Limiter)
- Azure OpenAI SDK
- Google reCAPTCHA Enterprise

### Frontend
- Thymeleaf
- Vanilla JavaScript
- CSS

### Database / Infra
- MySQL
- Redis
- H2 (테스트)

---

## 주요 기능

- **게시판 관리**: 게시판 조회/생성/수정/삭제
- **게시글 관리**: 목록/상세/작성/수정/삭제 + 권한 체크
- **댓글 관리**: 댓글/대댓글 작성, 수정, 삭제
- **좋아요**: 게시글 좋아요 조회 및 토글
- **회원/인증**
  - 이메일 기반 회원가입 및 중복 체크
  - 로그인/로그아웃/토큰 재발급/토큰 검증
  - Google/Naver OAuth
- **정책 동의**: 이용약관 조회 및 동의 저장
- **파일 업로드**: 에디터 업로드 및 파일 접근 API

---

## 빠른 시작

### 1) 요구 사항

- JDK 17+
- MySQL
- Redis

### 2) 환경 변수 설정

`src/main/resources/application.yml` 기준으로 아래 값이 필요합니다.

```bash
DB_HOST=
DB_USER=
DB_PASSWORD=

REDIS_HOST=
REDIS_PORT=
REDIS_PASSWORD=
REDIS_USERNAME=

MAIL_HOST=
MAIL_PORT=
GOOGLE_MAIL_ADDRESS=
GOOGLE_APP_PASSWORD=
MAIL_PATH=

JWT_SECRET=
JWT_ISSUER=
DOMAIN=

OPENAI_KEY=

RECAPTCHA_V2_SITE_KEY=
RECAPTCHA_V3_SITE_KEY=
RECAPTCHA_PROJECT_ID=
GOOGLE_APPLICATION_CREDENTIALS=

NAVER_CLIENT_ID=
NAVER_CLIENT_SECRET=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

UPLOAD_PATH=
```

### 3) 실행

```bash
./gradlew bootRun
```

기본 포트는 `8080`입니다.

---

## 테스트

```bash
./gradlew test
```

테스트 프로필은 `application-test.yml`을 사용하며, H2 메모리 DB로 실행됩니다.

---

## API 개요

> 상세 스펙은 코드의 Controller를 참고하세요.

- `/api/v1/board` : 게시판 API
- `/api/v1/post` : 게시글 API
- `/api/v1/comment` : 댓글 API
- `/api/v1/like` : 좋아요 API
- `/api/v1/file` : 파일 API
- `/api/v1/users` : 회원 API
- `/api/v1/auth` : 인증 API
- `/api/v1/oauth2` : OAuth API
- `/api/v1/policy` : 정책 API

---

## 화면 라우트

- `/`, `/main` : 메인
- `/login` : 로그인
- `/signup-options`, `/join` : 회원가입
- `/write`, `/modify` : 글 작성/수정
- `/post` : 게시글 상세
- `/mypage` : 마이페이지

---

## 프로젝트 구조

```text
src/main/java/com/neuma573/autoboard
├── ai          # OpenAI 연동
├── board       # 게시판
├── post        # 게시글
├── comment     # 댓글
├── like        # 좋아요
├── file        # 파일 업로드
├── user        # 회원/OAuth
├── security    # 인증/인가/JWT/필터
├── policy      # 약관/동의
├── global      # 공통 설정/예외/유틸
└── mvc         # 페이지 라우팅
```

필요하면 다음 단계로 **배포용 README(아키텍처 다이어그램, ERD, 운영 체크리스트 포함)** 형태로 확장해드릴게요.
