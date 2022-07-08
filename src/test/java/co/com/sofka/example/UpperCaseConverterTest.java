package co.com.sofka.example;

import co.com.sofka.example.classes.UpperCaseConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@SpringBootTest(classes = UpperCaseConverter.class)
class UpperCaseConverterTest {
    final TestPublisher<String> testPublisher = TestPublisher.create();

    /**
     * Publisher
     *
     * next(T value) or next(T value, T rest) - sends one or more signals to subscribers
     * emit(T Value) - same than next (T) but invokes complete() at the end
     * complete() - ends the source with the complete signal
     * error(Throwable tr) - terminates a source with an error
     * flux() - method for wrapping a TestPublisher in Flux
     * mono() - the same as flux() but wrapping it in Monkey
     */
    @Test
    void testError() {
        UpperCaseConverter uppercaseConverter = new UpperCaseConverter(testPublisher.flux());
        StepVerifier.create(uppercaseConverter.getUpperCase())
                .then(() -> testPublisher.next("First", "Second", "Third")
                                            .error(new RuntimeException("Message")))
                .expectNext("FIRST", "SECOND", "THIRD")
                .expectError(RuntimeException.class);
    }

    /**
     * In addition to ALLOW_NULL we can configure some other typical behaviors that would cause errors.
     * REQUEST_OVERFLOW – allows you to call next() without launching an IllegalStateException when there is an insufficient number of requests.
     * CLEANUP_ON_TERMINATE – allows multiple termination signals to be sent consecutively.
     * DEFER_CANCELLATION – allow us to ignore the cancellation signals and continue with the emission of elements
     */
    @Test
    void testUpperCase() {
        UpperCaseConverter uppercaseConverter = new UpperCaseConverter(testPublisher.flux());
        StepVerifier.create(uppercaseConverter.getUpperCase())
                .then(() -> testPublisher.emit("datos", "GeNeRaDoS", "Sofka"))
                .expectNext("DATOS", "GENERADOS", "SOFKA")
                .verifyComplete();
    }

    @Test
    void testNull() {
        UpperCaseConverter uppercaseConverter = new UpperCaseConverter(testPublisher.flux());
        StepVerifier.create(uppercaseConverter.getUpperCase())
                .then(() -> TestPublisher.createNoncompliant(TestPublisher.Violation.ALLOW_NULL)
                                            .emit("1", "2", null, "3"))
                .expectNext("1", "2", "3")
                .expectError(NullPointerException.class);
    }
}
