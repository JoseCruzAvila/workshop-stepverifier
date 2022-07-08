package co.com.sofka.example.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class FirstService {
    public Mono<String> searchOne() {
        return Mono.just("Pedro");
    }

    public Flux<String> searchAll() {
        return Flux.just("Pedro", "Maria", "Jesus", "Carmen");
    }

    public Flux<String> searchAllSlow() {
        return Flux.just("Pedro", "Maria", "Jesus", "Carmen").delaySequence(Duration.ofSeconds(20));
    }

    public Flux<String> searchAllfilter() {
        Flux<String> source = Flux.just("John", "Monica", "Mark", "Cloe", "Frank", "Casper", "Olivia", "Emily", "Cate")
                .filter(name -> name.length() == 4)
                .map(String::toUpperCase);

        return source.concatWith(
                Mono.error(new IllegalArgumentException("Error Message"))
        );
    }

    public Flux<Integer> emmitManyElements() {
        return Flux.<Integer>create(emitter -> {
            emitter.next(1);
            emitter.next(2);
            emitter.next(3);
            emitter.complete();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            emitter.next(4);
        }).filter(number -> number % 2 == 0);
    }
}
