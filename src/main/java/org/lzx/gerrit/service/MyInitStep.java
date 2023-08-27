package org.lzx.gerrit.service;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.InitStep;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LZx
 * @since 2022/8/1
 */
@Slf4j
public class MyInitStep implements InitStep {

    private final String pluginName;

    @Inject
    public MyInitStep(@PluginName String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public void run() throws Exception {
        log.info("插件[{}]开始启动", pluginName);
    }

    @Override
    public void postRun() throws Exception {
        log.info("插件[{}]启动完毕", pluginName);
    }

}
