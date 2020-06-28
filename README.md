# 뿌리기 기능 구현



## 0. API 공통

##### Header
<pre><code>
"X-USER-ID": Long
"X-ROOM-ID : String
</code></pre>

##### 공통에러
<table>
<tr>
    <td>에러코드</td><td>Status</td><td>설명</td>
</tr>
<tr>
    <td>401</td><td>Unauthorized</td><td>Invalid UserId or RoomId</td>
</tr>
</table>



## 1. 뿌리기 API
##### Method & URL
<pre><code>
Method : POST
URL : /api/v1/svc/moneys
Request: 

</code></pre>

##### Request 
<table>
<tr><th>key</th><th>type</th></tr>
<tr><td>money</td><td>long</td></tr>
<tr><td>people</td><td>int</td></tr>
</table>
<pre><code>
{
    "money" : 10000,
    "people" : 5
}
</code></pre>

##### Response
<table>
<tr><th>key</th><th>type</th></tr>
<tr><td>token</td><td>string</td></tr>
</table>
<pre><code>
{
    "success": true,
    "result": {
        "token": "k0O"
    }
}
</code></pre>

##### 공통에러
<table>
<tr>
    <th>에러코드</th><th>Status</th><th>설명</th>
</tr>
<tr>
    <td>400</td><td>Invalid Parameters</td><td>요청한 Body가 잘못된 정보</td>
</tr>
<tr>
    <td>500</td><td>Create Token Fail</td><td>토큰 생설 실패</td>
</tr>

</table>

1. 0원 또는 0명으로 요청할 수 없음. 
2. Token은 2자리 문자열로 대소문자, 소문자, 숫자, 특수문자 총 71개 조합으로 생성.
3. Token은 대화방 당 최대 34만개 생성 가능.
   (중복 발생시 재생성 로직이 있으나 최대 100개 까지만 반복.)
   (문제 발생시 Error 응답)
4. 뿌리기 돈은 인원수에 맞게 100% ~ 0% 사이에 랜덤으로 분배.


## 2. 받기 API
##### Method & URL
<pre><code>
Method : GET
URL : /api/v1/svc/moneys/{token}/receive
</code></pre>

##### Response
<pre><code>
{
    "success" : true,
    "result": {}
}  
</code></pre>

##### 공통에러
<table>
<tr>
    <th>에러코드</th><th>Status</th><th>설명</th>
</tr>
<tr>
    <td>400</td><td>Duplicate User</td><td>받은 사용자가 요청</td>
</tr>
<tr>
    <td>400</td><td>Create User</td><td>생성한 사용자가 요청</td>
</tr>
<tr>
    <td>400</td><td>Invalid RoomId</td><td>해당 토큰이 없는 Room으로 요청</td>
</tr>
<tr>
    <td>400</td><td>Expired Date</td><td>뿌리기 받기 10분 경과 후 요청</td>
</tr>
</table>

  - 대화방에 속한 토큰으료 요청해야 뿌리기 머니를 받을 수 있음.
  - 생성한 사용자는 요청할 수 없음.
  - 10분간 유효
  - 중복 받기 불가능
  
## 3. 조회 API

##### Method & URL
<pre><code>
Method : GET
URL : /api/v1/svc/moneys/{token}/status
</code></pre>

##### Response
<table>
<tr><th>key</th><th>type</th></tr>
<tr><td>startDate</td><td>Date</td></tr>
<tr><td>spreadMoney</td><td>long</td></tr>
<tr><td>receivedMoney</td><td>long</td></tr>
<tr><td>receivedList</td><td>List</td></tr>
<tr><td> - money</td><td>long</td></tr>
<tr><td> - userId</td><td>long</td></tr>
</table>
<pre><code>
Response:
{
    "success": true,
    "result": {
        "spreadMoneyInfo": {
            "startDate": "2020-06-28T13:34:12.338+00:00",
            "spreadMoney": 1421,
            "receivedMoney": 70,
            "receivedList": [
                {
                    "money": 70,
                    "userId": 8
                }
            ]
        }
    }
}
</code></pre>

##### 공통에러
<table>
<tr>
    <th>에러코드</th><th>Status</th><th>설명</th>
</tr>
<tr>
    <td>400</td><td>Invalid Token</td><td>잘못된 토큰 정보</td>
</tr>
<tr>
    <td>401</td><td>Invalid User</td><td>권한 없는 사용자가 요청</td>
</tr>
<tr>
    <td>400</td><td>Expired Date</td><td>7일 지난 뒤 조회 요청</td>
</tr>
</table>

  - Toekn 과 Room 정보가 정상이여야 함.
  - 생성한 유저가 아니라면 조회 불가능.
  - 7일내 조회가능.
