package cz.gattserver.grass3.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.model.domain.ContentTag;

public interface ContentTagRepository extends JpaRepository<ContentTag, Long>, ContentTagRepositoryCustom {

	ContentTag findByName(String name);

}
