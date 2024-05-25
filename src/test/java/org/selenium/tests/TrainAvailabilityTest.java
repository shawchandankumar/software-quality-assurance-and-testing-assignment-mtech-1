package org.selenium.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.MarionetteDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.selenium.utils.ExcelUtils;


public class TrainAvailabilityTest {

    private String testUrl;
    private ExcelUtils eu;
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void prepare() throws IOException {
        //setup chromedriver
        System.setProperty(
                "webdriver.chrome.driver",
                "webdriver/chromedriver.exe");

        testUrl = "https://www.irctc.co.in/";

        // Create a new instance of the Chrome driver
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);

        //maximize window
        driver.manage().window().maximize();

        eu = new ExcelUtils();
    }

    @DataProvider(name = "excelData")
    public Object[][] getData() throws IOException {
        String filePath = "data/TestDataForSelenium.xlsx";
        String sheetName = "Sheet1";
        Object[][] testData = ExcelUtils.readExcelSheetTestData(filePath, sheetName);
        return new Object[][]{testData[0], testData[1], testData[2], testData[3], testData[4]};
    }

    @Test(dataProvider = "excelData")
    public void testIRCTCTrainSearchPage(String source, String dest, String date, String expectedOutput) throws Exception {
        driver.get(testUrl);
        WebElement sourceInput = driver.findElement(By.xpath("(//input[@role='searchbox'])[1]"));
        WebElement destinationInput = driver.findElement(By.xpath("(//input[@role='searchbox'])[2]"));
        WebElement dateInput = driver.findElement(By.xpath("//p-calendar/span/input"));
        WebElement searchBtn = driver.findElement(By.xpath("//button[@label='Find Trains']"));

        sourceInput.sendKeys(source);
        destinationInput.sendKeys(dest);
        dateInput.sendKeys(date);
        dateInput.clear();
        dateInput.sendKeys(date);
        String prevUrl = driver.getCurrentUrl();
        searchBtn.click();
        Thread.sleep(2000);

        String errorFromToSame = "Origin and destination can\"t be same";
        String noTrains = "No direct trains available between the inputted stations";
        String invalidDate = "Invalid journey date";

        if (expectedOutput.equals("List of trains page appear")) {
            // check if the trains list has appear by comparing the url
            Assert.assertNotEquals(prevUrl, driver.getCurrentUrl(), "List of trains page doesn't appear");
        } else if (expectedOutput.contains(errorFromToSame)) {
            verifyMessageContent("//div[contains(@class, 'ui-toast-detail')]", errorFromToSame);
        } else if (expectedOutput.contains(noTrains)) {
            verifyMessageContent("//div[contains(@class,'ui-dialog-content')]/span", noTrains);
        } else if (expectedOutput.contains(invalidDate)) {
            verifyMessageContent("//div[contains(@class, 'ui-toast-detail')]", invalidDate);
        } else {
            // the page remains same, nothing changes because of invalid input
            Assert.assertEquals(prevUrl, driver.getCurrentUrl());
        }
    }

    private void verifyMessageContent(String elementXpath, String expectedMessage) {
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
        String actualToastMessage = toastMessage.getText();
        String expectedToastMessage = expectedMessage;
        Assert.assertEquals(actualToastMessage, expectedToastMessage, "The message text does not match the expected value.");
    }

    @AfterMethod
    public void teardown() throws IOException {
        if (driver != null) {
            driver.quit();
        }
    }

}
