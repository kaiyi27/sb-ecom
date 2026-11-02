package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new APIException("There is no category created yet!");
        }
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists!");
        }
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(category);

        return "Category id: " + categoryId + " deleted successfully!";
    }

    @Override
    public String updateCategory(Long categoryId, Category updatedCategory) {
        String updatedCategoryName = updatedCategory.getCategoryName();
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Category savedCategory = categoryRepository.findByCategoryName(updatedCategoryName);
        if (!category.getCategoryName().equals(updatedCategoryName) && savedCategory != null) {
            throw new APIException("Category with the name " + updatedCategoryName + " already exists!");
        }

        category.setCategoryName(updatedCategoryName);
        categoryRepository.save(category);

        return "Category id: " + categoryId + " updated successfully!";
    }
}
