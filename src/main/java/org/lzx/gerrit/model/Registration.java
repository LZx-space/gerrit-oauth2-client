package org.lzx.gerrit.model;

import lombok.Data;

import java.util.*;

/**
 * @author LZx
 * @since 2022/7/27
 */
@Data
public class Registration {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private final Set<String> scopes = new HashSet<>();

    public Registration(String clientId, String clientSecret, String redirectUri, List<String> scopes) {
        this.clientId = Objects.requireNonNull(clientId, "clientId不能为空");
        this.clientSecret = Objects.requireNonNull(clientSecret, "clientSecret不能为空");
        this.redirectUri = Objects.requireNonNull(redirectUri, "redirectUri不能为空");
        this.scopes.addAll(scopes);
    }

}
