package cz.gattserver.grass3.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.model.domain.Node;

public interface NodeRepository extends JpaRepository<Node, Long> {

	List<Node> findByParentIsNull();

	List<Node> findByParentId(Long id);

	@Modifying
	@Query("update NODE n set n.name = ?2 where n.id = ?1")
	void rename(Long nodeId, String newName);

	@Query("select size(n.subNodes) + size(n.contentNodes) from NODE n where n.id = ?1")
	Integer countAllSubNodes(Long nodeId);

}
