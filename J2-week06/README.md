# 6주차 - AWS EC2, Terraform, DB와 CI/CD 학습 정리

## 학습 개요

이번 주차에는 Spring Boot 애플리케이션을 로컬에서 실행하는 것부터 AWS EC2에 배포하고, GitHub Actions로 배포를 자동화하는 흐름을 학습했다. 이전 주차의 JPA·MySQL 내용과 이어지는 부분으로, DB 영속성, 개발/운영 환경 구분, Docker, Terraform, 리버스 프록시까지 정리했다.

**학습 자료:** 「12가지 기술로 구현하는 APP서비스」 관련 강의(제공된 01~20강 요약 기준)

**진행 범위:** 이번 주는 강의 내용을 중심으로 개념과 실습 절차를 정리했다. 이 폴더의 Terraform·Docker 파일은 **참고용 설정 예제**로 작성했으며, AWS 인스턴스 생성이나 배포 성공을 확인한 실제 실습 결과는 아니다. 계정·비용이 필요한 실습은 진행했다고 표시하지 않았다.

## 1. Spring Boot와 데이터베이스

### Spring Data JPA의 동작 흐름

요청이 들어와 Repository 메서드를 호출하면 Spring Data JPA가 JPA 구현체(Hibernate)를 이용하고, JDBC 드라이버를 거쳐 DB에 SQL을 실행한다.

```text
Controller → Service → Repository → JPA/Hibernate → JDBC → Database
```

- **JPA:** 자바 객체와 관계형 데이터베이스를 연결하기 위한 표준 명세
- **Hibernate:** JPA의 대표 구현체
- **Spring Data JPA:** Repository 인터페이스를 통해 기본 CRUD를 편리하게 구현
- **영속성:** 서버를 재시작해도 외부 DB에 저장한 데이터가 남는 성질

### H2, MySQL, PostgreSQL

| DB | 용도 및 특징 |
| --- | --- |
| H2 | 설치 부담이 적은 로컬 개발·테스트용 DB. 메모리 모드는 종료 시 데이터 소멸 |
| MySQL | 관계형 DB. 서버 실행과 사용자/비밀번호 등 접속 설정이 필요 |
| PostgreSQL | 관계형 DB. JPA를 통해 연동할 수 있으며 실제 사용 시 JDBC 드라이버와 SQL 방언 차이를 확인 |

