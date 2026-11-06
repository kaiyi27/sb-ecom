package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
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

        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setCategoryId(1L);
        categoryDTO1.setCategoryName("Electronics");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setCategoryId(2L);
        categoryDTO2.setCategoryName("Furniture");

        List<CategoryDTO> list = List.of(categoryDTO1, categoryDTO2);

        CategoryResponse categoryResponse = new CategoryResponse(
                list, 0, 50, 2L, 1, true
        );

        when(categoryService.getAllCategories(0, 50, "categoryId", "asc"))
                .thenReturn(categoryResponse);

        mockMvc.perform(get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].categoryName").value("Electronics"))
                .andExpect(jsonPath("$.content[1].categoryName").value("Furniture"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(50))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.lastPage").value(true));

        verify(categoryService, times(1)).getAllCategories(0, 50, "categoryId", "asc");
    }

    @Test
    void createCategoryTest() throws Exception {

        String requestBody = """
                {
                    "categoryId": 1,
                    "categoryName": "Electronics"
                }
                """;

        CategoryDTO categoryDTO = new CategoryDTO(1L, "Electronics");

        when(categoryService.createCategory(categoryDTO))
                .thenReturn(categoryDTO);

        mockMvc.perform(post("/api/public/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        verify(categoryService, times(1)).createCategory(categoryDTO);
    }

    @Test
    void deleteCategoryTest() throws Exception {

        CategoryDTO categoryDTO = new CategoryDTO(3L, "Deleted");

        when(categoryService.deleteCategory(3L))
                .thenReturn(categoryDTO);

        mockMvc.perform(delete("/api/admin/categories/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(3L))
                .andExpect(jsonPath("$.categoryName").value("Deleted"));

        verify(categoryService, times(1)).deleteCategory(3L);
    }

    @Test
    void updateCategoryTest() throws Exception {

        String requestBody = """
                {
                    "categoryName": "Updated Electronics"
                }
                """;

        CategoryDTO categoryDTO = new CategoryDTO(5L, "Updated Electronics");

        when(categoryService.updateCategory(eq(5L), any(CategoryDTO.class)))
                .thenReturn(categoryDTO);

        mockMvc.perform(put("/api/public/categories/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(5L))
                .andExpect(jsonPath("$.categoryName").value("Updated Electronics"));

        verify(categoryService, times(1))
                .updateCategory(eq(5L), any(CategoryDTO.class));
    }
}
