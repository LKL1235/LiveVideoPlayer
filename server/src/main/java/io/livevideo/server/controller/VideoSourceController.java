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
import io.livevideo.server.utils.MyHttpResult;
import io.livevideo.server.utils.MyResult;
import io.livevideo.server.utils.MyHttpClient;


@RestController
public class VideoSourceController{

    private static final Logger log = LoggerFactory.getLogger(VideoSourceController.class);
    
    @Autowired
    private ChromeOptions WebDriverOptions;
    @Autowired
    private MyHttpClient httpClient;

    @RequestMapping("/getSrcFromIframeByHtml")
    public MyHttpResult getSrcFromIframeByHtml(@RequestParam("url") Optional<String> url,String type){
        log.info("收到getSrcFromIframeByHtml请求,参数url为:{}",url.get());
        MyResult myResult = getSrcFormIframeByEndswith(url,"."+type);
        if (myResult.getResult()==false){
            log.error("从"+url+"中获取失败,Error:"+myResult.getMsg());
            return new MyHttpResult(ResultCode.FAILURE.getCode(),(String)myResult.getMsg());
        }
        log.info("从"+url+"中获取成功,内容为"+myResult.getData());
        return new MyHttpResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),myResult.getData());
    }


    @RequestMapping("/checkM3u8")
    public MyHttpResult getM3u8(@RequestParam("url") Optional<String> url){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(url)){
            return new MyHttpResult(ResultCode.FAILURE.getCode(),"无效URL");
        }

        MyResult res = checkM3u8Nest(url.get());
        
        try { 
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        driver.quit();
        if (res.getResult() == false){
            return new MyHttpResult(ResultCode.FAILURE.getCode(),(String)res.getMsg());
        }
        URL fullUrl;
        String link = (String)res.getData();
        try {
            fullUrl = new URL(url.get());
            String baseUrl = fullUrl.getProtocol() + "://" + fullUrl.getHost();
            link = baseUrl + link;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return new MyHttpResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),link);
    }

    public MyResult checkM3u8Nest(String src){

        String filePath = httpClient.fileDownLoad(src);
        if (filePath == null||filePath.isEmpty()){
            return new MyResult(false, "从"+src+"下载失败", filePath);
        }
        Pattern pattern = Pattern.compile("\\.m3u8$");
        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    return new MyResult(true, "从"+src+"下载成功", line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new MyResult(false, filePath, line);
    }

    public MyResult driverGet(WebDriver driver, int maxRetries, String url){
        int retryCount = 0;
        Boolean success = false;
        String link = "";
        while (retryCount < maxRetries && !success) {
            try {
                log.info("正在尝试读取{}", url);
                driver.get(url);
                TimeUnit.SECONDS.sleep(5);
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
                success = true;
            } catch (Exception e) {
                log.error("读取"+url+";第"+retryCount+"次重试失败");
                retryCount++;
                if (retryCount == maxRetries) {
                    // 达到最大重试次数，退出重试
                    driver.quit();
                    return new MyResult(false, "重试"+maxRetries+"次后失败", "");
                }
            }
        }
        return new MyResult(true, url, link);
    }
    

    public MyResult getUrlFromHtmlByType(WebDriver driver, String initiatorType){
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
                    log.info("iframe的内容: {}",link);
                    if (link.endsWith(".m3u8")){
                        return new MyResult(true, "从"+initiatorType+"中获取成功", link);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                driver.quit();
            }
        }
        log.debug(xhr.toString());
        return new MyResult(false, "从"+initiatorType+"中获取失败", link);
    }

    public MyResult getUrlFromHtmlByEndswith(WebDriver driver, String endswith){
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
                    return new MyResult(true, "从"+endswith+"中获取成功", link);
                }
            }catch(Exception e){
                e.printStackTrace();
                driver.quit();
            }
        }
        log.debug(xhr.toString());
        return new MyResult(false, "从"+endswith+"中获取失败", link);
    }

    public MyResult getSrcFormIframeByEndswith(Optional<String> url, String endswith){
        WebDriver driver = new ChromeDriver(WebDriverOptions);
        if (isUrlNull(url)){
            return new MyResult(false, "无效URL", "");
        }
        
        MyResult res = driverGet(driver, 4, url.get());

        if (res.getResult() == false){
            driver.quit();
            return new MyResult(false, (String)res.getData(), "");
        }
        
        res = getUrlFromHtmlByType(driver, "iframe");
        if (res.getResult() == false){
            driver.quit();
            return new MyResult(false, (String)res.getMsg(), "");
        }

        res = driverGet(driver, 4, (String)res.getData());

        res = getUrlFromHtmlByEndswith(driver, endswith);
        driver.quit();
        return new MyResult((boolean)res.getResult(), (String)res.getMsg(), (String)res.getData());
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



}