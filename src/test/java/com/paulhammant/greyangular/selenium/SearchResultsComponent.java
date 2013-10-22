package com.paulhammant.greyangular.selenium;

import com.paulhammant.ngwebdriver.AngularModelAccessor;
import com.paulhammant.ngwebdriver.ByAngular;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.selenium.fluent.FluentWebElement;
import org.seleniumhq.selenium.fluent.FluentWebElements;
import org.seleniumhq.selenium.fluent.TestableString;

import static org.seleniumhq.selenium.fluent.FluentBy.attribute;
import static org.seleniumhq.selenium.fluent.Period.secs;

public class SearchResultsComponent extends BaseFluentSeleniumPage {

    private AngularModelAccessor ngModel;

    private WebElement controllerElem;
    private ByAngular byAngular;

    public SearchResultsComponent(String url, WebDriver webDriver, AngularModelAccessor ngModel) {
        super(webDriver, url);
        this.ngModel = ngModel;
        controllerElem = div(attribute("ng-controller", "MyController")).getWebElement();
        byAngular = new ByAngular((JavascriptExecutor) delegate);
    }

    @Override
    public void verifyOnPage() {
        within(secs(2)).form(attribute("name", "srForm"));
    }

    public SearchResultsComponent(String url, WebDriver webDriver) {
        super(webDriver, url);
    }

    public TestableString tableText() {
        return tbody().getText();
    }

    public String getSelection() {
        return ngModel.retrieveJson(controllerElem, "selection");
    }

    public FluentWebElements radioButtons() {
        return tbody().inputs();
    }

    public FluentWebElements checkedRadioButtons() {
        return tbody().inputs(attribute("checked", "checked"));
    }

    public void hasReturnedToCriteria() {
        url().shouldContain("/SearchCriteria.html");
    }

    public TestableString noResultsText() {
        return tr(attribute("data-ng-show", "searchResponse.Valid == false")).getText();
    }

    public long getPercentage() {
        return ngModel.retrieveAsLong(controllerElem, "searchResponsePercent");
    }

    public String getPercentageJson() {
        return ngModel.retrieveAsString(controllerElem, "searchResponsePercent");
    }

    public FluentWebElement radioButtonWhenAvailable() {
        return tr(byAngular.repeater("choice in searchResponse.SchedulesDepart")).within(secs(10)).input(attribute("type", "radio"));
    }
}
