package transaction.stage2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 트랜잭션 전파(Transaction Propagation)란? 트랜잭션의 경계에서 이미 진행 중인 트랜잭션이 있을 때 또는 없을 때 어떻게 동작할 것인가를 결정하는 방식을 말한다.
 * <p>
 * FirstUserService 클래스의 메서드를 실행할 때 첫 번째 트랜잭션이 생성된다. SecondUserService 클래스의 메서드를 실행할 때 두 번째 트랜잭션이 어떻게 되는지 관찰해보자.
 * <p>
 * https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#tx-propagation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Stage2Test {

    private static final Logger log = LoggerFactory.getLogger(Stage2Test.class);

    @Autowired
    private FirstUserService firstUserService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /**
     * 생성된 트랜잭션이 몇 개인가? 1개
     * <p>
     * 왜 그런 결과가 나왔을까? Required 는 기존 트랜잭션(부모 트랜잭션)이 있을 시 합류한다.
     */
    @Test
    void testRequired() {
        final var actual = firstUserService.saveFirstTransactionWithRequired();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithRequired");
    }

    /**
     * 생성된 트랜잭션이 몇 개인가? 2개
     * <p>
     * 왜 그런 결과가 나왔을까? : RequiredNew 는 부모 트랜잭션과 관계없는 새 트랜잭션을 생성한다. 두 트랜잭션은 별개로 처리된다.
     */
    @Test
    void testRequiredNew() {
        final var actual = firstUserService.saveFirstTransactionWithRequiredNew();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(2)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithRequiresNew",
                        "transaction.stage2.FirstUserService.saveFirstTransactionWithRequiredNew");
    }

    /**
     * firstUserService.saveAndExceptionWithRequiredNew()에서 강제로 예외를 발생시킨다.
     * <p>
     * REQUIRES_NEW 일 때 예외로 인한 롤백이 발생하면서 어떤 상황이 발생하는지 확인해보자.
     */

//    부모 트랜잭션 시작 - 자식 트랜잭션 세이브 후 커밋 - 부모 트랜잭션 예외 터짐
//    -> RequiredNew 에서 자식 트랜잭션 세이브도 롤백되는가? : 롤백되지 않는다. 별개의 트랜잭션 이므로...
    @Test
    void testRequiredNewWithRollback() {
        assertThat(firstUserService.findAll()).hasSize(0);

        assertThatThrownBy(() -> firstUserService.saveAndExceptionWithRequiredNew())
                .isInstanceOf(RuntimeException.class);

        assertThat(firstUserService.findAll()).hasSize(1);
    }

    /**
     * FirstUserService.saveFirstTransactionWithSupports() 메서드를 보면 @Transactional 이 주석으로 되어 있다.
     * <p>
     * 주석인 상태에서 테스트를 실행했을 때와 주석을 해제하고 테스트를 실행했을 때 어떤 차이점이 있는지 확인해보자.
     */

    // 주석일때(기존 트랜잭션 없을 때) : 트랜잭션 없이 처리 (saveSecondTransactionWithSupports)
    // 주석 아닐 때(기존 트랜잭션 존재) : 기존 트랜잭션에 합류 (saveFirstTransactionWithSupports)
    @Test
    void testSupports() {
        final var actual = firstUserService.saveFirstTransactionWithSupports();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithSupports");
    }

    /**
     * FirstUserService.saveFirstTransactionWithMandatory() 메서드를 보면 @Transactional 이 주석으로 되어 있다.
     * <p>
     * 주석인 상태에서 테스트를 실행했을 때와 주석을 해제하고 테스트를 실행했을 때 어떤 차이점이 있는지 확인해보자.
     * <p>
     * SUPPORTS 와 어떤 점이 다른지도 같이 챙겨보자.
     */

    // 주석일때(기존 트랜잭션 없을 때) : 예외 터짐(IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory')
    // 주석 아닐 때(기존 트랜잭션 존재) : 기존 트랜잭션에 합류 (saveFirstTransactionWithSupports)
    @Test
    void testMandatory() {
        final var actual = firstUserService.saveFirstTransactionWithMandatory();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithMandatory");
    }

    /**
     * 아래 테스트는 몇 개의 물리적 트랜잭션이 동작할까?
     * <p>
     * FirstUserService.saveFirstTransactionWithNotSupported() 메서드의 @Transactional 을 주석처리하자.
     * <p>
     * 다시 테스트를 실행하면 몇 개의 물리적 트랜잭션이 동작할까?
     * <p>
     * 스프링 공식 문서에서 물리적 트랜잭션과 논리적 트랜잭션의 차이점이 무엇인지 찾아보자.
     */

    // 주석일 때(기존 트랜잭션 없을 때) : 트랜잭션 없음?-> 실제론 second 트랜잭션이 있다??
    // 주석 아닐 때(기존 트랜잭션 존재) : 기존 트랜잭션 보류 후 트랜잭션 없이 실행? ->  실제론 두 트랜잭션 모두 존재한다?? 실제론 second 트랜잭션은 Active가 아니다.
    @Test
    void testNotSupported() {
        final var actual = firstUserService.saveFirstTransactionWithNotSupported();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(2)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNotSupported",
                        "transaction.stage2.FirstUserService.saveFirstTransactionWithNotSupported");
    }

    /**
     * 아래 테스트는 왜 실패할까?
     * <p>
     * FirstUserService.saveFirstTransactionWithNested() 메서드의 @Transactional 을 주석 처리하면 어떻게 될까?
     */

    // 중첩 트랜잭션
    // 주석 없을 땐 새 트랜잭션, 없을 땐 중첩 트랜잭션을 예상했다.
    // - 실제로는 NestedTransactionNotSupportedException 가 뜬다. JPA 에서는 Nested 를 지원하지 않는 듯...
    // - DB가 SAVEPOINT 기능을 지원해야 사용이 가능(Oracle)하다.
    // 주석 있을 땐(기존 트랜잭션 없을 때) saveSecondTransactionWithNested 만 존재. 즉 새 트랜잭션을 만든다.

    @Test
    void testNested() {
        final var actual = firstUserService.saveFirstTransactionWithNested();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNested");
    }

    /**
     * 마찬가지로 @Transactional 을 주석처리하면서 관찰해보자.
     */

    // 기존 트랜잭션 있을 때 : 예외 처리. Existing transaction found for transaction marked with propagation 'never'
    // 새 트랜잭션일 때(주석) : 트랜잭션 없이 간다. second 트랜잭션은 active 되어있지 않다.
    @Test
    void testNever() {
        final var actual = firstUserService.saveFirstTransactionWithNever();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNever");
    }
}
