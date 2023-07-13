package io.livevideo.server.controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.livevideo.server.utils.ResultCode;
import io.livevideo.server.utils.myResult;
import io.livevideo.server.utils.myHttpClient;


@RestController
public class VideoSourceController{

    @Autowired
    private ChromeOptions WebDriverOptions;
    @Autowired
    private myHttpClient httpClient;

    @RequestMapping("/getM3u8FromIframeByHtml")
    public myResult getM3u8FromIframeByHtml(@RequestParam("url") Optional<String> url){
        String link = getSrcFormIframeByEndswith(url,".m3u8");
        return new myResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
    }

    @RequestMapping("/getMp4FromIframeByHtml")
    public myResult getMp4FromIframeByHtml(@RequestParam("url") Optional<String> url){
        String link = getSrcFormIframeByEndswith(url,".mp4");
        return new myResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
    }

    @RequestMapping("/getM3u8")
    public myResult getM3u8(@RequestParam("url") Optional<String> url){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(url)){
            return new myResult(ResultCode.FAILURE.getCode(),"无效URL");
        }

        String link = checkM3u8Nest(url.get());
        
        try { 
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        driver.quit();
        if (isStrNull(link)){
            return new myResult(ResultCode.SUCCESS.getCode(),"没有.m3u8嵌套",true);
        }
        URL fullUrl;
        try {
            fullUrl = new URL(url.get());
            String baseUrl = fullUrl.getProtocol() + "://" + fullUrl.getHost();
            link = baseUrl + link;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return new myResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
    }

    public Boolean isUrlNull(Optional<String> url){
        if (!url.isPresent() || url.isEmpty()){
            return true;
        }
        return false;
    }

    public Boolean isStrNull(String Str){

        if (Str == null || Str.isEmpty()) {
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
                driver.quit();
            }
        }
        return link;
    }

    public String getUrlFromHtmlByEndswith(WebDriver driver, String endswith){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Map> xhr = (ArrayList)jse.executeScript("return window.performance.getEntries();");

        String link = "";
        Pattern pattern = Pattern.compile("\\"+endswith+"$");

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
                driver.quit();
            }
        }
        return link;
    }

    public String getSrcFormIframeByEndswith(Optional<String> URL, String endswith){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(URL)){
            return "";
        }

        driver.get(URL.get());
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
        String link = getUrlFromHtmlByType(driver, "iframe");
        driver.get(link);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            driver.quit();
        }
        link = getUrlFromHtmlByEndswith(driver, endswith);
        driver.quit();
        return link;
    }

    public String checkM3u8Nest(String src){

        String filePath = httpClient.fileDownLoad(src);
        if (filePath == null||filePath.isEmpty()){
            return "";
        }
        Pattern pattern = Pattern.compile("\\.m3u8$");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}