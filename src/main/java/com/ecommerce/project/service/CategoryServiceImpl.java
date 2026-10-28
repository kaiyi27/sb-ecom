package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    private List<Category> categories =  new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        for (int i = 0; i < categories.size(); ++i) {
            Long id = categories.get(i).getCategoryId();
            if (id.equals(categoryId)) {
                categories.remove(i);
                return "Category id: " + id + " deleted successfully";
            }
        }
        return "There are no category with id " + categoryId + ". Failed to delete!";
    }
}
