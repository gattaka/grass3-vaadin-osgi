package cz.gattserver.grass3.books.facades;

import java.util.List;

import com.vaadin.data.provider.QuerySortOrder;

import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;

public interface BooksFacade {

	/**
	 * Smaže knihu
	 * 
	 * @param id
	 */
	void deleteBook(Long id);

	/**
	 * Získá počet knih v DB
	 * 
	 */
	int countBooks();

	/**
	 * Získá všechny knihy
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<BookOverviewTO> getBooks(int offset, int limit);

	/**
	 * Získá počet knih v DB
	 * 
	 * @param filterTO
	 */
	int countBooks(BookOverviewTO filterTO);

	/**
	 * Získá všechny knihy
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<BookOverviewTO> getBooks(BookOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá knihu dle id
	 * 
	 * @param id
	 */
	BookTO getBookById(Long id);

	/**
	 * Založ/uprav novou knihu
	 * 
	 * @param to
	 */
	BookTO saveBook(BookTO to);

}
