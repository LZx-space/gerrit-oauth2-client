package org.lzx.gerrit.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LZx
 * @since 2022/7/27
 */
@Data
@AllArgsConstructor
public class Provider {

    private String authorizeEndpointUrl;

    private String tokenEndpointUrl;

    private String userInfoApiUrl;

}
