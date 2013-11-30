package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.myftp.gattserver.grass3.model.domain.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<Node, Long> {

	public List<Node> findByParentIsNull();

	public List<Node> findByParentId(Long id);

}
