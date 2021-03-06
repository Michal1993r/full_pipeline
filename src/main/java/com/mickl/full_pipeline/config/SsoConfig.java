package com.mickl.full_pipeline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.extensions.saml2.config.SAMLConfigurer.saml;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SsoConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.saml2.metadata-url}")
    private String metadataUrl;

    @Value("${server.ssl.key-alias}")
    private String keyAlias;

    @Value("${server.ssl.key-store-password}")
    private String password;

    @Value("${server.port}")
    private String port;

    @Value("${server.ssl.key-store}")
    private String keyStoreFilePath;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .requiresChannel()
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure()
                .and()
                .authorizeRequests()
                .antMatchers("/saml*").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(saml())
                .serviceProvider()
                .keyStore()
                .storeFilePath(keyStoreFilePath)
                .password(password)
                .keyname(keyAlias)
                .keyPassword(password)
                .and()
                .protocol("https")
                .hostname(String.format("%s:%s", "full-pipeline.herokuapp.com", port))
                .basePath("/")
                .and()
                .identityProvider()
                .discoveryEnabled(true)
                .metadataFilePath(metadataUrl);
    }
}
