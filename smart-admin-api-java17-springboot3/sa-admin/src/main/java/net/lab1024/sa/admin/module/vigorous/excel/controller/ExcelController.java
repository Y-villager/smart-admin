package net.lab1024.sa.admin.module.vigorous.excel.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.common.util.SmartResponseUtil;
import net.lab1024.sa.base.module.support.file.domain.vo.FileDownloadVO;
import net.lab1024.sa.base.module.support.file.domain.vo.FileMetadataVO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Value("${file.excel.failed-import.upload-path}")
    private String uploadPath;

    @Value("${file.excel.failed-import.failed-data-name}")
    private String failedDataName;

    private String filepath;

    @GetMapping("/download_failed_data")
    public void downloadFailedImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filePath = uploadPath+ SmartRequestUtil.getRequestUserId() + "/" + failedDataName;
        ResponseDTO<FileDownloadVO> downloadRes = download(filePath);
        if (downloadRes.getOk()) {
            downloadRes.getData().getMetadata().setFileName(failedDataName);
        }
        if (!downloadRes.getOk()) {
            SmartResponseUtil.write(response, downloadRes);
        }
        FileDownloadVO fileDownloadVO = downloadRes.getData();
        SmartResponseUtil.setDownloadFileHeader(response, failedDataName, fileDownloadVO.getMetadata().getFileSize());
        response.getOutputStream().write(fileDownloadVO.getData());
    }

    @GetMapping("/downloadExcel/{path}")
    public void downloadExcel(HttpServletRequest request, HttpServletResponse response, @PathVariable("path") String filepath) throws IOException {
        if (filepath == null || filepath.isEmpty()) {
            return;
        }
        ResponseDTO<FileDownloadVO> downloadRes = download(filepath);
        if (downloadRes.getOk()) {
            downloadRes.getData().getMetadata().setFileName(failedDataName);
        }
        if (!downloadRes.getOk()) {
            SmartResponseUtil.write(response, downloadRes);
        }
        FileDownloadVO fileDownloadVO = downloadRes.getData();
        SmartResponseUtil.setDownloadFileHeader(response, failedDataName, fileDownloadVO.getMetadata().getFileSize());
        response.getOutputStream().write(fileDownloadVO.getData());
    }

    private ResponseDTO<FileDownloadVO> download(String filePath){
        File localFile = new File(filePath);
        InputStream in = null;
        try {
            in = Files.newInputStream(localFile.toPath());
            // 输入流转换为字节流
            byte[] buffer = FileCopyUtils.copyToByteArray(in);
            FileDownloadVO fileDownloadVO = new FileDownloadVO();
            fileDownloadVO.setData(buffer);

            FileMetadataVO fileMetadataDTO = new FileMetadataVO();
            fileMetadataDTO.setFileName(localFile.getName());
            fileMetadataDTO.setFileSize(localFile.length());
            fileMetadataDTO.setFileFormat(FilenameUtils.getExtension(localFile.getName()));
            fileDownloadVO.setMetadata(fileMetadataDTO);

            return ResponseDTO.ok(fileDownloadVO);
        } catch (IOException e) {
            System.out.println("文件下载-发生异常：");
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "文件下载失败");
        } finally {
            try {
                // 关闭输入流
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("文件下载-发生异常：");
            }
        }
    }
}
