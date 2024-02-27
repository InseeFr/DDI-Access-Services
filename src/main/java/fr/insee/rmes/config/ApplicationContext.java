package fr.insee.rmes.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.net.ssl.SSLContext;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContext {

	@Value("${fr.insee.rmes.api.host}")
	private String serverHost;

	@Value("${fr.insee.ntlm.user}")
	private String ntlmUser;

	@Value("${fr.insee.ntlm.password}")
	private String ntlmPassword;

	@Value("${fr.insee.ntlm.domain}")
	private String ntlmDomain;
	
	@Value("#{'${fr.insee.rmes.search.root.sub-group.ids}'.split(',')}")
	private List<String> subGroupIds;
	
	@Value("#{'${fr.insee.rmes.search.root.resource-package.ids}'.split(',')}")
	private List<String> ressourcePackageIds;

	@Autowired
	private final Environment environment;

	public ApplicationContext(Environment environment) {
		this.environment = environment;
	}
	@Bean
	public HttpClientBuilder httpClientBuilder()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider> create()
				.register(AuthSchemes.NTLM, new NTLMSchemeFactory()).build();
		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(ntlmUser, ntlmPassword, null, ntlmDomain));
		return HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLSocketFactory(sslsf)
				.useSystemProperties().setDefaultAuthSchemeRegistry(authSchemeRegistry)
				.setDefaultCredentialsProvider(credsProvider);
	}
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {

		return builder.build();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		String serverUrl = "http://";
		if (isProductionProfileActive()) {
			serverUrl = "https://";
		}

		return new OpenAPI().servers(List.of(
				new Server().url(serverUrl + serverHost).description("Server")));
	}

	private boolean isProductionProfileActive() {
		String[] activeProfiles = environment.getActiveProfiles();
		for (String profile : activeProfiles) {
			if (profile.equalsIgnoreCase("prod") || profile.equalsIgnoreCase("production")) {
				return true;
			}
		}
		return false;
	}

}
