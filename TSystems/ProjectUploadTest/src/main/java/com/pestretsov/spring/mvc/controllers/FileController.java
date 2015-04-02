package com.pestretsov.spring.mvc.controllers;

import java.io.*;
import java.util.*;
import java.nio.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.pestretsov.spring.mvc.model.FileMeta;

@Controller
@RequestMapping("/controller")
public class FileController {
    LinkedList<FileMeta> files = new LinkedList<FileMeta>();

    // RequestParam gets "file", thats why we need to specify the right name (e.g "file") in .html
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody LinkedList<FileMeta> upload(@RequestParam("file") MultipartFile file) {

        FileMeta fileMeta;

        if (!file.isEmpty()) {
            fileMeta = new FileMeta();
            fileMeta.setFileSize(file.getSize()/1024 + " Kb");
            fileMeta.setFileType(file.getContentType());

            try {
                fileMeta.setBytes(file.getBytes());
                //System.out.println(fileMeta.getFileType());
                String fileExtension = fileMeta.getFileType().split("/")[1];

                // save file to the local storage
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(fileMeta.getFileId())));
                stream.write(fileMeta.getBytes());
                stream.close();
                files.add(fileMeta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    @RequestMapping(value = "/delete/{value}", method = RequestMethod.DELETE)
    public @ResponseBody LinkedList<FileMeta> delete(@PathVariable String value) {
        try {
            // такой быдлокод и в продакшн можно
            System.out.print(value + " ");
            for (int i = 0; i < files.size(); i++) {
                System.out.println("Trying");
                System.out.println(value);
                System.out.println(files.get(i).getFileId());
                if (files.get(i).getFileId().equals(value)) {
                    (new File(value)).delete();
                    files.remove(i);
                    break;
                }
            }

            System.out.print("successfuly deleted\n");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return files;
    }
}
