package co.com.sofka.example.classes;

import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

public class UpperCaseConverter {
    private final Flux<String> source;

    public UpperCaseConverter(@Autowired(required = false)  Flux<String> source) {
        this.source = source;
    }

    public Flux<String> getUpperCase() {
        return source.map(String::toUpperCase);
    }
}
