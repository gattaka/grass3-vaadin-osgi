package cz.gattserver.grass3.books.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.books.model.domain.Book;
import cz.gattserver.grass3.books.model.domain.QBook;
import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;
import cz.gattserver.grass3.books.model.interfaces.QBookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.QBookTO;
import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateBooks(BookOverviewTO filterTO) {
		QBook b = QBook.book;
		PredicateBuilder builder = new PredicateBuilder();
		if (filterTO != null) {
			builder.iLike(b.author, filterTO.getAuthor());
			builder.iLike(b.name, filterTO.getName());
		}
		return builder.getBuilder();
	}

	@Override
	public long countBooks(BookOverviewTO filterTO) {
		JPAQuery<Book> query = new JPAQuery<>(entityManager);
		QBook b = QBook.book;
		return query.from(b).where(createPredicateBooks(filterTO)).fetchCount();
	}

	@Override
	public List<BookOverviewTO> findBooks(BookOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order) {
		JPAQuery<BookOverviewTO> query = new JPAQuery<>(entityManager);
		QBook b = QBook.book;
		QuerydslUtil.applyPagination(pageable, query);
		return query.select(new QBookOverviewTO(b.id, b.name, b.author, b.rating, b.released)).from(b)
				.where(createPredicateBooks(filterTO)).orderBy(order).fetch();
	}

	@Override
	public BookTO findBookById(Long id) {
		JPAQuery<BookTO> query = new JPAQuery<>(entityManager);
		QBook b = QBook.book;
		return query.select(new QBookTO(b.id, b.name, b.author, b.rating, b.released, b.image, b.description)).from(b)
				.where(b.id.eq(id)).fetchOne();
	}

}
