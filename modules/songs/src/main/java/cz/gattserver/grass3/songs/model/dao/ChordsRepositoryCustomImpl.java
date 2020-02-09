package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.songs.model.domain.Chord;
import cz.gattserver.grass3.songs.model.domain.QChord;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;

@Repository
public class ChordsRepositoryCustomImpl implements ChordsRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Chord> findAllOrderByName(ChordTO filterTO) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QChord c = QChord.chord;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(c.name, filterTO.getName());
		return query.select(c).from(c).where(builder.getBuilder()).orderBy(new OrderSpecifier<>(Order.ASC, c.name))
				.fetch();
	}
}
