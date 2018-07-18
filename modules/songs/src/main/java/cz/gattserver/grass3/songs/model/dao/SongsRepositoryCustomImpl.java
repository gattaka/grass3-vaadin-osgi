package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.songs.model.domain.QSong;
import cz.gattserver.grass3.songs.model.domain.Song;
import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;

@Repository
public class SongsRepositoryCustomImpl implements SongsRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Song> findAllOrderByName(SongOverviewTO filterTO) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(s.name, filterTO.getName());
		builder.iLike(s.author, filterTO.getAuthor());
		builder.eq(s.year, filterTO.getYear());
		return query.select(s).from(s).where(builder.getBuilder()).orderBy(new OrderSpecifier<>(Order.ASC, s.name))
				.fetch();
	}
}
