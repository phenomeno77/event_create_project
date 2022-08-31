package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.TagService;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


@Component
@Scope("view")
public class TagsDetailController {

    @Autowired
    TagService tagService;

    private Tags tags;

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }
    
    public void setService(TagService tagService) {
    	this.tagService = tagService;
    }

    public void doSaveTags(){
        tags = this.tagService.saveTag(tags);
    }

    public void onRowEdit(RowEditEvent<Tags> event) {
        FacesMessage msg = new FacesMessage("Tag Edited", String.valueOf(event.getObject().getTagName()));
        FacesContext.getCurrentInstance().addMessage(null, msg);

        //tagsLoad.setTagName(event.getObject().getTagName());
        //tagService.saveTag(event.getObject());
        setTags(event.getObject());
        doSaveTags();
    }

    public void removeTag(){
        tagService.delete(tags);
        this.tags = null;
    }

}
