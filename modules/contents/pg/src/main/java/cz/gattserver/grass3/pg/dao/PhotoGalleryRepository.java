package cz.gattserver.grass3.pg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.pg.domain.Photogallery;

public interface PhotoGalleryRepository extends JpaRepository<Photogallery, Long> {

}
