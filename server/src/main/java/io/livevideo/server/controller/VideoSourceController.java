package io.livevideo.server.controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.livevideo.server.utils.ResultCode;
import io.livevideo.server.utils.myHttpResult;
import io.livevideo.server.utils.myResult;
import io.livevideo.server.utils.myHttpClient;


@RestController
public class VideoSourceController{

    private static final Logger log = LoggerFactory.getLogger(VideoSourceController.class);
    
    @Autowired
    private ChromeOptions WebDriverOptions;
    @Autowired
    private myHttpClient httpClient;

    @RequestMapping("/getSrcFromIframeByHtml")
    public myHttpResult getSrcFromIframeByHtml(@RequestParam("url") Optional<String> url,String type){
        Map<String,Object> map = getSrcFormIframeByEndswith(url,"."+type);
                if (map.get("result").equals(false)){
            return new myHttpResult(ResultCode.FAILURE.getCode(),(String)map.get("msg"));
        }
        return new myHttpResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),map.get("data"));
    }


    @RequestMapping("/checkM3u8")
    public myHttpResult getM3u8(@RequestParam("url") Optional<String> url){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(url)){
            return new myHttpResult(ResultCode.FAILURE.getCode(),"无效URL");
        }

        Map<String,Object> map = checkM3u8Nest(url.get());
        
        try { 
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        driver.quit();
        if (map.get("result").equals(false)){
            return new myHttpResult(ResultCode.FAILURE.getCode(),(String)map.get("msg"));
        }
        URL fullUrl;
        String link = (String)map.get("data");
        try {
            fullUrl = new URL(url.get());
            String baseUrl = fullUrl.getProtocol() + "://" + fullUrl.getHost();
            link = baseUrl + link;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return new myHttpResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
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

    public Map<String,Object> driverGet(WebDriver driver, int maxRetries, String url){
        int retryCount = 0;
        Boolean success = false;
        String link = "";
        while (retryCount < maxRetries && !success) {
            try {
                driver.get(url);
                TimeUnit.SECONDS.sleep(5);
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                retryCount++;
                if (retryCount == maxRetries) {
                    // 达到最大重试次数，退出重试
                    driver.quit();
                    return myResult.getResultAsMap(false, "重试"+maxRetries+"次后失败", "");
                }
            }
        }
        return myResult.getResultAsMap(true, url, link);
    }

    
    public Map<String,Object> getUrlFromHtmlByType(WebDriver driver, String initiatorType){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Map<String,Object>> xhr = (ArrayList<Map<String,Object>>)jse.executeScript("return window.performance.getEntries();");

        String link = "";

        for (Map<String,Object> item : xhr) {
            try{
                if (!item.containsKey("initiatorType")){
                    continue;
                }
                if (item.get("initiatorType").equals(initiatorType))
                {
                    link = (String)item.get("name");
                    log.info(link);
                    return myResult.getResultAsMap(true, "从"+initiatorType+"中获取成功", link);
                }

            }catch(Exception e){
                e.printStackTrace();
                driver.quit();
            }
        }
        log.debug(xhr.toString());
        return myResult.getResultAsMap(false, "从"+initiatorType+"中获取失败", link);
    }

    public Map<String,Object> getUrlFromHtmlByEndswith(WebDriver driver, String endswith){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Map<String,Object>> xhr = (ArrayList<Map<String,Object>>)jse.executeScript("return window.performance.getEntries();");
        
        String link = "";
        Pattern pattern = Pattern.compile("\\"+endswith+"$");

        for (Map<String,Object> item : xhr) {
            try{
                if (!item.containsKey("name")){
                    continue;
                }
                Matcher matcher = pattern.matcher((String)item.get("name"));
                if (matcher.find())
                {
                    link = (String)item.get("name");
                    log.info(link);
                    return myResult.getResultAsMap(true, "从"+endswith+"中获取成功", link);
                }
            }catch(Exception e){
                e.printStackTrace();
                driver.quit();
            }
        }
        log.debug(xhr.toString());
        return myResult.getResultAsMap(false, "从"+endswith+"中获取失败", link);
    }

    public Map<String,Object> getSrcFormIframeByEndswith(Optional<String> URL, String endswith){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(URL)){
            return myResult.getResultAsMap(false, "无效URL", "");
        }

        Map res = driverGet(driver, 4, endswith);

        if (res.get("result").equals(false)){
            driver.quit();
            return myResult.getResultAsMap(false, (String)res.get("data"), "");
        }

        Map<String,Object> map = getUrlFromHtmlByType(driver, "iframe");
        if (map.get("result").equals(false)){
            driver.quit();
            return myResult.getResultAsMap(false, (String)map.get("msg"), "");
        }

        res = driverGet(driver, 4, (String)map.get("data"));

        map = getUrlFromHtmlByEndswith(driver, endswith);
        driver.quit();
        return myResult.getResultAsMap((boolean)map.get("result"), (String)map.get("msg"), (String)map.get("data"));
    }

    public Map<String,Object> checkM3u8Nest(String src){

        String filePath = httpClient.fileDownLoad(src);
        if (filePath == null||filePath.isEmpty()){
            return myResult.getResultAsMap(false, "从"+src+"下载失败", filePath);
        }
        Pattern pattern = Pattern.compile("\\.m3u8$");
        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    return myResult.getResultAsMap(true, "从"+src+"下载成功", line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myResult.getResultAsMap(false, filePath, line);
    }

}