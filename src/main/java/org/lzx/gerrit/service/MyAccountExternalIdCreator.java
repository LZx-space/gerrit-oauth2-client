package org.lzx.gerrit.service;

import com.google.gerrit.entities.Account;
import com.google.gerrit.server.account.AccountExternalIdCreator;
import com.google.gerrit.server.account.externalids.ExternalId;

import java.util.Collections;
import java.util.List;

/**
 * @author LZx
 * @since 2022/8/1
 */
public class MyAccountExternalIdCreator implements AccountExternalIdCreator {

    @Override
    public List<ExternalId> create(Account.Id id, String username, String email) {
        return Collections.emptyList();
    }

}
