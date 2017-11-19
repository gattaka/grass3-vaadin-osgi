package cz.gattserver.grass3.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.model.domain.ContentNode;

public interface ContentNodeRepository extends JpaRepository<ContentNode, Long> {

	@Query("select c from CONTENTNODE c where (c.draft = false or c.draft is null) and (?2 = true or c.publicated = true or c.author.id = ?1)")
	Page<ContentNode> findByUserAccess(Long userId, boolean admin, Pageable pageable);

	@Query("select c from CONTENTNODE c join c.contentTags t where ?1 in t.id and (c.draft = false or c.draft is null) and (?3 = true or c.publicated = true or c.author.id = ?2) order by c.creationDate desc")
	Page<ContentNode> findByTagAndUserAccess(Long tagId, Long userId, boolean admin, Pageable pageable);

	@Query("select c from USER_ACCOUNTS u join u.favourites as c where u.id = ?1 and (c.draft = false or c.draft is null) and (?3 = true or c.publicated = true or c.author.id = ?2) order by c.creationDate desc")
	Page<ContentNode> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId, boolean admin,
			Pageable pageable);

	@Query("select c from CONTENTNODE c where c.parent.id = ?1 and (c.draft = false or c.draft is null) and (?3 = true or c.publicated = true or c.author.id = ?2) order by c.creationDate desc")
	Page<ContentNode> findByNodeAndUserAccess(Long nodeId, Long userId, boolean admin, Pageable pageable);

	@Query("select c.id from CONTENTNODE c where c.contentReaderId = ?1 and c.contentId = ?2")
	Long findIdByContentModuleAndContentId(String contentModuleId, Long contentId);

	@Modifying
	@Query("update CONTENTNODE c set c.parent.id = ?1 where c.id = ?2")
	void moveContent(Long nodeId, Long contentNodeId);
}
