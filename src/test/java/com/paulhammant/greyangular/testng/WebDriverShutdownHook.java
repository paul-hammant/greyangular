package com.paulhammant.greyangular.testng;

import com.google.inject.Inject;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Guice;

@Guice(moduleFactory = OurModuleFactory.class)
public class WebDriverShutdownHook {

    @Inject
    private WebDriver webDriver;

    @AfterGroups(groups = "ui")
    public void shutdown() {
        webDriver.quit();
    }
}
