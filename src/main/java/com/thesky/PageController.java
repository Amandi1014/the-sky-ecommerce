package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/products-page")
    public String showProductsPage(@org.springframework.web.bind.annotation.RequestParam(required = false) Integer categoryId, Model model) {
        if (categoryId != null) {
            model.addAttribute("products", productService.getProductsByCategory(categoryId));
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        return "products";
    }

    @GetMapping("/categories-page")
    public String showCategoriesPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

}