# Practice - Embeddable Password
Spring JPA에서 password를 embeddable class로 따로 분리해 효율적으로 관리하는 방법을 학습하기 위한 코드입니다.

---
# 서론
최근 spring-boot와 kotlin을 공부하고 있는데, spring jpa와 관련한 best practice를 알려주는 좋은 글을 찾아서 직접 해볼 만한 것들을 실습해보기로 했다. 이번에 진행한 실습의 내용 및 학습 목표는 아래와 같다.
### 내용
- 사용자 로그인 기능을 구현하되, 아래와 같은 세부 사항을 따른다.
	+ 아이디가 잘못되어 로그인이 실패한 경우와 비밀번호가 틀려 로그인이 실패한 경우를 구분하여 오류를 출력한다.
    + 비밀번호가 틀려 로그인이 실패한 경우, 로그인 실패 횟수를 response에 담아 함께 반환한다.
    + 로그인 실패 횟수가 5회 이상이 될 시 그에 맞는 또다른 오류를 출력한다.
    + 로그인 성공 시, 실패 횟수는 초기화된다.
    + 위의 모든 구현 사항은 클라이언트가 아닌 서버에서 이루어져야 하며, 클라이언트가 응답을 쉽게 받아볼 수 있도록 오류 응답의 형식을 통일한다.
    
### 학습목표
- 안전한 data transfer를 위한 Dto 클래스 사용
- @Embedded / @Embeddable을 이용한 Password 클래스 분리
- password domain과 관련된 business logic 및 validation 처리는 Password 클래스에게 모두 위임
- 각 상황에 맞는 적절한 custom exception 생성
- @ExceptionHandler을 이용한 exception handling
- error response 형식의 통일을 위한 객체 생성

# 코드
우선 data transfer에 사용될 accountDto 클래스를 살펴보자.

`AccountDto.kt`
```kotlin
class AccountDto {
    data class SignUpReq(
            val username: String,
            val rawPassword: String
    ) {
        fun toEntity(bCryptPasswordEncoder: BCryptPasswordEncoder): User {
            return User(
                    username = username,
                    password = Password(value = bCryptPasswordEncoder.encode(rawPassword)),
                    roles = mutableSetOf(Role.USER)
            )
        }
    }

    data class SignInReq(
            val username: String,
            val rawPassword: String
    )
}
```
signup과 sigin에서 쓰일 dto가 있고, signupReq에는 이를 user 객체로 mapping해주는 toEntity 함수를 선언했다. 다음으로 User model을 살펴보자.

`User.kt`
```kotlin
@Entity
data class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(name="username", unique = true, length = 200)
    var username: String,

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    var roles: MutableSet<Role>,

    @Embedded
    var password: Password
)
```
실습이므로 간단한 정보들만 담고 있도록 작성했다.

`Password.kt`
```kotlin
@Embeddable
data class Password(
        var value: String
) {
    private var expirationDate = LocalDateTime.now().plusDays(14)
    private var failedCount = 0

    fun updateFailedCount(matches: Boolean) {
        failedCount = if(matches) 0 else failedCount + 1

        if(matches) extendExpirationDate()
        if(failedCount >= 5) throw PasswordFailedExceededException()
    }

    fun changePassword(newPassword: String, oldPassword: String, bCryptPasswordEncoder: BCryptPasswordEncoder) {
        value = bCryptPasswordEncoder.encode(newPassword)
        extendExpirationDate()
    }

    private fun extendExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusDays(14)
    }

    fun getFailedCount() = failedCount

    fun getExpirationDate() = expirationDate
}
```
원래는 비밀번호 매칭 등의 인증 관련 로직까지 이 클래스에 작성할 생각이었는데, spring security의 도움을 받고 있으므로 인증 관련 로직은 security에게 모두 맡기기로 했다. (이미 존재하는 기능들은 잘 써먹는게 좋지 않을까?ㅎㅎ) 여기서는 로그인이 성공/실패했을 때 상황에 맞게 실패 횟수를 변경해주는 로직과, 비밀번호 변경에 관한 로직을 담고 있다.

다음으로, 통일된 형식의 error response를 위해 ErrorResponse라는 클래스를 만들었다.
`ErrorResponse.kt`
```kotlin
data class ErrorResponse(
        val message: String,
        val code: String,
        val status: Int,
        var details: List<ErrorDetail> = listOf()
) {
    class ErrorDetail(
            val message: String
    )

    companion object {
        fun of(errorCode: ErrorCode, details: List<ErrorDetail>? = null): ErrorResponse {
            val errorResponse = ErrorResponse(
                    message = errorCode.message,
                    code = errorCode.code,
                    status = errorCode.status
            )

            details?.let{ errorResponse.details = it }

            return errorResponse
        }
    }
}
```
이름만 그럴듯하지, 내용은 굉장히 단순하다.. 그리고 이 ErrorResponse의 내용을 채워줄 ErrorCode라는 클래스도 따로 분리하여 작성했다.

