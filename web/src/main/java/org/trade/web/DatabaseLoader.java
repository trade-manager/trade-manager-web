package org.trade.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final EmployeeRepository employees;
	private final UserRepository users;

	private final static String ROLE_MANAGER = "ROLE_MANAGER";

	@Autowired
	public DatabaseLoader(EmployeeRepository employeeRepository,
						  UserRepository userRepository) {

		this.employees = employeeRepository;
		this.users = userRepository;
	}


	public void run(String... strings) throws Exception {

		User greg = this.users.save(new User("admin", "admin",
				ROLE_MANAGER));
		User oliver = this.users.save(new User("oliver", "gierke",
				ROLE_MANAGER));

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("admin", "doesn't matter",
				AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

		this.employees.save(new Employee("Frodo", "Baggins", "ring bearer", greg));
		this.employees.save(new Employee("Bilbo", "Baggins", "burglar", greg));
		this.employees.save(new Employee("Gandalf", "the Grey", "wizard", greg));

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
				AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

		this.employees.save(new Employee("Samwise", "Gamgee", "gardener", oliver));
		this.employees.save(new Employee("Merry", "Brandybuck", "pony rider", oliver));
		this.employees.save(new Employee("Peregrin", "Took", "pipe smoker", oliver));

		SecurityContextHolder.clearContext();

	}
}
