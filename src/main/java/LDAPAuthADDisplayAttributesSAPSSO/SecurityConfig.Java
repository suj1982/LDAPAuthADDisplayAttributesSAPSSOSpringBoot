import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.CachingMetadataManagerFactory;
import org.springframework.security.saml.metadata.CachingMetadataManagerFactoryBean;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.provider.service.config.SamlServiceProviderSecurityConfigurer;
import org.springframework.security.saml.provider.service.config.SamlServiceProviderServerBeanConfiguration;
import org.springframework.security.saml.provider.service.config.SamlServiceProviderSecurityConfigurerAdapter;
import org.springframework.security.saml.provider.service.config.SamlServiceProviderServerBeanRelyingPartyConfiguration;
import org.springframework.security.saml.saml2.metadata.Saml2MetadataResolver;
import org.springframework.security.saml.saml2.metadata.Saml2MetadataResolverAdapter;
import org.springframework.security.saml.saml2.metadata.Saml2MetadataResolverFactory;
import org.springframework.security.saml.saml2.metadata.Saml2MetadataResolverFactoryBean;
import org.springframework.security.saml.web.SamlController;
import org.springframework.security.saml.web.SamlFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/user").authenticated()
                .anyRequest().permitAll()
                .and()
            .apply(saml())
                .sso()
                    .defaultSuccessURL("/user")
                    .and()
                .authenticationProvider(samlAuthenticationProvider())
                    .userDetailsService(samlUserDetailsService())
                    .and()
                .successHandler(samlSuccessHandler());
    }

    @Bean
    public Filter samlFilter() {
        return new SamlFilter(saml());
    }

    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        return new SAMLAuthenticationProvider();
    }

    @Bean
    public SAMLUserDetailsService samlUserDetailsService() {
        return new SAMLUserDetailsServiceImpl(ldapTemplate);
    }

    @Bean
    public SAMLAuthenticationSuccessHandler samlSuccessHandler() {
        return new SAMLAuthenticationSuccessHandler();
    }

    private SamlServiceProviderSecurityConfigurer saml() {
        return new SamlServiceProviderSecurityConfigurerAdapter() {
            @Override
            protected void configure(SamlServiceProviderServerBeanConfiguration.SamlServiceProviderServerBeanDsl serviceProvider) throws Exception {
                serviceProvider
                    .hostedSp(serviceProviderRelyingParty());
            }
        };
    }

    private SamlServiceProviderServerBeanRelyingPartyConfiguration serviceProviderRelyingParty() {
        return new SamlServiceProviderServerBeanRelyingPartyConfiguration()
            .signMetadata(true)
            .verifyMetadataSignature(true)
            .metadata(metadataManager());
    }

    private MetadataManager metadataManager() throws Exception {
        return new CachingMetadataManager(metadataResolvers());
    }

    private List<Saml2MetadataResolver> metadataResolvers() throws Exception {
        List<Saml2MetadataResolver> resolvers = new ArrayList<>();
        resolvers.add(ssoCircleMetadataResolver());
        // Add more metadata resolvers if needed
        return resolvers;
    }

    private Saml2MetadataResolver ssoCircleMetadataResolver() throws Exception {
        Saml2MetadataResolverFactoryBean resolver = new Saml2MetadataResolverFactoryBean();
        resolver.setParserPool(parserPool());
        resolver.setHttpClient(httpClient());
        resolver.setMetadataURL("https://idp.ssocircle.com");
        return new Saml2MetadataResolverAdapter(resolver.getObject());
    }

    private HttpClient httpClient() {
        return new HttpClient();
    }

    private ParserPool parserPool() {
        return new ParserPool();
    }
}