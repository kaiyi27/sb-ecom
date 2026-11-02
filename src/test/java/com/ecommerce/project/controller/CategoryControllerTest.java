package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getAllCategoriesTest() throws Exception {

        Category category1 = new Category();
        category1.setCategoryId(1L);
        category1.setCategoryName("Electronics");

        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setCategoryName("Furniture");

        when(categoryService.getAllCategories())
                .thenReturn(List.of(category1, category2));

        mockMvc.perform(get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"))
                .andExpect(jsonPath("$[1].categoryName").value("Furniture"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void createCategoryTest() throws Exception {

        String requestBody = """
            {
                "categoryId": 1,
                "categoryName": "Electronics"
            }
            """;

        mockMvc.perform(post("/api/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string(
                        "Category id: 1 added successfully!"
                ));

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void deleteCategoryTest() throws Exception {

        when(categoryService.deleteCategory(3L))
                .thenReturn("Category id: 3 deleted successfully!");

        mockMvc.perform(delete("/api/admin/categories/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category id: 3 deleted successfully!"));

        verify(categoryService, times(1)).deleteCategory(3L);
    }

    @Test
    void updateCategoryTest() throws Exception {

        String requestBody = """
            {
                "categoryName": "Updated Electronics"
            }
            """;

        when(categoryService.updateCategory(eq(5L), any(Category.class)))
                .thenReturn("Category id: 5 updated successfully!");

        mockMvc.perform(put("/api/admin/categories/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Category id: 5 updated successfully!"
                ));

        verify(categoryService, times(1))
                .updateCategory(eq(5L), any(Category.class));
    }

    @Test
    void createCategoryWithInvalidNameTest() throws Exception {

        String blankCategoryNameRequestBody = """
        {
            "categoryName": ""
        }
        """;

        mockMvc.perform(post("/api/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankCategoryNameRequestBody))
                .andExpect(status().isBadRequest());

        String singleCharCategoryNameRequestBody = """
        {
            "categoryName": "a"
        }
        """;

        mockMvc.perform(post("/api/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleCharCategoryNameRequestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never())
                .createCategory(any(Category.class));
    }

    @Test
    void updateCategoryWithInvalidNameTest() throws Exception {

        String blankCategoryNameRequestBody = """
        {
            "categoryName": ""
        }
        """;

        mockMvc.perform(put("/api/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankCategoryNameRequestBody))
                .andExpect(status().isBadRequest());

        String singleCharCategoryNameRequestBody = """
        {
            "categoryName": "a"
        }
        """;

        mockMvc.perform(put("/api/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleCharCategoryNameRequestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never())
                .updateCategory(eq(1L), any(Category.class));
    }

    @Test
    void invalidEndpointTest() throws Exception {

        mockMvc.perform(get("/api/public/category"))
                .andExpect(status().isNotFound());
    }
}