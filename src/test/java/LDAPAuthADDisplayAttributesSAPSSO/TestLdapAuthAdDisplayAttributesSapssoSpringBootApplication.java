package LDAPAuthADDisplayAttributesSAPSSO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestLdapAuthAdDisplayAttributesSapssoSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.from(LdapAuthAdDisplayAttributesSapssoSpringBootApplication::main).with(TestLdapAuthAdDisplayAttributesSapssoSpringBootApplication.class).run(args);
	}

}
