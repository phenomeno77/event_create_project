package at.qe.skeleton.services;

import at.qe.skeleton.model.Tags;
import at.qe.skeleton.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Service for accessing and manipulating tag data.
 */
@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    UserService userService;

    @Autowired
    LocationService locationService;

    /**
     * Returns a collection of all tags.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER','USER')")
    public Collection<Tags> getAllTags() {
        return tagRepository.findAll();
    }

    /**
     * Loads a single tag identified by its id.
     *
     * @param id the id to search for
     * @return the tag with the given id, if no such tag null
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Tags loadTag(int id) {
        Optional<Tags> loadedTag = tagRepository.findById(id);
        return loadedTag.orElse(null);
    }

    /**
     * Saves the tag.
     *
     * @param tag the tag to save
     * @return the updated tag
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Tags saveTag(Tags tag) {
        return tagRepository.save(tag);
    }

    /**
     * Deletes the tag.
     *
     * @param tag the tag to delete
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public void delete(Tags tag) {

        tag.getLocations().removeAll(tag.getLocations());
        saveTag(tag);

        tagRepository.deleteEntity(tag.getTagId());
    }

    public Tags findTagById(int id) {
        return tagRepository.findTagsByTagId(id);
    }

    /**
     * Adds a new tag to H2
     **/
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LOCATION_MANAGER', 'USER')")
    public Tags addNewTag(Tags request) {
        return saveTag(request);
    }
}
