package com.chetu.productManagementSystem.controllers;

import com.chetu.productManagementSystem.model.Product;
import com.chetu.productManagementSystem.model.ProductDto;
import com.chetu.productManagementSystem.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @GetMapping({"", "/"})
    public String showProducts(Model model) {
        List<Product> products = repo.findAll();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/createProduct")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "createProduct";
    }

    @PostMapping("/createProduct")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile ", "The image file cannot be empty"));
        }
        if (result.hasErrors()) {
            return "createProduct";
        }

        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + " " + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (Exception ex) {

            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/editProduct")
    public String showEditPage(Model model, @RequestParam int id) {

        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);


            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setDescription(product.getDescription());
            productDto.setPrice(product.getPrice());

            model.addAttribute("productDto", productDto);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

        return "editProduct";
    }


    @PostMapping("/editProduct")
    public String updateProduct(Model model, @RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {


        try {

            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "editProduct";
            }


            if (!productDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);


            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }


    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id){

        try{
            Product product = repo.findById(id).get();

            Path imagePath = Paths.get("public/images"+ product.getImageFileName());

            try{
                Files.delete(imagePath);

            }catch (Exception e){
                System.out.println(e.getMessage());
            }

            repo.delete(product);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }


}
