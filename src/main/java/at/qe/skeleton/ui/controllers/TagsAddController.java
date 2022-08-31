package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scope("view")
public class TagsAddController{

    @Autowired
    TagService tagService;
    
    public void setService(TagService tagService) {
    	this.tagService = tagService;
    }

    private List<String> tags;

    public List<String> getTags() {

        return new ArrayList<>();
    }


    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void doSaveTags(){

        Set<String> allTagNames = new HashSet<>();

        for(Tags tag : tagService.getAllTags()){
            allTagNames.add(tag.getTagName());
        }

        for (String s : tags) {
            if (!allTagNames.contains(s)) {
                Tags t = new Tags();
                t.setTagName(s);
                tagService.saveTag(t);
            }
        }
    }


}
