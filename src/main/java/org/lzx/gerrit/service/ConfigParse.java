package org.lzx.gerrit.service;

import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import org.eclipse.jgit.lib.Config;
import org.lzx.gerrit.model.Provider;
import org.lzx.gerrit.model.Registration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LZx
 * @since 2022/7/27
 */
public class ConfigParse {

    private static final String GERRIT_OAUTH2_CONFIG_SECTION_NAME = "gerrit_oauth2";

    private final Map<String, Provider> providers = new HashMap<>();

    private final Map<String, Registration> registrations = new HashMap<>();

    @Inject
    public ConfigParse(PluginConfigFactory cfg) {
        Config pluginConfig = cfg.getGlobalPluginConfig(GERRIT_OAUTH2_CONFIG_SECTION_NAME);
        String[] allowedProviders = pluginConfig.getStringList("gerrit_oauth2_allowed_providers", null, "");
        Arrays.stream(allowedProviders).forEach(allowedProvider -> {
            Provider provider = provider(pluginConfig, allowedProvider);
            providers.put(allowedProvider, provider);
            Registration registration = registration(pluginConfig, allowedProvider);
            registrations.put(allowedProvider, registration);
        });
    }

    private Provider provider(Config pluginConfig, String provider) {
        return pluginConfig.get(config -> {
            String authorizeEndpointUrl = config.getString("provider", provider, "authorizeEndpointUrl");
            String tokenEndpointUrl = config.getString("provider", provider, "tokenEndpointUrl");
            String userInfoApiUrl = config.getString("provider", provider, "userInfoApiUrl");
            return new Provider(authorizeEndpointUrl, tokenEndpointUrl, userInfoApiUrl);
        });

    }

    private Registration registration(Config pluginConfig, String provider) {
        return pluginConfig.get(config -> {
            String clientId = config.getString("provider", provider, "clientId");
            String clientSecret = config.getString("provider", provider, "clientSecret");
            String redirectUri = config.getString("provider", provider, "redirectUri");
            String[] scopes = config.getStringList("provider", provider, "scope");
            return new Registration(clientId, clientSecret, redirectUri, Arrays.stream(scopes).collect(Collectors.toList()));
        });
    }

}
