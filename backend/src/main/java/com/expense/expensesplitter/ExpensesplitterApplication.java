package com.expense.expensesplitter;

import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.model.User.Role;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ExpensesplitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensesplitterApplication.class, args);
	}

	@Bean
	CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				User user1 = new User();
				user1.setName("John Doe");
				user1.setEmail("john@example.com");
				user1.setUsername("john");
				user1.setPassword(passwordEncoder.encode("password"));
				user1.setPhone("1234567890");
				user1.setIsActive(true);
				user1.setRole(Role.USER);
				userRepository.save(user1);

				User user2 = new User();
				user2.setName("Jane Smith");
				user2.setEmail("jane@example.com");
				user2.setUsername("jane");
				user2.setPassword(passwordEncoder.encode("password"));
				user2.setPhone("9876543210");
				user2.setIsActive(true);
				user2.setRole(Role.USER);
				userRepository.save(user2);

				System.out.println("Created default users: john, jane");
			}
		};
	}
}