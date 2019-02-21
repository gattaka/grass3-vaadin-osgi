package cz.gattserver.grass3.books.model.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;

public interface BookRepositoryCustom {

	long countBooks(BookOverviewTO filterTO);

	List<BookOverviewTO> findBooks(BookOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order);

	BookTO findBookById(Long id);

}
