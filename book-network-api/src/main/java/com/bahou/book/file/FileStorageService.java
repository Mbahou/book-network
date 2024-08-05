package com.bahou.book.file;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;
    public String  saveFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull Integer userId) {
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath) {
        final String finalUploadFile = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadFile);
        if (!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated){
                log.warn("Failed to created the target folder");

            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // .upload /users/1/232659874.jpg
        String targetFilePath = finalUploadFile + separator +System.currentTimeMillis() + "." +fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved to " +targetFilePath);
            return targetFilePath;

        }catch (IOException  e){
            log.error("File was not saved",e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()){
            return "";
        }
        //PHOTO.PNG
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1){
            return "";
        }
        //.JPG ->jpg (to LowerCase())
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
