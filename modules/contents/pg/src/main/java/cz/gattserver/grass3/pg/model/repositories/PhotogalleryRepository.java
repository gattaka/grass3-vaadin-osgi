package cz.gattserver.grass3.pg.model.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.pg.model.domain.Photogallery;

public interface PhotogalleryRepository extends JpaRepository<Photogallery, Long> {

	@Query(value = "select count(p) from PHOTOGALLERY p where p.contentNode.publicated = true or p.contentNode.author.id = ?1")
	public int countByUserAccess(Long userId);

	@Query(value = "select p from PHOTOGALLERY p where p.contentNode.publicated = true or p.contentNode.author.id = ?1 order by p.id desc")
	public List<Photogallery> findByUserAccess(Long userId, Pageable pageable);

	@Query(value = "select count(p) from PHOTOGALLERY p where p.contentNode.publicated = true")
	public int countByAnonAccess();

	@Query(value = "select p from PHOTOGALLERY p where p.contentNode.publicated = true order by p.id desc")
	public List<Photogallery> findByAnonAccess(Pageable pageable);

	@Query(value = "select p.photogalleryPath from PHOTOGALLERY p where p.id = ?1")
	public String findPhotogalleryPathById(Long photogalleryId);

}
