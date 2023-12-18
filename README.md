# ZB_technical-challenges (제로베이스 개인 프로젝트)
<br>   

> 23.10.16 ~
>    
> 매장 예약 서비스 프로젝트

<br><br>

## 서비스 흐름도
![image](https://github.com/JoHyungJun/ZB_technical-challenges/assets/56953934/33c7271b-f1d8-470a-a65d-4ef61266c7d0)
<br><br>


## 프로젝트 기능 & 설계

- ***Member***
    - 회원 정보 조회
        - 회원의 primary key로 공개 가능한 정보를 조회할 수 있다.
            - 조회 되는 정보는 이용자 pk, 아이디, 권한, 아이디의 상태 정보이다.
            
    - 회원 등록 (가입)
        - 아이디, 비밀번호, 이름, 핸드폰 번호, 권한을 입력 받는다.
            - 아이디와 핸드폰 번호는 unique 해야 한다.
            - 비밀 번호와 핸드폰 번호는 형식을 지켜야 한다.
            - 권한은 USER(일반 유저), STORE_OWNER(매장 점주)로 나뉜다.
            
    - 로그인
        - 아이디, 비밀번호를 입력 받는다.
        - 로그인이 성공적으로 완료되면 JWT의 토큰, 리프레시 토큰을 발급 받으며, 이후 로그인이 필요한 서비스는 토큰을 통해 이용한다.
        
    - 로그아웃
        - 로그아웃을 진행한다.
            - DB에 저장된 해당 회원의 리프레시 토큰을 삭제한다.
            
    - 회원 정보 수정
        - 이용자가 로그인에 사용한 정보 모두를 수정할 수 있다.
            - 요청되는 정보들은 로그인 시의 규칙을 지켜야 한다.
            
    - 회원 탈퇴
        - 회원 아이디의 상태 정보를 수정한다. Soft Delete로 진행된다.

***

- ***Store***
    - 매장 정보 조회
        - 매장의 primary key로 정보를 조회할 수 있다.
            - 조회 되는 정보는 매장 pk, 이름, 위도, 경도, 매장 소개, 매장 상태 정보, 오픈 시간, 종료 시간, 예약 텀, 예약 불가능 요일, 별점이다.
        - 매장의 이름으로 정보를 조회할 수 있다.
            - 조회 되는 정보는 ‘매장 정보 조회’와 동일하다.
            
    - 매장 전체 정보 조회
        - 매장의 전체 정보를 조회할 수 있다.
            - 정렬이 가능하며, 이름/별점/거리 순으로 조회할 수 있다.
            - 조회 되는 개별 매장의 정보는 ‘매장 정보 조회’와 동일하다. 추가로 거리 순 조회의 경우 현재 이용자의 위도, 경도 값을 받아 매장과 이용자의 거리 차이를 추가로 보여준다.
            - paging으로 응답한다.
            
    - 매장 등록
        - 이용자가 자신의 매장을 등록할 수 있다. 매장 이름, 위도, 경도, 설명, 상태 정보, 오픈 시간, 종료 시간, 예약 텀을 입력 받는다.
            - 이용자의 권한이 STORE_OWNER(점주)인 경우에만 허용된다.
            - 예약 텀은 30분 이상으로만 설정할 수 있다. 이는 30분 간격으로 예약 등록이 가능함을 의미한다.
            
    - 매장 정보 수정
        - 이용자가 매장 등록에 사용한 정보 모두를 수정할 수 있다.
            - 요청되는 정보들은 매장 등록 시의 규칙을 지켜야 한다.
        - default로 설정된 하나의 시간 텀에 가능한 예약 테이블 수(1), 테이블에 앉을 수 있는 사람 수(4)를 수정할 수 있다.
        - 특정 날짜, 혹은 특정 요일의 예약을 막도록 수정할 수 있다.
        
    - 매장 삭제
        - 매장의 상태 정보를 수정한다. Soft Delete로 진행된다.
    
***

- ***Reservation***
    - 예약 정보 조회
        - 예약의 primary key로 정보를 조회할 수 있다.
            - 조회 되는 정보는 예약 pk, 예약된 매장 pk, 예약한 이용자 pk, 예약 날짜, 예약 명 수, 예약 메뉴, 상태 정보, 이용자가 해당 예약의 방문 여부 상태 정보이다.
            
    - 특정 조건의 예약 전체 정보 조회
        - 매장의 primary key로 해당 매장 예약의 전체 정보를 조회할 수 있다.
        - 이용자의 token으로 해당 이용자 예약의 전체 정보를 조회할 수 있다.
        - paging으로 응답한다.
        
    - 예약 가능 여부 조회
        - 특정 매장, 특정 시간의 예약 가능 여부를 조회한다.
            - 예약 가능 여부는 해당 매장 점주가 설정한 오픈 시간, 종료 시간, 예약 텀, 예약이 가득 차 있는지에 따라 결정된다.
            
    - 예약 등록
        - 이용자가 특정 매장, 특정 시간, 특정 메뉴에 예약을 등록할 수 있다.
            - 예약 가능 여부에 따라 결정된다.
            - 메뉴 정보는 선택적으로 등록한다.
            
    - 예약 수정
        - 이용자가 예약 등록에 사용한 정보 모두를 수정할 수 있다.
            - 요청되는 정보들은 예약 등록 시의 규칙 및 검증을 지켜야 한다.
            
    - 예약 삭제 (취소)
        - 예약의 상태 정보를 수정한다. Soft Delete로 진행된다.
        
    - 예약 허용
        - 점주 이용자의 경우, 자신의 매장에 등록된 특정 예약을 승인하거나 거절할 수 있다.

***

- ***Review***
    - 리뷰 정보 조회
        - 리뷰의 primary key로 정보를 조회할 수 있다.
            - 조회 되는 정보는 리뷰 pk, 리뷰를 작성한 이용자 pk, 리뷰가 작성된 매장 pk, 리뷰에 등록한 별점, 리뷰 메세지이다.
            
    - 특정 조건의 리뷰 전체 정보 조회
        - 이용자의 primary key로 해당 이용자가 작성한 리뷰의 전체 정보를 조회할 수 있다.
        - 매장의 primary key로 해당 매장에 작성된 리뷰의 전체 정보를 조회할 수 있다.
            - 별점 순으로 조회할 수 있다.
        - 조회 되는 개별 리뷰의 정보는 ‘리뷰 정보 조회’와 동일하다.
        - paging으로 응답한다.
        
    - 리뷰 등록
        - 이용자가 특정 매장의 리뷰를 등록할 수 있다. 매장 pk, 별점, 리뷰 메세지, 사진을 입력 받는다.
            - 해당 이용자가 해당 매장을 예약 했는지, 방문했는지 여부에 따라 결정된다.
            - 방문 이후 일주일 이내에 (다음 주의 동일 요일까지) 리뷰 작성 및 수정이 가능하다.
            
    - 리뷰 수정
        - 이용자가 리뷰 등록에 사용된 정보 중 리뷰 메세지, 사진 정보를 수정할 수 있다.
        
    - 리뷰 삭제
        - 리뷰의 상태 정보를 수정한다. Soft Delete로 진행된다.

***

- ***Menu***
    - 메뉴 정보 조회
        - 메뉴의 primary key로 정보를 조회할 수 있다.
            - 조회 되는 정보는 메뉴 pk, 해당 메뉴를 등록한 매장 pk, 메뉴 사진, 가격이다.
            
    - 특정 조건의 메뉴 전체 조회
        - 매장의 primary key로 해당 매장에 등록된 메뉴의 전체 정보를 조회할 수 있다.
            - 조회 되는 개별 메뉴의 정보는 ‘메뉴 정보 조회’와 동일하다.
            
    - 메뉴 등록
        - 이용자가 자신의 매장의 메뉴를 등록할 수 있다. 매장 pk, 메뉴 사진, 가격을 입력 받는다.
            - 이용자의 권한이 STORE_OWNER(점주)인 경우에만 허용된다.
            
    - 메뉴 수정
        - 이용자가 리뷰 등록에 사용된 정보 중 메뉴 사진, 가격 정보를 수정할 수 있다.
        
    - 메뉴 삭제
        - 메뉴의 상태 정보를 수정한다. Soft Delete로 진행된다.

***

- ***Kiosk***
    - 방문 등록
        - 이용자의 핸드폰 번호, 매장의 pk, 예약 시간으로 해당 예약의 방문 상태 정보를 수정할 수 있다.
        - 이용자의 아이디, 패스워드, 매장의 pk, 예약 시간으로 해당 예약의 방문 상태 정보를 수정할 수 있다.
        - 매장 날짜는 api를 호출한 현재 날짜를 기준으로 하며, 요청 정보의 형식 검증 및 점주 승인 여부, 방문 여부에 따라 결정된다.

<br><br><br>


## ERD
[ERD 링크](https://www.erdcloud.com/d/pWpvQqD8dbnJDFGde)
<br><br><br>

`참고 사진`
<br><br>
![image](https://github.com/JoHyungJun/ZB_technical-challenges/assets/56953934/3c39b75b-6444-4d36-9ce9-0916cf872f24)
<br><br><br>


## API 명세
[API 명세 링크](https://proud-thief-ae8.notion.site/ZB-API-7d9ec887c2434dd497abafcde539fd63?pvs=4)
<br><br><br>

`참고 사진`
<br><br>
![image](https://github.com/JoHyungJun/ZB_technical-challenges/assets/56953934/d50990db-5fd4-4c78-8d19-c9c4625fddea)
<br><br><br>
![image](https://github.com/JoHyungJun/ZB_technical-challenges/assets/56953934/435cbb6c-d0d6-459a-97d1-2c780474d00f)
