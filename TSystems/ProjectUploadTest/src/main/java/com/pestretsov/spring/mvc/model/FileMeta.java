package com.pestretsov.spring.mvc.model;

import java.util.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//ignore "bytes" when return json format
@JsonIgnoreProperties({"bytes"})
public class FileMeta {

    // maybe we'll need UUID later
    private UUID fileUUID;
    // cannot overrude fileId cause should be unique
    private String fileId;
    private String fileSize;
    private String fileType;

    // to set the UUID
    private byte[] bytes;

    public String getFileId() {
        return fileId;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        fileUUID = UUID.nameUUIDFromBytes(bytes);
        fileId = fileUUID.toString();
        this.bytes = bytes;
    }

    public static UUID calculateUUID(byte[] bytes) {
        return UUID.nameUUIDFromBytes(bytes);
    }
}
