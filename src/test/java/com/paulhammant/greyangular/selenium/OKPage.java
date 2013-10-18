package com.paulhammant.greyangular.selenium;

import org.openqa.selenium.WebDriver;

public class OKPage extends BaseFluentSeleniumPage {

    public OKPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void verifyOnPage() {
        title().shouldBe("OK");
    }
}
