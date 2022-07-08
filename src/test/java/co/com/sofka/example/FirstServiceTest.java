package co.com.sofka.example;

import co.com.sofka.example.services.FirstService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest(classes = FirstService.class)
class FirstServiceTest {
    @Autowired
    FirstService service;

    // First example with TestVerified

    @Test
    void testMono() {
        Mono<String> one = service.searchOne();
        StepVerifier.create(one)
                .expectNext("Pedro")
                .verifyComplete();
    }

    @Test
    void TestMany() {
        Flux<String> many = service.searchAll();
        StepVerifier.create(many)
                .expectNext("Pedro")
                .expectNext("Maria")
                .expectNext("Jesus")
                .expectNext("Carmen")
                .verifyComplete();
    }

    @Test
    void testManySlow() {
        Flux<String> many = service.searchAllSlow();
        StepVerifier.create(many)
                .expectNext("Pedro")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Maria")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Jesus")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Carmen")
                .thenAwait(Duration.ofSeconds(1))
                .verifyComplete();
    }

    /*@Test
    void testAllFilter() {
        Flux<String> source = service.searchAllfilter();
        StepVerifier.create(source)
                .expectNext("JOHN")
                .expectNextMatches(name -> name.startsWith("MA"))
                .expectNext("CLOE", "CATE")
                .expectComplete()
                .verify();
    }*/

    // Exceptions

    /**
     * If it is not necessary to check the type and message of the exception at once, we can use one of the dedicated methods:
     *
     * expectError() - expect any kind of error
     * expectError(Class<? extends Throwable> class) - expects an error of a specific type
     * expectErrorMessage(String errorMessage) - an error with a specific message is expected
     * expectErrorMatches(Predicate<Throwable> predicate) - an error that matches a given predicate is expected
     * expectErrorSatisfies(Consumer<Throwable> assertionConsumer) - consumes a Throwable to make a custom assertion
     */
    @Test
    void testAllFilter() {
        Flux<String> source = service.searchAllfilter();
        StepVerifier.create(source)
                .expectNextCount(4)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Error Message")
                ).verify();
    }

    // Publishers based in time

    /**
     * There are two main methods of expectation that deal with time:
     *
     * thenAwait(Duration duration) - stops the evaluation of steps; new events may occur during this time
     * expectNoEvent(Duration duration) - fails when an event appears during the duration; the sequence will pass with a certain duration
     */
    @Test
    void testBasedTime() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(2))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .expectNext(0L)
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(1L)
                .verifyComplete();
    }

    // Assertions after execution

    @Test
    void testAfterExecution() {
        Flux<Integer> source = service.emmitManyElements();

        StepVerifier.create(source)
                .expectNext(2)
                .expectComplete()
                .verifyThenAssertThat()
                .hasDropped(4)
                .tookLessThan(Duration.ofMillis(1050));
    }
}