H2 파일 모드와 메모리 모드는 데이터 보존 방식이 다르다. 강의에서 사용한 `application-test.yml` 같은 별도 설정을 이용하면 테스트 환경을 분리할 수 있다.

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.h2database:h2'
runtimeOnly 'com.mysql:mysql-connector-j'
// PostgreSQL로 연결할 때는 해당 JDBC 드라이버 사용
// runtimeOnly 'org.postgresql:postgresql'
```

DB마다 SQL 문법은 완전히 동일하지 않다. 예를 들어 MySQL의 `AUTO_INCREMENT`와 PostgreSQL의 `GENERATED ... AS IDENTITY`는 자동 번호 생성 표현이 다르다.

## 2. 개발 환경과 운영 환경 분리

로컬 개발에서는 H2나 로컬 DB를, 배포 환경에서는 별도 MySQL/PostgreSQL 서버를 이용할 수 있다. 환경별 설정은 Spring Profile로 분리한다.

| 설정 | 개발 환경(예시) | 운영 환경(예시) |
| --- | --- | --- |
| Profile | `local` | `prod` |
| 데이터베이스 | H2 또는 로컬 MySQL | 영속 볼륨을 가진 MySQL/PostgreSQL |
| 로그 수준 | DEBUG/INFO | INFO/WARN |
| 초기 데이터 | 테스트용 데이터 | 운영 데이터에 영향을 주지 않도록 제어 |
| 비밀번호 | 환경변수 | 비밀 관리 기능 또는 환경변수 |

애플리케이션 설정에 실제 비밀번호를 직접 넣거나 GitHub에 업로드하지 않아야 한다.

## 3. AWS 기본 개념

- **IAM:** 사용자·역할과 권한을 관리한다. 루트 계정으로 일상 작업을 하지 않고 최소 권한을 부여한다.
- **VPC:** 클라우드 내부에서 네트워크 범위를 구분하는 공간.
- **Subnet:** VPC 내부의 IP 주소 대역. 공개/비공개 접근 구성을 구분할 수 있다.
- **Internet Gateway / Route Table:** 외부 인터넷과 연결되는 경로 설정.
- **Security Group:** 인스턴스에 들어오고 나가는 네트워크 트래픽을 제한한다.
- **EC2:** Spring Boot나 DB 등 프로그램을 실행할 수 있는 가상 서버.
- **AWS CLI:** 명령어로 AWS 자원을 조회·관리하는 도구.

강의에서는 EC2에 Docker, Git, MySQL, Redis, Nginx 등을 준비하는 순서를 다뤘다. 실무에서는 DB를 별도 관리형 서비스에 두는 방법도 있다.

## 4. Terraform으로 인프라 관리

Terraform은 서버 및 네트워크 구성을 코드로 정의하는 IaC(Infrastructure as Code) 도구다. 콘솔에서 자원을 하나씩 만드는 대신, 선언한 구성과 실제 상태를 비교한 뒤 적용할 수 있다.

| 명령어 | 의미 |
| --- | --- |
| `terraform init` | 프로바이더 다운로드·작업 디렉터리 초기화 |
| `terraform fmt` | Terraform 파일 형식 정리 |
| `terraform validate` | 기본 설정 유효성 확인 |
| `terraform plan` | 생성·변경·삭제 예정 자원 확인 |
| `terraform apply` | 계획을 실제 AWS에 적용(요금 발생 가능) |
| `terraform destroy` | Terraform으로 관리하는 인프라 제거 |

```bash
cd terraform
terraform init
terraform fmt
terraform validate
terraform plan -var="aws_region=ap-northeast-2"
# 주의: 아래 명령은 실제 과금될 수 있어 실행 전에 확인
# terraform apply -var="aws_region=ap-northeast-2"
# terraform destroy -var="aws_region=ap-northeast-2"
```

`terraform/main.tf`는 VPC·서브넷·인터넷 게이트웨이·라우트·보안 그룹·EC2 구성의 **예제**다. `terraform apply`를 실행하지 않았으며, 실행하려면 AWS 계정 설정과 비용 확인이 필요하다.

## 5. Docker와 컨테이너

Dockerfile은 애플리케이션 이미지를 만드는 방법을 정의하고, Docker Compose는 MySQL·Redis처럼 여러 컨테이너를 함께 실행할 때 사용할 수 있다.

- **Dockerfile:** Java 21 기반 Spring Boot 애플리케이션 빌드·실행 예제
- **docker-compose.yml:** 로컬 MySQL/Redis 예제 (포트는 로컬호스트로 제한)
- **.env.example:** 실제 비밀번호가 아닌 환경변수 이름만 정리
- **.gitignore:** 비밀번호, 상태 파일, 빌드 산출물 등이 업로드되지 않도록 제외

`Dockerfile`은 Gradle Spring Boot 프로젝트 루트에서 사용한다는 가정으로 작성한 예제다. 이 폴더 자체에는 Spring Boot 프로젝트 소스가 없어서 단독으로 빌드할 수 없다.

### 로컬 DB 실행 절차(참고)

```bash
cd J2-week06
cp .env.example .env
# .env에 로컬 개발용 비밀번호를 직접 설정 (GitHub 업로드 금지)
docker compose up -d
docker compose ps
docker compose down
```

`down`만 실행하면 이름 있는 볼륨은 남기 때문에 데이터가 보존된다. 볼륨까지 제거하는 `down -v`는 DB 데이터 삭제에 주의해야 한다.

### GitHub Container Registry(GHCR)

GitHub Actions에서 이미지를 빌드해 GHCR로 올리고, 서버가 새 이미지를 내려받아 실행하는 방식이다. 비공개 이미지 접근에는 적절한 권한의 토큰이 필요하며 토큰을 저장소에 기록하면 안 된다.

## 6. Redis, Nginx, 도메인

- **Redis:** 메모리 기반 저장소. 로그인 세션 등을 외부에 저장하면 애플리케이션 컨테이너를 교체할 때 세션 유지에 도움이 된다.
- **Nginx / Nginx Proxy Manager:** 외부 요청을 적절한 애플리케이션 포트로 전달하는 리버스 프록시.
- **DNS A 레코드:** 도메인을 서버 IP에 연결.
- **CNAME:** 도메인 이름을 다른 도메인 이름의 별칭으로 연결.
- **HTTPS:** TLS 인증서로 통신을 암호화.

Redis만 사용한다고 무중단 배포가 자동으로 완성되는 것은 아니다. 새 서버가 정상 실행되었는지 확인한 후 프록시가 트래픽을 전환하고 기존 서버를 종료하는 절차도 필요하다.

### 수동 배포와 자동 배포 비교

| 수동 배포 | CI/CD 자동 배포 |
| --- | --- |
| 소스 변경 후 직접 빌드/배포 | GitHub push 등을 계기로 자동 빌드 |
| EC2에서 직접 이미지 교체 | GitHub Actions에서 이미지 생성·배포 |
| 작업 순서가 많아 실수 가능 | 반복 작업을 스크립트화해 일관성 확보 |
| 교체 중 다운타임 가능 | 헬스체크와 프록시 전환으로 다운타임 최소화 가능 |

강의의 무중단 배포 설명에서는 기존 컨테이너를 먼저 내리지 않고 새 컨테이너를 다른 포트에 띄운 뒤, Actuator 등으로 상태를 확인하고 트래픽을 전환하는 흐름을 다뤘다.

```text
GitHub push → GitHub Actions → Docker 이미지 빌드 → GHCR
                                              ↓
                                       EC2 이미지 갱신
                                              ↓
                                   새 컨테이너 실행/헬스체크
                                              ↓
                                     Nginx 연결 전환
