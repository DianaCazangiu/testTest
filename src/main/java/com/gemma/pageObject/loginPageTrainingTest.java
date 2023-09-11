package com.gemma.pageObject;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gemma.utils.waitHelper;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.gemma.pageObject.callPage.*;
import static com.gemma.utils.PropertyFileReader.*;
import static com.gemma.utils.encryptDecrypt.*;


public class loginPageTrainingTest extends baseClass{
  static Properties propertyReader;

  static {
    try {
      propertyReader = getPropertyFile("config.properties");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static Properties propertyRead;

  static {
    try {
      propertyRead = getPropertyLocatorsFile("generalLocators.properties");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private static Logger logger = LoggerFactory.getLogger(loginPageTrainingTest.class);

  public static String callIDSummary;
  public static String NewCallID;


  public static By getStartSession() {
    return By.xpath(propertyRead.getProperty("x_startSessionButton"));
  }

  public static By getTestSuperuserAndSuperviserRole() {
    return By.xpath(propertyRead.getProperty("x_testSuperuserAndSuperviserRole"));
  }

  public static By getLogonButton() {
    return By.xpath(propertyRead.getProperty("x_logonButton"));
  }


  public static void loginToGemmaTraining() {
    driver.quit();
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    ((RemoteWebDriver) driver).setLogLevel(Level.INFO);
    driver.manage().window().maximize();
    driver.get(getWebsite());
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  public static void accessGemmaAccount() {

    driver.findElement(By.xpath(propertyRead.getProperty("x_username"))).sendKeys(getDecryptUsername(decryptedData1));
    logger.info("Username is correctly entered");
    driver.findElement(By.xpath(propertyRead.getProperty("x_password"))).sendKeys(getDecryptPassword(decryptedData2));
    logger.info("Password is correctly entered");
    driver.findElement(getStartSession()).click();
    waitHelper.hardWait(1000);
    logger.info("Access requested");

    if (driver.findElement(By.xpath(propertyRead.getProperty("x_confirmationYesButton"))).isDisplayed()) {
      driver.findElement(By.xpath(propertyRead.getProperty("x_confirmationYesButton"))).click();
    }
  }

  public static void getGemmaRole() {
    driver.findElement(By.xpath(propertyRead.getProperty("x_roleArrow"))).click();
    //waitHelper.hardWait(1000);
    driver.findElement(getTestSuperuserAndSuperviserRole()).click();
    waitHelper.hardWait(1000);
    logger.info("User role selected");
    driver.findElement(getLogonButton()).click();
    logger.info("Access done");
    waitHelper.hardWait(2000);
    String actualTitle = driver.getTitle();
    String expectedTitle = "GEMMA";

    assert actualTitle.equals(expectedTitle);
    logger.info("The actual Title is " + actualTitle);
  }

  public static void getCallTraining() {
    waitHelper.hardWait(6000);
    driver.findElement(getAccessUserMenu()).click();
    waitHelper.hardWait(6000);
    Actions actTheme = new Actions(driver);
    //waitHelper.waitForElementVisible(getCreateMenu(),40,5);
    WebElement callCreate = driver.findElement(getCreateMenu());
    actTheme.moveToElement(callCreate).perform();
    waitHelper.hardWait(4000);
    //waitHelper.waitForElementVisible(By.xpath(propertyRead.getProperty("x_callButton")),40,5);
    driver.findElement(By.xpath(propertyRead.getProperty("x_callButton"))).click();
    waitHelper.hardWait(5000);
    logger.info("The call file is opened");
  }

  public static int getPhoneNumber() {
    Random objGenerator = new Random();
    int finalPhoneNumber = Integer.parseInt("07" + objGenerator.nextInt(90000000));
    logger.info("Random No : " + finalPhoneNumber);
    //System.out.println("Random No : " + finalPhoneNumber);
    return finalPhoneNumber;
  }

  public static void getData() {
    waitHelper.hardWait(3000);
    driver.findElement(By.xpath(propertyRead.getProperty("x_priorityArrow"))).click();
    waitHelper.hardWait(500);
    driver.findElement(By.xpath(propertyRead.getProperty("x_highPrioritySelect"))).click();
    logger.info("Priority was selected");

    //select call type
    driver.findElement(By.xpath(propertyRead.getProperty("x_callTypeArrow"))).click();
    waitHelper.hardWait(500);
    driver.findElement(By.xpath(propertyRead.getProperty("x_callTypeButton"))).click();

    //enter phone number
    String phoneNumber = Integer.toString(getPhoneNumber());
    driver.findElement(By.xpath(propertyRead.getProperty("x_phoneNumberBoxInput"))).sendKeys("+40" + phoneNumber + '\n');
    logger.info("Phone number was correctly entered");

    //extract CallID
    waitHelper.hardWait(5000);
    driver.findElement(getKeyDataTab()).click();
    driver.findElement(By.xpath(propertyRead.getProperty("x_SummaryTab"))).click();

    callIDSummary = driver.findElement(By.xpath(propertyRead.getProperty("x_callIDBox"))).getText();

    logger.info(callIDSummary);
  }

  public static String getNewCallIDFromUI() {
    Pattern pattern = Pattern.compile("C+[0-9]+-+[0-9]+");

    Matcher mat = pattern.matcher(callIDSummary);

    while (mat.find()) {
      NewCallID = mat.group();

    }
    System.out.println("Match: " + NewCallID);
    return NewCallID;
  }



}
