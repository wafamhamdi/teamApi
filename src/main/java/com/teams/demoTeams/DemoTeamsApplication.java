package com.teams.demoTeams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = TeamsController.class)
public class DemoTeamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoTeamsApplication.class, args);
	}

}
