package cz.gattserver.grass3.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.model.domain.ContentNode;

public interface ContentNodeRepository extends JpaRepository<ContentNode, Long> {

	public Page<ContentNode> findByCreationDateNotNullOrderByCreationDateDesc(Pageable pageable);

	public Page<ContentNode> findByLastModificationDateNotNullOrderByLastModificationDateDesc(Pageable pageable);

	@Query(value = "select c from ContentNode c where ?2 = true or c.publicated = true or c.author.id = ?1")
	public Page<ContentNode> findByUserAccess(Long userId, boolean admin, Pageable pageable);

	@Query(value = "select c from ContentNode c join c.contentTags t where ?1 in t.id and (?3 = true or c.publicated = true or c.author.id = ?2) order by c.creationDate desc")
	public Page<ContentNode> findByTagAndUserAccess(Long tagId, Long userId, boolean admin, Pageable pageable);

	@Query(value = "select c from User u join u.favourites as c where u.id = ?1 and (?3 = true or c.publicated = true or c.author.id = ?2) order by c.creationDate desc")
	public Page<ContentNode> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId, boolean admin,
			Pageable pageable);

	@Query(value = "select c from Node n join n.contentNodes as c where n.id = ?1 and (?3 = true or c.publicated = true or c.author.id = ?2) order by c.creationDate desc")
	public Page<ContentNode> findByNodeAndUserAccess(Long nodeId, Long userId, boolean admin, Pageable pageable);

}
