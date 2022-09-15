package com.arif.testapi.services;

import com.arif.testapi.payloads.CategoryDto;
import com.arif.testapi.payloads.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto, int categoryId);

    void deleteCategory(int categoryId);

    CategoryDto getCategoryById(int categoryId);

    CategoryResponse getCategories(int pageNumber, int pageSize);
}
