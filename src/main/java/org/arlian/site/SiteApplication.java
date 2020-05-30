package org.arlian.site;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SiteApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SiteApplication.class);
		springApplication.setAdditionalProfiles(getProfiles());
		springApplication.run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.profiles(getProfiles());
		return application.sources(SiteApplication.class);
	}

	private static String[] getProfiles() {
		List<String> profiles = new ArrayList<>();

		String hostName = "";
		try{
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if(hostName.contains("host.arlian.solutions")){
			profiles.add("SERVER");
			profiles.add("DEV");
		}
		else {
			profiles.add("LOCAL");
			profiles.add("DEV");
		}

		String[] profileStringArray = profiles.toArray(new String[0]);

		for (String profile:profiles) {
			LoggerFactory.getLogger(SiteApplication.class).info("Profile '"+profile+"' added on hostname '"+hostName+"'.");
		}

		return profileStringArray;
	}

}
