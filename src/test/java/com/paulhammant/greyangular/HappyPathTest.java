package com.paulhammant.greyangular;

import com.google.inject.Inject;
import com.paulhammant.greyangular.selenium.BaseFluentSeleniumPage;
import com.paulhammant.greyangular.selenium.SearchCriteriaComponent;
import com.paulhammant.greyangular.selenium.SearchResultsComponent;
import com.paulhammant.greyangular.testng.OurModuleFactory;
import com.paulhammant.greyangular.testng.TestNumber;
import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Run via the maven-failsafe-plugin
 */
@Guice(moduleFactory = OurModuleFactory.class)
public class HappyPathTest {

    @Inject
    private WebDriver webDriver;

    @Inject
    private AngularModelAccessor ngModel;

    @Inject
    private TestNumber testNumber;

    @Test(groups = "fullStack")
    public void search_for_a_seat_works_brand_1() throws InterruptedException {
        search_for_a_seat_works(1);
    }

    @Test(groups = "fullStack")
    public void search_for_a_seat_works_brand_2() throws InterruptedException {
        search_for_a_seat_works(2);
    }

    private void search_for_a_seat_works(final int i) throws InterruptedException {

        SearchCriteriaComponent searchCriteria = new SearchCriteriaComponent("http://localhost:8080/index_" + i + ".html", webDriver, ngModel) {
            @Override
            public BaseFluentSeleniumPage clickSubmitButton() {
                super.clickSubmitButton();
                return new MySearchResultsComponent(webDriver, ngModel);
            }
        };

        searchCriteria.originField().sendKeys("Chicago");
        searchCriteria.selectFirstOriginOffered();

        searchCriteria.destinationField().sendKeys("Detroit");
        searchCriteria.selectFirstDestinationOffered();

        searchCriteria.dateField().click();

        SearchResultsComponent searchResults = (SearchResultsComponent) searchCriteria.clickSubmitButton();

        String selection = searchResults.getSelection();
        searchResults.radioButtonWhenAvailable().click();
        assertThat(searchResults.getSelection(), not(equalTo(selection)));

    }

    private static class MySearchResultsComponent extends SearchResultsComponent {
        public MySearchResultsComponent(WebDriver wd, AngularModelAccessor model) {
            super("", wd, model);
        }

        @Override
        protected void openPage(String url) {
            verifyOnPage();
        }
    }
}
