package io.livevideo.server.controller;

import io.livevideo.server.DTO.DirDTO;
import io.livevideo.server.utils.ResultCode;
import io.livevideo.server.utils.MyHttpResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @description:
 * @author:LKL1235
 * @date:2022/12/4 20:10
 **/
@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5173", "http://localhost:5173"},allowCredentials = "true")
public class DirController {
    //e:\\video
    @Value("${var.filePath}")
    private String filePath;
    @Autowired
    private DirDTO dirDTO;

    @RequestMapping("/getFileList")
    public MyHttpResult getFileList(@RequestParam("DirName") Optional<String> DirName){
        // String path = "/src/video";
        // String path = "/src/video"+fdir;
        String path = filePath;
        if (DirName.isPresent() && !(DirName.get().isEmpty())){
            path = filePath + "\\" + DirName.get();
        }
        try {
            File file = new File(path);
            File[] fs = file.listFiles();
            List<DirDTO> list =new ArrayList<>();
            for(File f:fs){
                if(!f.isDirectory()) {
                    String s=f.getName();
                    DirDTO dirDTO1=new DirDTO(s);
                    list.add(dirDTO1);
                }
            }
            return new MyHttpResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMsg(),list);
        } catch (Exception e) {
            System.out.println(e);
            return new MyHttpResult(ResultCode.FAILURE.getCode(),"查询错误,请检查参数");
        }

    }
    // @RequestMapping("/getFileList")
    // public List<DirDTO> getFileListNoParam(){
    //     // String path = "/src/video";
    //     // String path = "/src/video"+fdir;
    //     String path = filePath;
    //     File file = new File(path);
    //     File[] fs = file.listFiles();
    //     List<DirDTO> list =new ArrayList<>();
    //     for(File f:fs){
    //         if(!f.isDirectory()) {
    //             String s=f.getName();
    //             DirDTO dirDTO1=new DirDTO(s);
    //             list.add(dirDTO1);
    //         }
    //     }
    //     return list;
    // }

    @RequestMapping("/getDir")
    public MyHttpResult getDir(){
        String path = filePath;		//要遍历的路径
        File file = new File(path);		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        List<DirDTO> list=new ArrayList<>();
        for(File f:fs){					//遍历File[]数组
            if(f.isDirectory())		//若非目录(即文件)，则打印
            {
                String s = f.getName();
                DirDTO dirDTO1=new DirDTO(s);
                list.add(dirDTO1);
                System.out.println(s);
            }
        }
        return new MyHttpResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMsg(),list);
    }
}
