package ma.fst.projet_full_stack_2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjetFullStack2Application {

    public static void main(String[] args) {
        SpringApplication.run(ProjetFullStack2Application.class, args);
    }
    @Bean
    CommandLineRunner testPasswordEncoder(PasswordEncoder passwordEncoder){
        return args -> {
            System.out.println("hash admin123 = " + passwordEncoder.encode("admin123"));
        };
    }

}
