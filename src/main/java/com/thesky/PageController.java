package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products-page")
    public String showProductsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/products-page/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "redirect:/products-page";
    }

    @GetMapping("/products-page/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/products-page/add")
    public String processAddForm(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products-page";
    }

    @GetMapping("/products-page/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id).orElseThrow();
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/products-page/edit/{id}")
    public String processEditForm(@PathVariable Integer id, @ModelAttribute Product product) {
        product.setProductId(id);
        productService.saveProduct(product);
        return "redirect:/products-page";
    }

    @GetMapping("/categories-page")
    public String showCategoriesPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    @GetMapping("/categories-page/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "add-category";
    }

    @PostMapping("/categories-page/add")
    public String processAddCategoryForm(@ModelAttribute Category category) {
        categoryService.saveCategory(category);
        return "redirect:/categories-page";
    }

    @GetMapping("/categories-page/edit/{id}")
    public String showEditCategoryForm(@PathVariable Integer id, Model model) {
        Category category = categoryService.getCategoryById(id).orElseThrow();
        model.addAttribute("category", category);
        return "edit-category";
    }

    @PostMapping("/categories-page/edit/{id}")
    public String processEditCategoryForm(@PathVariable Integer id, @ModelAttribute Category category) {
        category.setCategoryId(id);
        categoryService.saveCategory(category);
        return "redirect:/categories-page";
    }

    @GetMapping("/categories-page/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories-page";
    }

}