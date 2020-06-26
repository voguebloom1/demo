demo

/api/v1/svc/moneys

- Token은 대화방 단위로 최대 357,940개 생성 가능.
- 7일 지난 뒤 삭제 로직은 미구현 (구현 필요) -> Batch 로직으로 추가(미구현)
- 기본적으로 UserId, RoomId가 잘못되면 Error


1. 뿌리기 API
  - 0원, 0명으로 요청할 수 없음.
  - Token은 3자리 문자열로 대소문자, 소문자, 숫자, 특수문자 조합으로 생성.
  - 인원수에 맞게 최대 100% ~ 0%를 분배한다.
  - Token 발급.
  
2. 받기 API
  - 자신이 속한 대화방에서만 받기 가능.
  - 자신이 뿌리기한 건은 자신이 받을 수 없음.
  - 10분간 유효
  - 중복 받기 불가능
  - 받기가 종료되면 종료 메세지 전달.
  
3. 조회 API
  - 토큰과 룸 정보가 정상이어야 함.
  - 생성한 유저가 아니라면 조회 불가능.
  - 7일내 조회가능.