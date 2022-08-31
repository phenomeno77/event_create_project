
package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Tags;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TagRepository extends AbstractRepository<Tags, Integer> {

    @Transactional
    void deleteTagsByTagId(int id);

    @Transactional
    void deleteTagsByTagName(String name);
    Tags findTagsByTagId(int id);

    @Transactional
    @Modifying
    @Query("delete from Tags where tagId = ?1")
    void deleteEntity(int id);
}

