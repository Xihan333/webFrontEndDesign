package org.fatmansoft.teach;

import lombok.extern.slf4j.Slf4j;
import org.fatmansoft.teach.service.SystemService;
import org.fatmansoft.teach.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SystemApplicationListener 系统应用实践处理程序
 */
@Component
@Order(0)
@Slf4j
public class SystemApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private SystemService systemService;  //系统服务对象自动注入
    @Autowired
    private TestService testService;

    /**
     * 系统实践处理方法 系统启动后自动加载数据字典
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("SystemInitStart");
        systemService.initDictionary();
        systemService.initSystem();
        log.info("systemInitEnd");
    }

}