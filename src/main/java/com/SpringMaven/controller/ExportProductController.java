package com.SpringMaven.controller;

import com.SpringMaven.model.Product;
import com.SpringMaven.repository.ProductRepository;
import com.SpringMaven.service.ProductService;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@RestController
public class ExportProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/export-products")
    public void generateExcelExport (HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dataFormat.format(new Date());
        String filename = "products_" + timeStamp + ".xlsx";
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" +filename;
        response.setHeader(headerKey, headerValue);
        productService.generateExcel(response);
    }

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Đọc dữ liệu từ file Excel
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Xử lý dữ liệu
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                // Lấy giá trị từ các ô cell và tạo đối tượng Product
                Product product = new Product();
                product.setId((int) cellIterator.next().getNumericCellValue());
                product.setName(cellIterator.next().getStringCellValue());
                product.setPrice(cellIterator.next().getNumericCellValue());
                product.setQuantity((int) cellIterator.next().getNumericCellValue());
                product.setTotal(cellIterator.next().getNumericCellValue() * product.getQuantity());

                // Lưu đối tượng Product vào database
                productRepository.save(product);
            }

            return ResponseEntity.ok("Imported successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import: " + e.getMessage());
        }
    }
}
