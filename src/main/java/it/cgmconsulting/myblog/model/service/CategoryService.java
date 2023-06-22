package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.entity.Category;
import it.cgmconsulting.myblog.model.data.payload.request.SwitchVisibilityRequest;
import it.cgmconsulting.myblog.model.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repo;

    public ResponseEntity<?> addCategory(String categoryName){
        if(repo.existsByCategoryName(categoryName))
            return new ResponseEntity<>("Category alredy present", HttpStatus.BAD_REQUEST);
        repo.save(new Category(categoryName));
        return new ResponseEntity<>("New category has been added", HttpStatus.OK);
    }

    public ResponseEntity<?> getCategories(){
        return new ResponseEntity<>(repo.getCategoriesVisible(), HttpStatus.OK);
    }

    public ResponseEntity<List<Category>> getAllCategories(){
        return new ResponseEntity<>(repo.getAllCategories(), HttpStatus.OK);
    }

    public ResponseEntity<String> switchVisibility(SwitchVisibilityRequest request) {
        repo.saveAll(request.getCategories());
        return new ResponseEntity<String>("Category visibility has been updated", HttpStatus.OK);
    }
}
