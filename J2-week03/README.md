# J2-week03 - Docker와 Fly.io를 활용한 Spring Boot 배포

이번 주차에는 Docker의 기본 개념을 익히고, Docker 이미지와 컨테이너를 활용해 애플리케이션을 실행한 뒤 Fly.io에 Spring Boot 서비스를 배포하는 흐름을 학습했다. 또한 환경별 설정, GitHub Actions, Secret 관리와 CI/CD의 기본 개념까지 살펴보았다.

## 1. 애플리케이션 배포와 Docker

Java 애플리케이션은 보통 JAR 파일로 만들어지며 실행하려면 JVM이 필요하다.

Docker를 사용하면 애플리케이션과 실행에 필요한 환경을 이미지에 함께 담을 수 있어 서버마다 직접 환경을 구성하는 과정을 줄일 수 있다.

- **Dockerfile** : Docker 이미지를 만들기 위한 레시피
- **Docker Image** : 애플리케이션과 실행 환경을 포함한 템플릿
- **Docker Container** : 이미지를 실제로 실행한 환경
- **Docker Hub** : Docker 이미지를 저장하고 공유하는 레지스트리

~~~text
Dockerfile
   ↓ build
Docker Image
   ↓ run
Docker Container
~~~

## 2. Nginx 샘플 앱과 Docker 이미지

간단한 HTML 파일을 Nginx로 서비스하는 Docker 이미지를 만들어 Docker의 기본 흐름을 실습했다.

~~~bash
docker build -t nginx-1 .
docker images
docker rmi nginx-1
~~~

`Dockerfile`을 기준으로 이미지를 생성하고, 생성된 이미지는 여러 개의 컨테이너를 만드는 데 사용할 수 있다.

## 3. 컨테이너 실행과 포트 포워딩

이미지를 실행하면 컨테이너가 생성된다.

~~~bash
docker run -p 8080:80 nginx-1
~~~

위 명령어는 로컬 PC의 `8080` 포트와 컨테이너 내부의 `80` 포트를 연결한다.

컨테이너 관리에 사용한 주요 명령어는 다음과 같다.

~~~bash
docker ps
docker ps -a
docker exec -it <컨테이너이름> bash
docker rm -f <컨테이너이름>
~~~

## 4. Fly.io를 이용한 배포

로컬에서 실행되는 서비스를 외부에서도 접근할 수 있도록 Fly.io를 이용해 배포하는 과정을 실습했다.

~~~text
프로젝트
  ↓
Dockerfile
  ↓
fly launch
  ↓
fly.toml
  ↓
fly deploy
  ↓
외부에서 서비스 접근
~~~

처음 앱을 등록할 때는 `fly launch`, 실제 배포와 재배포에는 `fly deploy`를 사용한다.

## 5. 개발환경과 운영환경 분리

개발환경과 운영환경에서는 포트, 데이터베이스 주소, 각종 설정값이 달라질 수 있다.

Spring Boot에서는 환경별 설정 파일을 분리해 관리할 수 있다.

~~~text
application.yml
application-dev.yml
application-prod.yml
~~~

동일한 소스 코드를 사용하면서 실행 환경에 따라 서로 다른 설정을 적용할 수 있다.

## 6. GitHub Actions와 CI/CD

기존에는 코드를 GitHub에 push한 뒤 Fly.io 배포 명령을 별도로 실행해야 했다.

GitHub Actions를 사용하면 push를 기준으로 빌드와 배포를 자동화할 수 있다.

~~~text
git push
   ↓
GitHub Actions
   ↓
Docker Build
   ↓
Fly.io Deploy
~~~

- **CI (Continuous Integration)** : 코드 변경 사항을 지속적으로 통합하고 검증
- **CD (Continuous Delivery / Deployment)** : 검증된 애플리케이션을 배포하는 과정

## 7. Secret 관리

비밀번호, API Key, Access Token과 같은 민감한 정보는 저장소에 그대로 올리지 않고 별도로 관리해야 한다.

대표적인 예시는 다음과 같다.

- DB 비밀번호
- API Key
- Fly.io Access Token
- JWT Secret

GitHub Actions에서는 **Secrets**에 값을 등록하고 워크플로우에서 이를 참조해 사용할 수 있다.

## 8. 무중단 배포와 데이터 영속성

여러 컨테이너를 활용하면 기존 서버를 유지한 상태에서 새로운 버전을 실행해 서비스 중단을 줄일 수 있다.

또한 데이터를 메모리에만 저장하면 서버나 컨테이너가 종료될 때 데이터가 사라지므로 실제 서비스에서는 MySQL, PostgreSQL 같은 영속성 데이터베이스를 사용해야 한다.

## 정리

이번 주차의 전체 흐름은 다음과 같다.

~~~text
Spring Boot
    ↓
Dockerfile
    ↓
Docker Image
    ↓
Docker Container
    ↓
Fly.io 배포
    ↓
GitHub Actions
    ↓
CI/CD 자동 배포
~~~

Docker를 이용한 애플리케이션 패키징부터 Fly.io 배포, 환경별 설정, Secret 관리, GitHub Actions를 활용한 자동 배포까지 백엔드 서비스 배포의 전체 흐름을 익혔다.
