package helloWorld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
    @GetMapping({"/hello/{name}", "/hello/", "/"})
    public String helloWorld(@PathVariable(required = false) String name) {
        if (name == null) {
            return "Hello World";
        } else {
            return "Hello " + name;
        }
    }
}