package org.lzx.gerrit.service;

import com.google.common.net.HttpHeaders;
import com.google.gerrit.extensions.auth.oauth.OAuthServiceProvider;
import com.google.gerrit.extensions.auth.oauth.OAuthToken;
import com.google.gerrit.extensions.auth.oauth.OAuthUserInfo;
import com.google.gerrit.extensions.auth.oauth.OAuthVerifier;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.http.HttpStatus;
import org.jsoup.HttpStatusException;
import org.lzx.gerrit.model.Provider;
import org.lzx.gerrit.model.Registration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * @author LZx
 * @since 2022/7/27
 */
@Singleton
public class GerritOAuthServiceProvider implements OAuthServiceProvider {

    HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
            .build();

    private Provider provider;

    private Registration registration;

    @Inject
    public GerritOAuthServiceProvider(Provider provider,
                                      Registration registration) {
        this.provider = provider;
        this.registration = registration;
    }

    @Override
    public String getAuthorizationUrl() {
        return provider.getAuthorizeEndpointUrl() + "?client_id=" + registration.getClientId() +
                "&response_type=code&redirect_uri=" + registration.getRedirectUri() +
                "&scope=" + String.join(" ", registration.getScopes());
    }

    @Override
    public OAuthToken getAccessToken(OAuthVerifier verifier) {
        String tokenEndpointUrl = provider.getTokenEndpointUrl();
        tokenEndpointUrl = Objects.requireNonNull(tokenEndpointUrl);
        URI tokenEndpointUri = URI.create(tokenEndpointUrl);

        String authorizeCode = verifier.getValue();
        String clientId = registration.getClientId();
        String clientSecret = registration.getClientSecret();
        String scopes = String.join(" ", registration.getScopes());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(null)
                .uri(tokenEndpointUri)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new HttpStatusException("请求用户端点失败", response.statusCode(), tokenEndpointUrl);
            }
            JsonElement jsonElement = JsonParser.parseString(response.body());
            String accessToken = jsonElement.getAsJsonObject().get("access_token").toString();
            String name = jsonElement.getAsJsonObject().get("name").toString();
            String email = jsonElement.getAsJsonObject().get("email").toString();
            return new OAuthToken(accessToken, );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(OAuthToken token) throws IOException {
        String userInfoApiUrl = provider.getUserInfoApiUrl();
        userInfoApiUrl = Objects.requireNonNull(userInfoApiUrl);
        URI userInfoUri = URI.create(userInfoApiUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, token.getToken())
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new HttpStatusException("请求用户端点失败", response.statusCode(), userInfoApiUrl);
            }
            JsonElement jsonElement = JsonParser.parseString(response.body());
            String sub = jsonElement.getAsJsonObject().get("sub").toString();
            String name = jsonElement.getAsJsonObject().get("name").toString();
            String email = jsonElement.getAsJsonObject().get("email").toString();
            return new OAuthUserInfo(null, sub, email, name, null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getVersion() {
        return PluginConstants.PLUGIN_VERSION;
    }

    @Override
    public String getName() {
        return PluginConstants.PLUGIN_NAME;
    }

}
