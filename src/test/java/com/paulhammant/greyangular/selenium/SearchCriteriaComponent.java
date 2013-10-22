package com.paulhammant.greyangular.selenium;

import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.selenium.fluent.FluentWebElement;
import org.seleniumhq.selenium.fluent.TestableString;

import static org.openqa.selenium.By.id;
import static org.seleniumhq.selenium.fluent.FluentBy.attribute;
import static org.seleniumhq.selenium.fluent.FluentBy.notAttribute;

public class SearchCriteriaComponent extends BaseFluentSeleniumPage {

    private final AngularModelAccessor ngModel;
    private final WebElement controllerElem;

    public SearchCriteriaComponent(String url, WebDriver webDriver, AngularModelAccessor ngModel) {
        super(webDriver, url);
        this.ngModel = ngModel;
        controllerElem = div(attribute("ng-controller", "MyController")).getWebElement();
    }

    @Override
    public void verifyOnPage() {
        div(id("originRow"));
    }

    public TestableString destinationErrText() {
        return span(attribute("ng-show","errs.destination")).getText();
    }

    public TestableString originErrText() {
        return span(attribute("ng-show","errs.origin")).getText();
    }

    public FluentWebElement submitButton() {
        return button(attribute("type", "submit"));
    }

    public FluentWebElement destinationField() {
        return div(id("destRow")).input();
    }

    public FluentWebElement originField() {
        return div(id("originRow")).input();
    }

    public FluentWebElement selectFirstOriginOffered() {
        return div(id("originRow")).li().link().click();
    }

    public FluentWebElement selectFirstDestinationOffered() {
        return div(id("destRow")).li().link().click();
    }


    public BaseFluentSeleniumPage clickSubmitButton() {
        submitButton().click();
        return new OKPage(delegate);
    }

    public String getDate() {
        return ngModel.retrieveJson(controllerElem, "search.when").replace("\"", "");
    }

    public void setWhen(String value) {
        ngModel.mutate(controllerElem, "search.when", value);
    }

    public String getSearch() {
        return ngModel.retrieveJson(controllerElem, "search").replace("\"", "'");
    }

    public FluentWebElement dateField() {
        return input(id("date"));
    }

    public FluentWebElement tomorrow() {
        return tbody().buttons(notAttribute("disabled")).get(1);
    }

    public FluentWebElement hourIncrementor() {
        return div(id("time")).link(attribute("ng-click", "incrementHours()"));
    }
}
