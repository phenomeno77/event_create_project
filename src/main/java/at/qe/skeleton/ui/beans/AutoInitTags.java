package at.qe.skeleton.ui.beans;


import at.qe.skeleton.model.Tags;
import at.qe.skeleton.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;

@ManagedBean
@Scope("request")
public class AutoInitTags {
    @Autowired
    private TagService tagService;

    public void initializeTags() {
        Tags testTag1 = new Tags();
        Tags testTag2 = new Tags();
        Tags testTag3 = new Tags();
        testTag1.setTagName("testTag1");
        testTag2.setTagName("testTag2");
        testTag3.setTagName("testTag3");
        tagService.saveTag(testTag1);
        tagService.saveTag(testTag2);
        tagService.saveTag(testTag3);
    }
}
