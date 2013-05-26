package org.myftp.gattserver.grass3.model.dao;

import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentTagRepository extends JpaRepository<ContentTag, Long> {

	public ContentTag findByName(String name);

}
