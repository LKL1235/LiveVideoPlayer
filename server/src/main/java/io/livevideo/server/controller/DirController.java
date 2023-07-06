package io.livevideo.server.controller;

import io.livevideo.server.DTO.DirDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author:25445
 * @date:2022/12/4 20:10
 **/
@RestController
public class DirController {
    //e:\\video
    @Value("${var.filePath}")
    private String filePath;
    @Autowired
    private DirDTO dirDTO;
    @RequestMapping("/getFileList/{filePath}")
    public List<DirDTO> getFileList(@PathVariable String filePath){
        // String path = "/src/video";
        // String path = "/src/video"+fdir;
        System.out.println(filePath);
        String path = filePath + "\\" + filePath;
        //e:\\video\\ad2
        System.out.println(path);
        System.out.println(123);
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
        return list;
    }
    @RequestMapping("/getFileList")
    public List<DirDTO> getFileListNoParam(){
        // String path = "/src/video";
        // String path = "/src/video"+fdir;
        String path = filePath;
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
        return list;
    }

    @RequestMapping("/getDir")
    public List<DirDTO> getDir(){
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
        return list;
    }
}
