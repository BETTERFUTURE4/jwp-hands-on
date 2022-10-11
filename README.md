# JWP Hands-On

## 만들면서 배우는 스프링 실습 코드

### 학습 순서

- cache
- thread
- servlet
- reflection
- di

### 요구사항

#### cache

- [x] 0단계 - 휴리스틱 캐싱 제거하기
- [x] 1단계 - HTTP Compression 설정하기
- [x] 2단계 - ETag/If-None-Match 적용하기
- [x] 3단계 - 캐시 무효화(Cache Busting)

#### thread

- [x] 0단계 - 스레드 이해하기
- [x] 1단계 - 1단계 - 동시성 이슈 확인하기
- [x] 2단계 - 2단계 - WAS에 스레드 설정하기

#### servlet

- [x] 1단계 - 서블릿 학습 테스트
- [x] 2단계 - 필터 학습 테스트

#### reflection

- [x] Junit3TestRunner 클래스의 모든 테스트를 통과시킨다.
- [x] Junit4TestRunner 클래스의 모든 테스트를 통과시킨다.
- [x] ReflectionTest 클래스의 모든 테스트를 통과시킨다.
- [x] ReflectionsTest 클래스의 모든 테스트를 통과시킨다.

#### DI

- [x] Stage3Test 클래스의 모든 테스트를 통과시킨다.
- [x] Stage4Test 클래스의 모든 테스트를 통과시킨다.

#### connection pool

- [x] 0단계 - DataSource 다루기
- [x] 1단계 - 커넥션 풀링
- [x] 2단계 - HikariCP 설정하기

#### Transaction

- [x] 0단계 - ACID

원자성(`Atomicity`) :
트랜잭션이 **완전히 성공하거나 완전히 실패**하는 단일 단위로 처리되어야 함을 의미한다.

일관성(`Consistency`): 각 데이터 트랜잭션이 데이터베이스를 **일관성 있는 상태에서 일관성 있는 상태로 이동해야 함**을 의미한다. 즉 트랜잭션이 성공적으로 완료하면 **언제나 동일한 데이터베이스 상태로
유지**하는 것을 의미한다.

독립성(`Isolation`): 트랜잭션을 수행 시 **다른 트랜잭션의 연산 작업이 끼어들지 못하도록 보장**하는 것을 의미한다. 여러 트랜잭션이 동시에 발생하는 경우 최종 상태는 트랜잭션이 개별적으로 발생한 것과
같아야 한다. 즉, 데이터베이스는 **스트레스 테스트를 통과**해야 한다. 과부하로 인해 잘못된 데이터베이스 트랜잭션이 발생하지 않아야 한다.

지속성(`Durability`) : **성공적으로 수행된 트랜잭션은 영원히 반영(기록)되어야 함**을 의미한다. 트랜잭션은 로그에 모든 것이 저장된 후에만 commit 상태로 간주될 수 있다. **데이터베이스 내의
데이터는 트랜잭션의 결과로만 변경**되어야 하며 외부 영향에 의해 변경될 수 없어야 한다. 예를 들어 소프트웨어 업데이트로 인해 데이터가 실수로 변경되거나 삭제되지 않아야 한다.

- [x] 1단계 - Isolation

Dirty Reads : 다른 트랜잭션의 **커밋하지 않은 변경사항**을 읽는다.

Non-repeatable reads : 다른 트랜잭션으로 인한, **커밋을 통해 DB에 변경(`update`)된 행의 변화**를 트랜잭션 도중에 읽는다. 따라서 한 트랜잭션에서 같은 조회 쿼리를 날렸을 때 결과가
달라질 수 있다.

Phantom reads : 다른 트랜잭션으로 인한, **커밋을 통해 DB에 `delete`/`insert` 된 행의 변화**를 트랜잭션 도중에 읽는다. 따라서 한 트랜잭션에서 같은 조회 쿼리를 날렸을 때 결과가
달라질 수 있다.

- MySQL의 경우 **현재 트랜잭션에서 업데이트된 행에 대해서만 팬텀리드가 발생**할 수 있다.

| Read phenomena   | Dirty reads | Non-repeatable reads | Phantom reads  |
|------------------|-------------|----------------------|----------------|
| Read Uncommitted | +           | +                    | +              |
| Read Committed   | -           | +                    | +              |
| Repeatable Read  | -           | -                    | +(MySQL은 변경 후) |
| Serializable     | -           | -                    | -              |

- [x] 2단계 - Propagation

### Required

생성된 트랜잭션이 몇 개인가? : 1개

왜 그런 결과가 나왔을까? : Required 는 기존 트랜잭션(부모 트랜잭션)이 있을 시 합류한다.

### Required New

생성된 트랜잭션이 몇 개인가? : 2개

왜 그런 결과가 나왔을까?

- RequiredNew 는 부모 트랜잭션과 관계없는 새 트랜잭션을 생성한다.
- 두 트랜잭션은 별개로 처리된다.

### REQUIRES_NEW 일 때 예외로 인한 롤백

- 부모 트랜잭션 시작 -> 자식 트랜잭션 세이브 후 커밋 -> 부모 트랜잭션 예외 터짐

RequiredNew 에서 자식(second) 트랜잭션도 롤백되는가?

- 롤백되지 않는다.
- 별개의 트랜잭션 이므로!

### Supports

주석일때(기존 트랜잭션 없을 때) : 트랜잭션 없이 처리 (saveSecondTransactionWithSupports)

주석 아닐 때(기존 트랜잭션 존재) : 기존 트랜잭션에 합류 (saveFirstTransactionWithSupports)

### Mandatory

주석일때(기존 트랜잭션 없을 때) : 
- 예외 터짐
- IllegalTransactionStateException: No existing transaction found for transaction marked with
propagation 'mandatory'

주석 아닐 때(기존 트랜잭션 존재) : 기존 트랜잭션에 합류 (saveFirstTransactionWithSupports)

### Not Supported

주석일 때(기존 트랜잭션 없을 때) : 트랜잭션 없이 간다. first, second 트랜잭션은 active 되어있지 않다.

주석 아닐 때(기존 트랜잭션 존재) : first 트랜잭션은 존재하되, second는 트랜잭션 없이 간다. 기존 트랜잭션은 second 메소드 실행 전까지 보류된다. second 트랜잭션은 active 되어있지 않다.

### Nested

주석 없을 땐 새 트랜잭션, 없을 땐 중첩 트랜잭션을 예상했다.

- 실제로는 NestedTransactionNotSupportedException 가 뜬다. JPA 에서는 Nested 를 지원하지 않는 듯...
- DB가 SAVEPOINT 기능을 지원해야 사용이 가능(Oracle)하다.

주석 있을 땐(기존 트랜잭션 없을 때)

- saveSecondTransactionWithNested 만 존재.
- 즉 새 트랜잭션을 만든다.

### Never

기존 트랜잭션 있을 때 : 
- 예외 처리. 
- Existing transaction found for transaction marked with propagation 'never'

새 트랜잭션일 때(주석) : 
- 트랜잭션 없이 간다. second 트랜잭션은 active 되어있지 않다.
