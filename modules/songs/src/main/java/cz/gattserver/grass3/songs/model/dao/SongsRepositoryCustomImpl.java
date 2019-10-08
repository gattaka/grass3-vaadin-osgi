package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.songs.model.domain.QSong;
import cz.gattserver.grass3.songs.model.interfaces.QSongOverviewTO;
import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;

@Repository
public class SongsRepositoryCustomImpl implements SongsRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(SongOverviewTO filterTO) {
		QSong s = QSong.song;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(s.name, filterTO.getName());
		builder.iLike(s.author, filterTO.getAuthor());
		builder.eq(s.year, filterTO.getYear());
		return builder.getBuilder();
	}

	@Override
	public long count(SongOverviewTO filterTO) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		return query.from(s).where(createPredicate(filterTO)).fetchCount();
	}

	@Override
	public List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO, int offset, int limit) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		query.offset(offset).limit(limit);
		return query.select(new QSongOverviewTO(s.name, s.author, s.year, s.id)).from(s)
				.where(createPredicate(filterTO)).orderBy(new OrderSpecifier<>(Order.ASC, s.name)).fetch();
	}
}
