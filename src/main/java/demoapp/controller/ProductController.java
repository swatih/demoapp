package demoapp.controller;

import javax.validation.Valid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import demoapp.model.Product;
import demoapp.service.ProductService;


@Controller
public class ProductController {

	private ProductService productService;
	 private static String UPLOAD_LOCATION="C:/mytemp/";
    @Autowired(required=true)
    @Qualifier(value="productService")
    public void setProductService(ProductService ps){
        this.productService = ps;
    }
     
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String listProducts(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("listProducts", this.productService.listProducts());
        return "ProductAdmin";
    }
     
    //For add and update product both
    @RequestMapping(value= "/product/add", method = RequestMethod.POST)
    public String addProduct(@Valid @ModelAttribute("product") Product p,BindingResult res,Model model){
    	if(res.hasErrors()) {
    	
    		model.addAttribute("listProducts", this.productService.listProducts());
            return "ProductAdmin";
        }
	    
        if(p.getId() == 0){
            //new product, add it
            this.productService.addProduct(p);
         
            MultipartFile file=p.getFile();
            String name=String.valueOf(p.getId());
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
     
                    // Creating the directory to store file
                    String rootPath = System.getProperty("catalina.home");
                    File dir = new File(rootPath + File.separator + "tmpFiles");
                    if (!dir.exists())
                        dir.mkdirs();
                   
                    // Create the file on server
                    File serverFile = new File(dir.getAbsolutePath()
                            + File.separator + name);
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(serverFile));
                    stream.write(bytes);
                    stream.close();
     
                    System.out.println("Server File Location=" + serverFile.getAbsolutePath());
     
                    return "You successfully uploaded file=" + name;
                } catch (Exception e) {
                    return "You failed to upload " + name + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + name
                        + " because the file was empty.";
            }
         
                
         
    
        }
        else{
            //existing product, call update
            this.productService.updateProduct(p);
        }
         
        return "redirect:/products";
         
    }
     
    @RequestMapping("/remove/{id}")
    public String removeProduct(@PathVariable("id") int id){
         
        this.productService.removeProduct(id);
        return "redirect:/products";
    }
  
    @RequestMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", this.productService.getProductById(id));
        model.addAttribute("listProducts", this.productService.listProducts());
        return "ProductAdmin";
    }
}
