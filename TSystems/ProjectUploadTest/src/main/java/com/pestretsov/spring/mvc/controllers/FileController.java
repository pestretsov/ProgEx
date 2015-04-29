package com.pestretsov.spring.mvc.controllers;

import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pestretsov.spring.mvc.model.FileMeta;


class MongoClientSingleton {
    static private MongoClient mongoClient;
    private MongoClientSingleton(){}
    public static MongoClient getInstance(){
        if (mongoClient == null) {
            try {
                mongoClient = new MongoClient("localhost", 27017);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return mongoClient;
    }
}

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

            try {
                MongoClient mongo = MongoClientSingleton.getInstance();
                DB db = mongo.getDB("mydb2");
                if (db!=null) {
                    System.out.println("Succesfully connected");
                }

                File picFile = new File("//home//roman//PROGEX//MPF//" + fileMeta.getFileId());
                if (picFile.exists()) {
                    String fileName = picFile.getName();

                    // "fs" is basically because we cant inspect any other
                    // collection from 'mongofiles'; actually collection
                    // could be named as desired
                    GridFS gfsPhoto = new GridFS(db, "fs");
                    GridFSInputFile gfsFile = gfsPhoto.createFile(picFile);
                    gfsFile.setFilename(fileName);
                    gfsFile.save();

                    gfsPhoto = new GridFS(db, "fs");
                    DBCursor cursor = gfsPhoto.getFileList();
                    while (cursor.hasNext()) {
                        System.out.println(cursor.next());
                    }
                    picFile.delete();

                } else {
                    System.out.println("no such file :(");
                }

                String newFileName = picFile.getName();
                GridFS gfsPhoto = new GridFS(db, "photo");
                GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);
                System.out.println(imageForOutput);
            } catch (IOException e){
                System.out.println("File opening error");
            }
        }


        return files;
    }

    @RequestMapping(value = "/delete/{value}", method = RequestMethod.DELETE)
    public @ResponseBody LinkedList<FileMeta> delete(@PathVariable String value) {
        Iterator<FileMeta> i = files.iterator();
        while (i.hasNext()){
            if (i.next().getFileId().equals(value)){
                i.remove();
                MongoClient mongo = MongoClientSingleton.getInstance();
                DB db = mongo.getDB("mydb2");

                if (db!=null) {
                    System.out.println("Succesfully connected");
                } else {
                    System.out.println("Connection failed");
                    break;
                }

                GridFS gfsPhoto = new GridFS(db, "fs");
                if (gfsPhoto.findOne(value)!=null) {
                    gfsPhoto.remove(gfsPhoto.findOne(value));
                    System.out.println("Successfully removed from mongodb");
                } else {
                    System.out.println("No such file in mongodb");
                }
                break;
            }
        }
        return files;
    }
}
