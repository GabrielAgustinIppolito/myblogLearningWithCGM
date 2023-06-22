package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.model.data.payload.request.SwitchVisibilityRequest;
import it.cgmconsulting.myblog.model.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestParam @NotBlank @Size(max=50, min = 3) String categoryName){
        return categoryService.addCategory(categoryName);
    }

    @GetMapping("public")
    public ResponseEntity<?> getCategories(){
        return categoryService.getCategories();
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @PutMapping("switch-visibility")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> switchVisibility(@RequestBody @Valid SwitchVisibilityRequest request){
        return categoryService.switchVisibility(request);
    }

}
