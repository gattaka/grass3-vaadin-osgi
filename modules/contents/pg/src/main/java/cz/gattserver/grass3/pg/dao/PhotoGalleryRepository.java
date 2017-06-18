package cz.gattserver.grass3.pg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.pg.domain.Photogallery;

public interface PhotoGalleryRepository extends JpaRepository<Photogallery, Long> {

	@Query(value = "select p from Photogallery p where p.contentNode.publicated = true or p.contentNode.author.id = ?1 order by p.contentNode.creationDate desc")
	public List<Photogallery> findByUserAccess(Long userId);

}
