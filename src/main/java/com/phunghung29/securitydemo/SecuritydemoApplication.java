package com.phunghung29.securitydemo;

import com.phunghung29.securitydemo.entity.Role;
import com.phunghung29.securitydemo.entity.User;
import com.phunghung29.securitydemo.repository.RoleRepository;
import com.phunghung29.securitydemo.repository.UserRepository;
import com.phunghung29.securitydemo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SecuritydemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuritydemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(RoleRepository roleRepo, UserRepository userRepository) {
		return args -> {
			Role admin = new Role(1L, "admin");
			Role customer = new Role(2L, "customer");
			roleRepo.save(admin);
			roleRepo.save(customer);

			userRepository.save(new User(1L, "email1@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 18, "Male", true, admin));
			userRepository.save(new User(2L, "email2@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 20, "Female", true, admin));
			userRepository.save(new User(3L, "email3@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 17, "Unknown", true, customer));
			userRepository.save(new User(4L, "email4@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 21, "Female", false, customer));
			userRepository.save(new User(5L, "email5@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 16, "Male", false, customer));
			userRepository.save(new User(6L, "email6@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 25, "Male", true, customer));
			userRepository.save(new User(7L, "email7@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 25, "Male", true, customer));
			userRepository.save(new User(8L, "emailBstar8@gmail.com", "$2y$10$WR/cKrRYTaoJhyeE.OK5T.4qCMv1NVm4k1AQ4PmahqKsvLf4j.rea", 25, "Male", false, customer));
		};
	}
}
