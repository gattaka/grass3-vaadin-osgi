package cz.gattserver.grass3.books.model.dao;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;

public interface BookRepositoryCustom {

	long countBooks(BookOverviewTO filterTO);

	List<BookOverviewTO> findBooks(BookOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	BookTO findBookById(Long id);

}