```

## 7. 배포를 진행할 때 확인할 사항

1. AWS 비용 알림과 자원 삭제 방법을 먼저 확인한다.
2. EC2 보안 그룹에서 SSH·DB·Redis 포트를 무분별하게 전체 공개하지 않는다.
3. IAM Access Key, DB 비밀번호, GitHub 토큰은 저장소에 업로드하지 않는다.
4. GitHub Actions 설정 시 필요한 권한만 부여한다.
5. 운영 환경에서는 DB 볼륨이나 별도 DB로 데이터를 보존한다.
6. 새 컨테이너 실행 후 헬스체크에 성공했을 때만 트래픽을 전환한다.
7. 문제가 생기면 직전 이미지로 복구할 수 있게 버전을 관리한다.

## 8. 첨부 파일

| 파일 | 설명 | 상태 |
| --- | --- | --- |
| [terraform/main.tf](./terraform/main.tf) | AWS VPC/EC2 기본 구성 | 참고 예제 |
| [terraform/variables.tf](./terraform/variables.tf) | 리전·인스턴스 유형 변수 | 참고 예제 |
| [terraform/outputs.tf](./terraform/outputs.tf) | 생성 자원의 IP 출력 | 참고 예제 |
| [Dockerfile](./Dockerfile) | Java 21 Spring Boot 이미지 빌드 | 참고 예제 |
| [docker-compose.yml](./docker-compose.yml) | 개발용 MySQL·Redis 컨테이너 | 참고 예제 |
| [.env.example](./.env.example) | 환경변수 이름 샘플 | 참고 예제 |

## 정리 및 느낀 점

이전에는 Spring Boot를 실행하고 DB를 연결하는 단계에 집중했는데, 이번 강의를 통해 애플리케이션을 외부에서 접근할 수 있도록 배포할 때 필요한 구성 요소를 전체적으로 이해할 수 있었다. 특히 Terraform은 인프라를 코드로 관리한다는 점, Docker는 같은 실행 환경을 이미지로 만들 수 있다는 점이 인상적이었다. 배포 자동화는 단순히 GitHub에 push하는 것으로 끝나는 것이 아니라 새 이미지의 생성, 헬스체크, 트래픽 전환까지 연결되는 과정이라는 점을 정리했다.

클라우드 비용이 발생하는 부분은 실제 배포 대신 개념과 설정 예제로 학습했으며, 이후 실습 환경을 갖추면 순서대로 검증해 볼 계획이다.
