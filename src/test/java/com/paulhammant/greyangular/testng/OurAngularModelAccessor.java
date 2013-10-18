package com.paulhammant.greyangular.testng;

import com.google.inject.Inject;
import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class OurAngularModelAccessor extends AngularModelAccessor {

    @Inject
    public OurAngularModelAccessor(WebDriver driver) {
        super((JavascriptExecutor) driver);
    }
}
