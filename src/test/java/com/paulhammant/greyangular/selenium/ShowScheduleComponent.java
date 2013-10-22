package com.paulhammant.greyangular.selenium;

import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.selenium.fluent.TestableString;

import static org.seleniumhq.selenium.fluent.FluentBy.attribute;

public class ShowScheduleComponent extends BaseFluentSeleniumPage {

    private AngularModelAccessor ngModel;

    private WebElement controllerElem;

    public ShowScheduleComponent(String url, WebDriver webDriver, AngularModelAccessor ngModel) {
        super(webDriver, url);
        this.ngModel = ngModel;
        controllerElem = div(attribute("ng-controller", "MyController")).getWebElement();
    }

    @Override
    public void verifyOnPage() {
        tbody().tr(attribute("data-ng-repeat", "stop in choice.schedule"));
    }

    public ShowScheduleComponent(String url, WebDriver webDriver) {
        super(webDriver, url);
    }

    public TestableString noResultsText() {
        return div(attribute("data-ng-show", "choice.schedule.Valid == false")).getText();
    }

    public TestableString tableText() {
        return tbody().getText();
    }
}
