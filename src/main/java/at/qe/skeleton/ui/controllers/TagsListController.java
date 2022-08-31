package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
@Scope("view")
public class TagsListController {

    @Autowired
    TagService tagService;

    public Collection<Tags> getAllTags(){
        return tagService.getAllTags();
    }
}
