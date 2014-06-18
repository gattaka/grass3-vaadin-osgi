package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentNodeRepository extends JpaRepository<ContentNode, Long> {

	public Page<ContentNode> findByCreationDateNotNullOrderByCreationDateDesc(Pageable pageable);

	public Page<ContentNode> findByLastModificationDateNotNullOrderByLastModificationDateDesc(Pageable pageable);

	public List<ContentNode> findByAuthorId(Long userId);

}
