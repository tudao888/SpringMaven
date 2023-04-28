package com.SpringMaven.controller;

import com.SpringMaven.Exception.FileUploadExceptionAdvice;
import com.SpringMaven.model.ExcelHelper;
import com.SpringMaven.model.Product;
import com.SpringMaven.model.ResponseMessage;
import com.SpringMaven.repository.ProductRepository;
import com.SpringMaven.service.ProductService;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@RestController
public class ExportProductController {
    public final static Logger logger = Logger.getLogger(ExportProductController.class);
    @Autowired
    private ProductService productService;

    @GetMapping("/export")
    public void generateExcelExport (HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        String timeStamp = dataFormat.format(new Date());
        String filename = "products_" + timeStamp + ".xlsx";
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" +filename;
        response.setHeader(headerKey, headerValue);
        productService.generateExcel(response);
        logger.info("Exported file successfully" + filename);
    }

    @PostMapping("/import")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file)  {
        String message = "";

        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                productService.save(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                logger.info(message);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload an excel file!";
        logger.info(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }
}
