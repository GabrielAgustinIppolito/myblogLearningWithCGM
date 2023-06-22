package it.cgmconsulting.myblog.model.data.payload.request;

import it.cgmconsulting.myblog.model.data.entity.Category;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class SwitchVisibilityRequest {

    @NotEmpty
    List<Category> categories;
}
