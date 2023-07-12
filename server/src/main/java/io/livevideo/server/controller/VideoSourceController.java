package io.livevideo.server.controller;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.livevideo.server.utils.ResultCode;
import io.livevideo.server.utils.myResult;


@RestController
public class VideoSourceController{

    @Autowired
    private ChromeOptions WebDriverOptions;

    @RequestMapping("/getM3u8FromIframeSrcByHtml")
    public myResult getM3u8FromIframeSrcByHtml(@RequestParam("URL") Optional<String> URL){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(URL)){
            return new myResult(ResultCode.FAILURE.getCode(),"无效URL");
        }

        driver.get(URL.get());
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
        String link = getUrlFromHtmlByType(driver, "iframe");
        driver.get(link);
        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(5000));
        // wait.until(ExpectedConditions.attributeToBeNotEmpty(driver.findElement(By.tagName("video")), "src"));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        link = getUrlFromHtmlByEndswith(driver, ".m3u8");
        driver.quit();
        return new myResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
    }

    @RequestMapping("/getM3u8FromHtml")
    public myResult getM3u8FromHtml(@RequestParam("URL") Optional<String> URL){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(URL)){
            return new myResult(ResultCode.FAILURE.getCode(),"无效URL");
        }
        driver.get(URL.get());
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
        String link = getUrlFromHtmlByEndswith(driver, ".m3u8");
        driver.quit();
        return new myResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
    }

    public Boolean isUrlNull(Optional<String> URL){
        if (!URL.isPresent() || URL.isEmpty()){
            return true;
        }
        return false;
    }
    
    public String getUrlFromHtmlByType(WebDriver driver, String initiatorType){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Map> xhr = (ArrayList)jse.executeScript("return window.performance.getEntries();");

        String link = "";

        for (Map item : xhr) {
            try{
                if (!item.containsKey("initiatorType")){
                    continue;
                }
                if (item.get("initiatorType").equals(initiatorType))
                {
                    link = (String)item.get("name");
                    return link;
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return link;
    }

    public String getUrlFromHtmlByEndswith(WebDriver driver, String endsWith){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Map> xhr = (ArrayList)jse.executeScript("return window.performance.getEntries();");

        String link = "";
        Pattern pattern = Pattern.compile("\\"+endsWith+"$");

        for (Map item : xhr) {
            try{
                if (!item.containsKey("name")){
                    continue;
                }
                Matcher matcher = pattern.matcher((String)item.get("name"));
                if (matcher.find())
                {
                    link = (String)item.get("name");
                    return link;
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return link;
    }
}