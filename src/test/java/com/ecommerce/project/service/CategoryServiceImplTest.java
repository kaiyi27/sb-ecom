package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getAllCategoriesTest() {

        Category category1 = new Category();
        category1.setCategoryId(1L);
        category1.setCategoryName("Electronics");

        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setCategoryName("Furniture");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
        assertEquals("Furniture", result.get(1).getCategoryName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getAllCategoriesEmptyTest() {

        when(categoryRepository.findAll())
                .thenReturn(List.of());

        APIException exception = assertThrows(
                APIException.class,
                () -> categoryService.getAllCategories()
        );

        assertEquals(
                "There is no category created yet!",
                exception.getMessage()
        );

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void createCategoryTest() {

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        when(categoryRepository.findByCategoryName("Electronics"))
                .thenReturn(null);

        categoryService.createCategory(category);

        verify(categoryRepository, times(1)).findByCategoryName("Electronics");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void createCategoryDuplicateTest() {

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        when(categoryRepository.findByCategoryName("Electronics"))
                .thenReturn(category);

        APIException exception = assertThrows(
                APIException.class,
                () -> categoryService.createCategory(category)
        );

        assertEquals(
                "Category with the name Electronics already exists!",
                exception.getMessage()
        );

        verify(categoryRepository, times(1)).findByCategoryName("Electronics");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategoryTest() {

        Long categoryId = 1L;

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        String result = categoryService.deleteCategory(categoryId);

        assertEquals(
                "Category id: 1 deleted successfully!",
                result
        );

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryNotFoundTest() {

        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId)
        );

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void updateCategoryTest() {

        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Electronics");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Gadgets");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByCategoryName("Gadgets"))
                .thenReturn(null);

        String result = categoryService.updateCategory(categoryId, updatedCategory);

        assertEquals(
                "Category id: 1 updated successfully!",
                result
        );

        assertEquals("Gadgets", existingCategory.getCategoryName());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByCategoryName("Gadgets");
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void updateCategorySameNameTest() {

        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Electronics");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Electronics");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByCategoryName("Electronics"))
                .thenReturn(existingCategory);

        String result = categoryService.updateCategory(categoryId, updatedCategory);

        assertEquals(
                "Category id: 1 updated successfully!",
                result
        );

        assertEquals("Electronics", existingCategory.getCategoryName());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByCategoryName("Electronics");
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void updateCategoryDuplicateNameTest() {

        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Electronics");

        Category duplicateCategory = new Category();
        duplicateCategory.setCategoryId(2L);
        duplicateCategory.setCategoryName("Furniture");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Furniture");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByCategoryName("Furniture"))
                .thenReturn(duplicateCategory);

        APIException exception = assertThrows(
                APIException.class,
                () -> categoryService.updateCategory(categoryId, updatedCategory)
        );

        assertEquals(
                "Category with the name Furniture already exists!",
                exception.getMessage()
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategoryNotFoundTest() {

        Long categoryId = 1L;

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Gadgets");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, updatedCategory)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }
}