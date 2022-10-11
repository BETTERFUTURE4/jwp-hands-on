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

- [ ] 2단계 - Propagation
