package org.kzt18829d;

import org.kzt18829d.service.EnvironmentConfiguratorService;
import org.kzt18829d.service.EnvironmentReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        EnvironmentReadService readService = EnvironmentReadService.getInstance();
        EnvironmentConfiguratorService configuratorService = EnvironmentConfiguratorService.getInstance();

        try {
            readService.addFile("settings.env").load();
            configuratorService.setConfiguration(readService.getAll());
            configuratorService.configBankAccountNumberGenerator();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}