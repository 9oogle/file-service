# 📁 9oogle File Service - 파일 관리 서비스

## 🧭 목차

- [🚀 프로젝트 소개](#-프로젝트-소개)
- [🎯 핵심 기능](#-핵심-기능)
- [⚙️ 실행 방법](#️-실행-방법)
- [🛠️ 기술 스택](#️-기술-스택)
- [🏗️ 시스템 구조](#️-시스템-구조)
- [📌 설계 특징](#-설계-특징)
- [🗺️ 앞으로 할 일](#️-앞으로-할-일)

---

## 📅 프로젝트 기간

- 2026.04.16. ~ 2026.05.20.

---

## 🚀 프로젝트 소개

**9oogle File Service**는
**MSA 기반 온라인 강의 플랫폼**의 파일 도메인을 담당하는 백엔드 서비스입니다.
강의 썸네일 / 강의 영상 / 강의 자료 / 멘토링 이미지 / 사용자 프로필 등
**플랫폼 전반의 모든 파일 업로드 · 다운로드 · 조회 · 삭제**를 단일 책임으로 처리합니다.

### 📌 프로젝트 목표

- 다른 도메인 서비스(`lecture`, `mentoring`, `user`)에서 파일 관리 책임 완전 분리
- **Storage 추상화**로 로컬 / AWS S3 / GCS 등 저장소 전환 비용 최소화
- 태그(`FileTag`) 기반 파일 분류 및 권한 정책 일원화
- `groupId + FileTag` 조합만으로 다른 컨텍스트의 어떤 리소스에도 종속되지 않는 독립 설계

---

## 🎯 핵심 기능

### 📤 파일 업로드

- 멀티파트 기반 다중 파일 업로드 (`POST /api/v1/files`)
- `groupId` + `FileTag` 로 어느 도메인의 어떤 용도인지 분류
- 저장소(Local / S3 / GCS)는 설정으로 동적 결정
- 업로드 권한: 로그인 사용자만 가능

### 📥 파일 다운로드

- 파일 ID 기반 스트리밍 다운로드 (`GET /api/v1/files/{fileId}/download`)
- 태그별 다운로드 정책을 도메인 모델(`FileTag`)이 직접 들고 있음
    - `LECTURE_VIDEO`: 다운로드 불가 (스트리밍 전용)
    - `LECTURE_DOCUMENT`: 수강자만 다운로드 가능
    - `PROFILE`, `*_MAIN`, `*_LIST`, `*_DESCRIPTION`: 로그인 사용자 누구나 가능
- `MASTER` 권한은 모든 파일 다운로드 가능

### 🔍 파일 조회

- 파일 단건 상세 조회 (`GET /api/v1/files/{fileId}/details`)
- 그룹/태그 기반 파일 검색 (`GET /api/v1/files/search?groupId=&tag=`)
- 응답에 파일 URL, 이미지/영상 여부, 메타데이터 포함

### 🗑️ 파일 삭제

- 파일 ID 기반 삭제 (`DELETE /api/v1/files/{fileId}`)
- 삭제 권한: MASTER 또는 파일 소유자(업로드한 본인)
- Soft Delete 적용 (`deleted_at`, `deleted_by` / `@SQLRestriction`)

### 🏷️ 파일 태그(`FileTag`)

| 태그                      | 용도          | 다운로드 가능 | 수강 필요 |
|-------------------------|-------------|---------|-------|
| `PROFILE`               | 회원 프로필 이미지  | ✅       | ❌     |
| `LECTURE_LIST`          | 강의 목록 이미지   | ✅       | ❌     |
| `LECTURE_MAIN`          | 강의 메인 이미지   | ✅       | ❌     |
| `LECTURE_DESCRIPTION`   | 강의 상세설명 이미지 | ✅       | ❌     |
| `LECTURE_DOCUMENT`      | 강의 자료       | ✅       | ✅     |
| `LECTURE_VIDEO`         | 강의 동영상      | ❌       | -     |
| `MENTORING_LIST`        | 멘토링 목록 이미지  | ✅       | ❌     |
| `MENTORING_MAIN`        | 멘토링 메인 이미지  | ✅       | ❌     |
| `MENTORING_DESCRIPTION` | 멘토링 상세설명 이미지 | ✅       | ❌     |

### 👤 권한

- `X-User-Id`, `X-User-Role` 헤더 기반 사용자 식별 (Gateway 주입)
- `RoleChecker` 추상화로 권한 검증 (구현체: `HeaderRoleChecker`)
- 역할: `MASTER`, `INSTRUCTOR`, `STUDENT`

---

## ⚙️ 실행 방법

> ⚠️ 현재 file-service 는 **서비스 코드 단위까지만 작성된 단계**입니다.
> Dockerfile / docker-compose / 운영용 설정은 아직 준비되지 않았으며,
> 로컬에서 직접 Gradle 로 실행하는 형태입니다.

### 📦 사전 준비

file-service 는 **Spring Cloud Config Server + Eureka** 기반으로 동작하므로,
로컬 통합 테스트를 위해 9oogle 디스커버리 / 컨피그 인프라가 떠 있어야 합니다.

순서대로 실행:

```bash
# 1) discovery-server (Eureka) 실행
#    → http://localhost:9001 에서 대시보드 확인 가능
cd ../discovery-server
./gradlew bootRun

# 2) config-server 실행
#    → Eureka 에 자동 등록되어 Config 를 file-service 에 제공
cd ../config-server
./gradlew bootRun

# 3) PostgreSQL (별도로 띄워야 함)
#    예: 9oogle 통합 인프라 저장소의 docker-compose 활용
```

### 🚀 file-service 로컬 실행

#### 방법 1: Gradle 커맨드

```bash
./gradlew bootRun
```

#### 방법 2: IntelliJ Run Configuration

- `FileServiceApplication` 클래스 직접 실행
- 필요한 경우 Environment variables 에 `SPRING_PROFILES_ACTIVE` 지정

### 💾 저장소(Storage) 설정

저장소 타입은 `file.storage.type` 프로퍼티로 결정합니다.
각 저장소별 세부 설정은 `@ConfigurationProperties` prefix 기준으로 묶여 있습니다:

| 타입      | prefix              | 주요 키                                       |
|---------|---------------------|--------------------------------------------|
| `local` | `file.local.base`   | `path`, `url`                              |
| `s3`    | `file.s3`           | `bucket`, `region`, `accessKey`, `secretKey` |
| `gcs`   | `file.gcs`          | `bucket`, `projectId`, `keyPath`           |

```yaml
# 예시
file:
  storage:
    type: local         # local / s3 / gcs
    endpoint: http://localhost:8080   # 파일 URL 생성 시 사용되는 base
  local:
    base:
      path: ./storage/files
      url: /upload/
```

> 실제 환경별 값은 Config Server (`file-service/application-*.yaml`) 에서 관리할 예정입니다.

### 🧪 테스트 실행

```bash
./gradlew test
```

---

## 🛠️ 기술 스택

| 구분                | 기술                                                |
|-------------------|---------------------------------------------------|
| Backend           | Spring Boot 3.5.x, Java 21+                       |
| ORM / Query       | JPA, QueryDSL                                     |
| Architecture      | MSA, Clean Architecture, DDD, Ports & Adapters    |
| DB                | PostgreSQL                                        |
| Storage           | Local FileSystem / AWS S3 / Google Cloud Storage  |
| Service Discovery | Spring Cloud Eureka                               |
| Config            | Spring Cloud Config Server                        |
| Gateway           | Spring Cloud Gateway                              |
| Monitoring        | Spring Boot Actuator                              |
| Test              | JUnit 5, Mockito, AssertJ                         |
| CI                | GitHub Actions (PR Check, OpenAPI 자동 생성)          |
| Tools             | GitHub, IntelliJ IDEA, Notion                     |

---

## 🏗️ 시스템 구조

### 패키지 구조 (Clean Architecture)

```
com.goggles.file_service
├── presentation             # 컨트롤러, 요청/응답 DTO
│   ├── FileController       # /api/v1/files
│   └── dto                  # FileRequest, FileResponse
├── application              # 유스케이스
│   ├── FileService          # 업로드/다운로드/삭제
│   ├── query                # FileQueryService, FileQueryResult
│   └── dto                  # FileServiceDto
├── domain                   # 핵심 도메인 모델 + 포트 인터페이스
│   ├── FileInfo             # 애그리거트 루트
│   ├── FileGroup            # @Embeddable VO (groupId, tag)
│   ├── FileMeta             # @Embeddable VO (storage, fileName, contentType, contentLength)
│   ├── FileTag              # Enum (PROFILE, LECTURE_*, MENTORING_*)
│   ├── Storage              # Enum (LOCAL, S3, GCS)
│   ├── service              # FileUploader, FileDownloader, RoleChecker (포트)
│   ├── query                # FileQueryRepository
│   └── exception            # 도메인 예외
├── infrastructure           # 인프라 어댑터
│   ├── persistence          # JPA / QueryDSL 구현체
│   ├── security             # HeaderRoleChecker (헤더 기반 권한 검증)
│   └── storage              # Local/S3/GCS Uploader/Downloader 구현체 + Properties
└── global                   # JpaAuditingConfig, QuerydslConfig
```

### 설정 구조 (Config Server 기반)

| 위치                                                       | 내용                                    |
|----------------------------------------------------------|---------------------------------------|
| `src/main/resources/application.yaml`                    | bootstrap (Eureka, Config Server 연결만) |
| `src/main/resources/application-openapi.yaml`            | OpenAPI 문서 생성 전용 (H2)                 |
| **Config Server**: `file-service/application.yaml`       | file-service 전용 (예정)                  |
| **Config Server**: `file-service/application-{env}.yaml` | 환경별 전용 (예정)                           |

> 환경 설정을 바꿔야 하는 경우 **Config 저장소** 를 수정합니다.

### 주요 API 엔드포인트

#### 외부 API (`/api/v1/files`)

| Method   | Path                       | 설명             | 권한             |
|----------|----------------------------|----------------|----------------|
| `POST`   | `/files`                   | 파일 업로드 (다중 가능) | 로그인 사용자        |
| `GET`    | `/files/{fileId}/details`  | 파일 단건 상세 조회    | 누구나            |
| `GET`    | `/files/search`            | 파일 목록 검색       | 누구나            |
| `GET`    | `/files/{fileId}/download` | 파일 다운로드        | 태그별 정책에 따라 다름  |
| `DELETE` | `/files/{fileId}`          | 파일 삭제          | MASTER, 파일 소유자 |

#### 업로드 요청 예시

```http
POST /api/v1/files
Content-Type: multipart/form-data
X-User-Id: 11111111-2222-3333-4444-555555555555
X-User-Role: INSTRUCTOR

form-data:
  groupId  : {lectureId}
  tag      : LECTURE_MAIN
  file     : (binary, 다중 가능)
```

#### 검색 요청 예시

```http
GET /api/v1/files/search?groupId={lectureId}&tag=LECTURE_VIDEO
```

### 통신 방식

- **외부 요청** → Gateway → file-service
- **다른 서비스에서의 호출** → REST (예정: FeignClient 연동)
- **서비스 디스커버리** → Eureka

### 도메인 흐름

#### 📤 파일 업로드 흐름

```
[Client]
  → POST /api/v1/files (multipart, groupId + tag + file[])

[file-service]
  → RoleChecker.isLoggedIn() 검증
  → 저장소(FileUploader) 에 실제 파일 업로드
  → FileInfo 엔티티 생성 (groupId, tag, metadata, filePath)
  → DB 저장 후 fileId 반환
```

#### 📥 파일 다운로드 흐름

```
[Client]
  → GET /api/v1/files/{fileId}/download

[file-service]
  → FileInfo 조회 (없으면 FileNotFoundException)
  → FileInfo.verifyDownloadable(checker)
      - tag.isDownloadable() 검증
      - tag.isEnrollmentRequired() 인 경우 수강 여부 검증
  → FileDownloader 로 InputStream 획득
  → Content-Disposition: attachment 헤더 + 스트리밍 응답
```

#### 🗑️ 파일 삭제 흐름

```
[Client]
  → DELETE /api/v1/files/{fileId}

[file-service]
  → FileInfo 조회
  → FileInfo.delete(checker)
      - MASTER 또는 파일 소유자만 가능
  → Soft Delete (deleted_at, deleted_by)
```

---

## 📌 설계 특징

- **Clean Architecture + DDD**: presentation / application / domain / infrastructure 레이어 분리. 도메인 모델(`FileInfo`)이
  업로드 / 삭제 / 다운로드 가능 여부 등 비즈니스 규칙을 직접 들고 있음.
- **Storage 추상화 (Ports & Adapters)**: `FileUploader` / `FileDownloader` 포트 인터페이스를 도메인에 두고,
  Local / S3 / GCS 구현체를 인프라 계층에서 제공. 저장소 전환 시 도메인 / 애플리케이션 코드 변경 없음.
- **단일 책임 컨텍스트**: 파일에 대한 책임을 file-service 가 전담. lecture / mentoring / user 도메인은 파일 key 를 본인 엔티티에
  들고 있지 않고, `groupId + FileTag` 조합으로만 파일을 참조 → 도메인 간 결합도 최소화.
- **태그(`FileTag`) 기반 권한 정책**: 파일별 다운로드 가능 여부, 수강 여부 체크 필요성 등을 `FileTag` enum 속성으로 선언적 관리
  (`downloadable`, `enrollmentRequired`).
- **헤더 기반 사용자 식별**: 인증은 Gateway에서 처리되고, 서비스는 `X-User-Id`, `X-User-Role` 헤더만 받아 `HeaderRoleChecker` 가
  권한 검증 수행. Spring Security 미사용.
- **포트 & 어댑터 패턴**: `RoleChecker`, `FileUploader`, `FileDownloader` 모두 도메인 계층에 인터페이스를 두고 인프라 계층에서 구현 →
  도메인 테스트 용이성 확보.
- **Soft Delete 표준화**: 모든 엔티티에 `deleted_at`, `deleted_by` 적용. `@SQLRestriction("deleted_at IS NULL")` 으로
  조회 시 자동 필터링.

---

## 🗺️ 앞으로 할 일

현재 file-service 는 도메인 코드 중심으로 작성된 단계입니다.
이후 작업 예정 항목:

- [ ] **OpenAPI / Swagger UI 연동** (`springdoc-openapi` 의존성 추가 + `generateOpenApiDocs` task)
- [ ] **Dockerfile / docker-compose 작성** (로컬 PostgreSQL + file-service 통합 실행)
- [ ] **Config Server 설정 파일 추가** (`file-service/application.yaml`, 환경별 yaml)
- [ ] **lecture-service 의 수강 여부 확인 Internal API 연동** (`HeaderRoleChecker` TODO 해소)
- [ ] **다른 서비스에서 호출할 FeignClient 인터페이스 / 공통 모듈 정리**
- [ ] **운영 환경 Storage 구성 결정** (S3 vs GCS)

---

## 🚀 한 줄 소개

**플랫폼 전체의 파일 업로드 · 다운로드 · 조회 · 삭제를 단일 책임으로 처리하는, Storage 추상화 기반 파일 도메인 서비스**
