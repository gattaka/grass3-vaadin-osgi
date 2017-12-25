package cz.gattserver.grass3.pg.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.pg.model.domain.Photogallery;

public interface PhotogalleryRepository extends JpaRepository<Photogallery, Long> {

	@Query(value = "select p from PHOTOGALLERY p where p.contentNode.publicated = true or p.contentNode.author.id = ?1 order by p.contentNode.creationDate desc")
	public List<Photogallery> findByUserAccess(Long userId);

}