`ErrorCode.kt`
```kotlin
enum class ErrorCode(
        val code: String,
        val message: String,
        val status: Int
) {
    ACCOUNT_NOT_FOUND("AC_001", "Cannot find such user.", 404),
    EMAIL_DUPLICATION("AC_002", "Duplicated Email.", 400),
    INPUT_VALUE_INVALID("CM_001", "Input value is invalid.", 400),
    PASSWORD_FAILED_COUNT_EXCEEDED("PW_001", "password failed count was exceeded.", 400),
    AUTHENTICATION_FAILED("AC_003", "authentication failed.", 400)
}
```
ErrorCode는 빈번하게 재사용이 가능하도록 enum class로 작성했다. custome exception의 수가 늘어날 수록 이 enum class의 내용이 늘어날텐데, 이 방법이 장기적으로 좋은 방법인지는.. 잘 모르겠다.

그리고 controller 단에서 exception이 발생했을 때 이를 가로채서 적절히 handling해 줄 클래스를 만들어야한다. 이 클래스는 @ControllerAdvice의 도움을 받는다.

```kotlin
@ControllerAdvice
class ErrorExceptionController {
  ...
}
```

이 클래스 안에 각 exception들을 핸들링할 함수를 작성하면 된다.
```kotlin
    @ExceptionHandler(value = [(PasswordNotMatchedException::class)])
    fun handle(e: PasswordNotMatchedException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.AUTHENTICATION_FAILED
        val details = listOf(ErrorResponse.ErrorDetail("current fail count : ${e.failedCount}"))

        val errorResponse = ErrorResponse.of(errorCode, details)

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
```

이렇게 하면 exception이 발생했을 때, 클라이언트가 알아보기 쉽고 통일된 형식으로 error response를 전달해줄 수 있다.

# 시뮬레이션
exception이 발생했을 때, spring에서 기본적으로 제공하는 error response의 형식은 아래와 같다.

![image.png](https://images.velog.io/post-images/dvmflstm/57e1b0a0-1b00-11ea-9543-4d8bc0f0294f/image.png)

이러한 형식의 error response는 굉장히 알아보기 힘들고, error message 또한 정확하지 않으므로 클라이언트에서 이를 그대로 활용할 수가 없다. 반면 @ExceptionHandler를 이용해 exception을 적절히 핸들링하면 아래와 같은 response를 얻을 수 있다.

![image.png](https://images.velog.io/post-images/dvmflstm/a3882520-1b00-11ea-b4c8-d5ec753f7f37/image.png)

클라이언트 측과 이 response의 형식에 대해 협의가 되었다면 서비스적인 측면에서 error를 핸들링하기가 훨씬 쉬워질 것이다.

이제 username은 올바르게 넣었는데, 비밀번호가 틀릴 경우의 error response를 살펴보자.

![image.png](https://images.velog.io/post-images/dvmflstm/f3cd9d30-1b00-11ea-9543-4d8bc0f0294f/image.png)

역시 통일된 형식으로 error response를 반환하고, details 필드에 현재 실패 횟수를 담아 넘겨준다. 계속해서 로그인 시도가 실패해 실패횟수가 5회 이상이 되면 아래와 같은 response를 반환한다.

![image.png](https://images.velog.io/post-images/dvmflstm/18ea7fc0-1b01-11ea-b3b9-719ee8e47390/image.png)

의도한 대로 response가 잘 날라온다.

# 정리
ErrorResponse 클래스를 생성하고, ExceptionHandler를 이용해 효과적인 에러 핸들링을 할 수가 있었다. 통일된 response 형식을 갖고 있기 때문에 협업 시에 개발 생산성이 크게 향상될 수 있을 것 같고, 각각의 상황에 정확한 메세지를 담고 있는 error를 리턴함으로써 서비스 관점에서도 완결성이 높아질 것으로 기대할 수 있을 것 같다. 그리고 embeddable / embedded annotation을 사용함으로써 domain layer 내의 기능별 세분화를 확실히 할 수 있었고, password와 관련된 business logic 및 validation 처리를 모두 Password 클래스에게 위임함으로써 유지보수 용이성이 높아진 것 같다.

## reference
https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-04.md