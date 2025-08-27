package com.jobHunter.demoAPI.controller.file;

import com.jobHunter.demoAPI.domain.dto.file.RestUploadFileDTO;
import com.jobHunter.demoAPI.service.FileService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.jobHunter.demoAPI.util.exception.custom.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    @ApiMessage("Upload single file")
    public ResponseEntity<RestUploadFileDTO> uploadSingleFileRequest(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "folder", required = false) String folder
    ) throws
            URISyntaxException,
            IOException,
            StorageException {
        // validate file
        if (file.isEmpty() || file.getSize() == 0) {
            throw new StorageException("File is empty. Please upload a file.");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "docx", "doc", "pdf");

        boolean isValid = allowedExtensions.stream()
                .anyMatch(extension -> {
                    if (fileName != null) {
                        return fileName.toLowerCase().endsWith(extension);
                    } else {
                        return false;
                    }
                });

        if (!isValid) {
            throw new StorageException("Invalid file extension. Please upload a file.");
        }

        // create a directory if not exist
        this.fileService.createUploadFolder(this.baseURI + folder);

        // store file
        String fileUploaded = this.fileService.storeFile(file, folder);
        RestUploadFileDTO restUploadFileDTO = new RestUploadFileDTO(fileUploaded, Instant.now());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(restUploadFileDTO);
    }

    @GetMapping
    @ApiMessage("Download single file")
    public ResponseEntity<Resource> downloadSingleFileRequest(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder
    ) throws
            URISyntaxException,
            StorageException,
            FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required parameters: (fileName or folder)");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + fileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"") // Trình duyệt tải file về, ko mở lên
                .contentLength(fileLength) // Kích thước file
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Dữ liệu nhị phân, ko xác định loại
                .body(resource);
    }
}
