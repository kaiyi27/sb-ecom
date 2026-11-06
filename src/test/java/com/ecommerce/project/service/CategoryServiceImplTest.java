package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

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

        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setCategoryId(1L);
        categoryDTO1.setCategoryName("Electronics");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setCategoryId(2L);
        categoryDTO2.setCategoryName("Furniture");

        Pageable pageDetails = PageRequest
                .of(0, 10, Sort.by("categoryId").ascending());

        Page<Category> categoryPage = new PageImpl<>(
                List.of(category1, category2)
        );

        when(categoryRepository.findAll(pageDetails))
                .thenReturn(categoryPage);

        when(modelMapper.map(category1, CategoryDTO.class))
                .thenReturn(categoryDTO1);

        when(modelMapper.map(category2, CategoryDTO.class))
                .thenReturn(categoryDTO2);

        CategoryResponse result = categoryService
                .getAllCategories(0, 10, "categoryId", "asc");

        assertEquals(2, result.getContent().size());
        assertEquals("Electronics",  result.getContent().get(0).getCategoryName());
        assertEquals("Furniture", result.getContent().get(1).getCategoryName());

        verify(categoryRepository, times(1)).findAll(pageDetails);
    }

    @Test
    void getAllCategoriesEmptyTest() {

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        APIException exception = assertThrows(
                APIException.class,
                () -> categoryService.getAllCategories(0, 10, "categoryId", "asc")
        );

        assertEquals(
                "There is no category created yet!",
                exception.getMessage()
        );

        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void createCategoryTest() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("Electronics");

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        when(categoryRepository.findByCategoryName("Electronics"))
                .thenReturn(null);

        when(modelMapper.map(categoryDTO, Category.class))
                .thenReturn(category);

        categoryService.createCategory(categoryDTO);

        verify(categoryRepository, times(1)).findByCategoryName("Electronics");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void createCategoryDuplicateTest() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("Electronics");

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        when(categoryRepository.findByCategoryName("Electronics"))
                .thenReturn(category);

        when(modelMapper.map(categoryDTO, Category.class))
                .thenReturn(category);

        APIException exception = assertThrows(
                APIException.class,
                () -> categoryService.createCategory(categoryDTO)
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

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(categoryId);
        categoryDTO.setCategoryName("Electronics");

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(modelMapper.map(category, CategoryDTO.class))
                .thenReturn(categoryDTO);

        CategoryDTO result = categoryService.deleteCategory(categoryId);

        assertEquals(categoryDTO, result);

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

        CategoryDTO existingCategoryDTO = new CategoryDTO();
        existingCategoryDTO.setCategoryId(categoryId);
        existingCategoryDTO.setCategoryName("Electronics");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Gadgets");

        CategoryDTO updatedCategoryDTO = new CategoryDTO();
        updatedCategoryDTO.setCategoryName("Gadgets");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByCategoryName("Gadgets"))
                .thenReturn(null);

        when(categoryRepository.save(updatedCategory))
                .thenReturn(updatedCategory);

        when(modelMapper.map(updatedCategoryDTO, Category.class))
                .thenReturn(updatedCategory);

        when(modelMapper.map(updatedCategory, CategoryDTO.class))
                .thenReturn(updatedCategoryDTO);

        CategoryDTO result = categoryService.updateCategory(categoryId, updatedCategoryDTO);

        assertEquals(updatedCategoryDTO, result);

        assertEquals("Gadgets", updatedCategory.getCategoryName());
        assertEquals(categoryId, updatedCategory.getCategoryId());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByCategoryName("Gadgets");
        verify(categoryRepository, times(1)).save(updatedCategory);
    }

//    @Test
//    void updateCategorySameNameTest() {
//
//        Long categoryId = 1L;
//
//        Category existingCategory = new Category();
//        existingCategory.setCategoryId(categoryId);
//        existingCategory.setCategoryName("Electronics");
//
//        Category updatedCategory = new Category();
//        updatedCategory.setCategoryName("Electronics");
//
//        when(categoryRepository.findById(categoryId))
//                .thenReturn(Optional.of(existingCategory));
//
//        when(categoryRepository.findByCategoryName("Electronics"))
//                .thenReturn(existingCategory);
//
//        String result = categoryService.updateCategory(categoryId, updatedCategory);
//
//        assertEquals(
//                "Category id: 1 updated successfully!",
//                result
//        );
//
//        assertEquals("Electronics", existingCategory.getCategoryName());
//
//        verify(categoryRepository, times(1)).findById(categoryId);
//        verify(categoryRepository, times(1)).findByCategoryName("Electronics");
//        verify(categoryRepository, times(1)).save(existingCategory);
//    }
//
//    @Test
//    void updateCategoryDuplicateNameTest() {
//
//        Long categoryId = 1L;
//
//        Category existingCategory = new Category();
//        existingCategory.setCategoryId(categoryId);
//        existingCategory.setCategoryName("Electronics");
//
//        Category duplicateCategory = new Category();
//        duplicateCategory.setCategoryId(2L);
//        duplicateCategory.setCategoryName("Furniture");
//
//        Category updatedCategory = new Category();
//        updatedCategory.setCategoryName("Furniture");
//
//        when(categoryRepository.findById(categoryId))
//                .thenReturn(Optional.of(existingCategory));
//
//        when(categoryRepository.findByCategoryName("Furniture"))
//                .thenReturn(duplicateCategory);
//
//        APIException exception = assertThrows(
//                APIException.class,
//                () -> categoryService.updateCategory(categoryId, updatedCategory)
//        );
//
//        assertEquals(
//                "Category with the name Furniture already exists!",
//                exception.getMessage()
//        );
//
//        verify(categoryRepository, never()).save(any(Category.class));
//    }
//
//    @Test
//    void updateCategoryNotFoundTest() {
//
//        Long categoryId = 1L;
//
//        Category updatedCategory = new Category();
//        updatedCategory.setCategoryName("Gadgets");
//
//        when(categoryRepository.findById(categoryId))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> categoryService.updateCategory(categoryId, updatedCategory)
//        );
//
//        verify(categoryRepository, never()).save(any(Category.class));
//    }
}