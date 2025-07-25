# 2인 프로젝트 - File Park
<p align="center" title="filepark-logo">
  <img src="./readme_images/logo.png" width="256" />
</p>

## 📖 프로젝트 소개
> 사진, 동영상 등의 파일을 그룹별로 공유하고 관리할 수 있는 웹 서비스이며, 사용자는 그룹을 생성하거나 참여하여 파일을 업로드하고 관리할 수 있습니다.  
> 또한, 그룹별로 실시간 채팅 기능을 제공하여 사용자 간의 소통을 지원합니다.
- **프로젝트명**: File Park
- **개발 기간**: 2025.03.05 ~ 2025.03.11
- **인원**: **2인 프로젝트 개발**

## 🖥️ 화면 구성
|미리보기|
|:---:|
|<img src="./readme_images/main.gif" width="800" />|

|홈페이지|로그인 페이지|
|:---:|:---:|
|<img src="./readme_images/home.png" width="400" />|<img src="./readme_images/login0.png" width="400" />|

|카카오 로그인|네이버 로그인|
|:---:|:---:|
|<img src="./readme_images/login1.png" width="400" />|<img src="./readme_images/login2.png" width="400" />|

|그룹 참여 모달|그룹 생성 모달|
|:---:|:---:|
|<img src="./readme_images/home_part.png" width="400" />|<img src="./readme_images/home_create.png" width="400" />|

|그룹 내에 파일 리스트 조회|그룹 설정|
|:---:|:---:|
|<img src="./readme_images/groups.png" width="400" />|<img src="./readme_images/groups_setting.png" width="400" />|

|우클릭 메뉴|사용자 프로필|
|:---:|:---:|
|<img src="./readme_images/right_contextmenu.png" width="400" />|<img src="./readme_images/profile.png" width="400" />|

|파일 업로드|파일 일괄 업로드|
|:---:|:---:|
|<img src="./readme_images/upload.png" width="400" />|<img src="./readme_images/upload_many.png" width="400" />|

## ⚙ 기술 스택
### Framework
![Spring Boot](https://shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

### Backend
![Spring MVC](https://shields.io/badge/Spring%20MVC-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MyBatis](https://shields.io/badge/MyBatis-0F4C81?style=for-the-badge&logo=mybatis&logoColor=white)
![Java](https://shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)

### Frontend
![Thymeleaf](https://shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![JQuery](https://shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white)
![JavaScript](https://shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![daisyUI](https://shields.io/badge/daisyUI-1D4ED8?style=for-the-badge&logo=daisyui&logoColor=white)

### Database
![MySQL](https://shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### DevOps
![Git](https://shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Jenkins](https://shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)
![Docker](https://shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Naver Compute Server](https://shields.io/badge/Naver%20Compute%20Server-03C75A?style=for-the-badge&logo=naver&logoColor=white) 

## 📌 주요 기능
- OAuth2.0 기반 로그인/회원가입(Kakao, Naver)
- 그룹 생성 및 참여 기능
- 그룹별 파일 업로드 및 관리
- 파일 일괄 업로드 및 다운로드 기능
- 파일 및 폴더 일괄 삭제 기능
- 파일 및 폴더 CRUD 기능
- 우클릭 메뉴를 통한 파일 및 폴더 관리
- 사용자 프로필 조회 및 수정
- 그룹별 실시간 채팅 기능
- 그룹 설정 기능 (그룹명, 그룹 설명, 그룹 구성원, 그룹 삭제 등)

## 👥 프로젝트 팀원 및 담당
|이름|역할|
|:---:|:---|
|[장진욱](https://github.com/chauid)|DB 설계, 홈페이지, 그룹 페이지, 파일 및 폴더 CRUD 구현, CI/CD|
|[전종원](https://github.com/jw6963)|사용자 프로필, 그룹별 채팅 기능, OAuth 기반 로그인/회원가입|
