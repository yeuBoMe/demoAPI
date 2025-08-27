package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void createUploadFolder(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);

        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectories(tmpDir.toPath());
                System.out.println("CREATE DIRECTORY SUCCESSFULLY, PATH = " + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("SKIPPING CREATE DIRECTORY, ALREADY EXISTS");
        }
    }

    @Override
    public String storeFile(MultipartFile file, String folder) throws URISyntaxException, IOException {
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(this.baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    @Override
    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(this.baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File tmpDir = new File(path.toString());
        if (!tmpDir.exists() || tmpDir.isDirectory()) {
            return 0;
        }

        return tmpDir.length();
    }

    @Override
    public InputStreamResource getResource(String fileName, String folder) throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(this.baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File file =  new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
