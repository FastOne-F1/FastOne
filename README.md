

<br>
<img src="https://capsule-render.vercel.app/api?type=waving&height=250&color=gradient&text=FastOne&fontSize=60&fontAlignY=30&animation=fadeIn&rotate=0&desc=AI%20활용%20비즈니스%20프로젝트&descSize=30&reversal=false" style="width: 120%;">
<br>

## 🎁팀원 소개
> **[JAVA 단기심화 부트캠프 4기]**<br>
> **AI 활용 비즈니스 프로젝트**<br>
> **팀 F1**

<table align="center">
 <tr>
    <td align="center"><a href="https://github.com/dyun23"><img src="https://avatars.githubusercontent.com/dyun23" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/minjko"><img src="https://avatars.githubusercontent.com/minjko" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/BackToTheDev"><img src="https://avatars.githubusercontent.com/BackToTheDev" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/jinyoung0718"><img src="https://avatars.githubusercontent.com/jinyoung0718" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/banasu0723"><img src="https://avatars.githubusercontent.com/banasu0723" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/owzl"><img src="https://avatars.githubusercontent.com/owzl" width="150px;" alt=""></td>
  </tr>
  <tr>
    <td align="center">🐥<a href="https://github.com/dyun23"><b>김다윤</b></td>
    <td align="center">🦊<a href="https://github.com/minjko"><b>고민정</b></td>
    <td align="center">😼<a href="https://github.com/BackToTheDev"><b>권준성</b></td>
    <td align="center">🐰<a href="https://github.com/jinyoung0718"><b>소진영</b></td>
    <td align="center">🐻<a href="https://github.com/banasu0723"><b>이승언</b></td>
    <td align="center">🐻<a href="https://github.com/owzl"><b>정유리</b></td>
  </tr>
  </table>
<br>


### 목차
- [🛠 기술 스택](#-기술-스택)
- [🎁 FastOne 서비스 소개](#-fastone-서비스-소개)
- [📈 프로젝트 설계](#-프로젝트-설계)
  <br><br>


## 🛠 기술 스택
#### &nbsp;　[ Backend ]
&nbsp;&nbsp;&nbsp;&nbsp;
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Spring data jpa-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white" style="border-radius: 5px;">

#### &nbsp;　[ DB ]
&nbsp;&nbsp;&nbsp;
<img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white" style="border-radius: 5px;">

#### &nbsp;　[ Infra ]
&nbsp;&nbsp;&nbsp;
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=AWS%20EC2&logoColor=white" style="border-radius: 5px;"> 

#### &nbsp;　[ Communication ]
&nbsp;&nbsp;&nbsp;&nbsp;
<img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white" style="border-radius: 5px;">
<br><br><br>

## 🎨 FastOne 서비스 소개
<img width="1280" height="320" alt="f1_logo_blue" src="https://github.com/user-attachments/assets/fe9e5183-a38d-4406-99d3-03739d1110e6" />

### 프로젝트 배경
- FastOne은 '배달의 민족', '요기요'와 같은 기존 음식 주문 플랫폼의 편리함은 그대로 유지하되, 사용자들이 겪었던 가장 큰 불편함인 여러 가게 음식을 동시에 주문하지 못하는 문제를 해결하기 위해 개발되었습니다.
- 저희는 이 문제를 해결하고 차별점을 갖기 위해 하나의 장바구니에 여러 가게의 음식을 담을 수 있는 기능을 기획했습니다.
- 이제 FastOne을 통해 더욱 빠르고 편리하며 스마트한 주문 경험을 만나보세요.

### 🍴 세부 기능
- **`회원 및 인증`**<br>
  일반 사용자와 점주 회원을 구분하여 회원가입 및 로그인 기능을 제공합니다.<br>
  Spring Security와 JWT 기반의 인증 방식을 사용해 안전한 접근 제어를 구현했습니다.

- **`가게 관리`**<br>
  점주는 자신의 가게 정보를 등록·수정할 수 있으며,<br>
  카테고리, 영업시간, 주소 등 세부 정보를 포함해 체계적으로 관리할 수 있습니다.

- **`메뉴 관리`**<br>
  가게별로 메뉴를 등록하고 옵션, 가격 등을 설정할 수 있습니다.<br>
  메뉴 수정 시 자동으로 변경 이력을 관리할 수 있도록 설계했습니다.

- **`장바구니`**<br>
  사용자는 원하는 메뉴를 장바구니에 담고 수량을 변경하거나 삭제할 수 있습니다.<br>
  Redis를 이용해 임시 데이터를 저장함으로써 빠른 응답 속도와 세션 유지성을 보장했습니다.

- **`주문`**<br>
  사용자는 장바구니에 담은 메뉴를 선택해 주문을 진행할 수 있으며,<br>
  주문 상태(접수·조리 중·배달 중·완료)를 실시간으로 확인할 수 있습니다.<br>
  주문 내역은 사용자와 점주 모두가 확인할 수 있도록 관리됩니다.

- **`리뷰`**<br>
  주문 완료 후 사용자는 리뷰를 작성할 수 있고,<br>
  별점과 리뷰 내용을 기반으로 가게 평점이 자동으로 갱신됩니다.<br>
  가게 평점은 별도의 테이블로 관리되어 조회 시 성능을 최적화했습니다.

- **`AI 연동`**<br>
  OpenAI API를 연동하여 해당 가게의 최근 리뷰 내용들의 요약을 제공합니다. <br>
AI를 이용하여 메뉴 설명을 자연스러운 추천 문구를 제공합니다.
  <br><br><br>


## 📈 프로젝트 설계
<details>
<summary><h3>1. ERD</h3></summary>
<img src="https://github.com/user-attachments/assets/742d8497-454a-4be6-bf91-fd9d9278fddd">
</details>
<details>
<summary><h3>2. 시스템 아키텍처</h3></summary>
<img src="https://github.com/user-attachments/assets/60c205b0-8b32-4cf0-83ac-e7a509489c3f" />
</details>

###  [3. API 명세서](http://www.fast1.kro.kr:8080/swagger-ui/index.html)
