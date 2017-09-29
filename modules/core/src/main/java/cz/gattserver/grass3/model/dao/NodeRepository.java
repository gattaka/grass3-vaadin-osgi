package cz.gattserver.grass3.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.model.domain.Node;

public interface NodeRepository extends JpaRepository<Node, Long> {

	public List<Node> findByParentIsNull();

	public List<Node> findByParentId(Long id);

//	@Query("select m.c1 + m.c2 from (select (select count(n) from Node n where n.parent.id = ?1) c1, (select count(c) from ContentNode c where c.parent.id = ?1) as c2) as m")
//	public boolean isEmpty(Long id);

}
