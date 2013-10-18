package com.paulhammant.greyangular.selenium;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.seleniumhq.selenium.fluent.FluentWebDriver;
import org.seleniumhq.selenium.fluent.monitors.CompositeMonitor;
import org.seleniumhq.selenium.fluent.monitors.HighlightOnError;
import org.seleniumhq.selenium.fluent.monitors.ScreenShotOnError;

public class BaseFluentSeleniumPage extends FluentWebDriver {

    public BaseFluentSeleniumPage(WebDriver webDriver, String url) {
        this(webDriver);
        webDriver.get(url);
    }

    public BaseFluentSeleniumPage(WebDriver webDriver) {
        super(webDriver,
                new CompositeMonitor(
                        new HighlightOnError(webDriver),
                        new ScreenShotOnError.WithUnitTestFrameWorkContext((TakesScreenshot) webDriver, BaseFluentSeleniumPage.class, "test-classes", "surefire-reports")));
    }
}