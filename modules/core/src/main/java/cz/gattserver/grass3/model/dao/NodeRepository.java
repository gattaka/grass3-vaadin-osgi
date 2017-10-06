package cz.gattserver.grass3.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.model.domain.Node;

public interface NodeRepository extends JpaRepository<Node, Long> {

	public List<Node> findByParentIsNull();

	public List<Node> findByParentId(Long id);

}
