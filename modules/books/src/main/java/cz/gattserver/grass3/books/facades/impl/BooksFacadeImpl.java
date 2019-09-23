package cz.gattserver.grass3.books.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.flow.data.provider.QuerySortOrder;

import org.springframework.data.domain.PageRequest;

import cz.gattserver.grass3.books.facades.BooksFacade;
import cz.gattserver.grass3.books.model.dao.BookRepository;
import cz.gattserver.grass3.books.model.domain.Book;
import cz.gattserver.grass3.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass3.books.model.interfaces.BookTO;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Transactional
@Component
public class BooksFacadeImpl implements BooksFacade {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);
	}

	@Override
	public int countBooks() {
		return (int) bookRepository.countBooks(null);
	}

	@Override
	public List<BookOverviewTO> getBooks(int page, int size) {
		return bookRepository.findBooks(null, PageRequest.of(page, size), null);
	}

	@Override
	public int countBooks(BookOverviewTO filterTO) {
		return (int) bookRepository.countBooks(filterTO);
	}

	@Override
	public List<BookOverviewTO> getBooks(BookOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return bookRepository.findBooks(filterTO, QuerydslUtil.transformOffsetLimit(offset, limit),
				QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public BookTO getBookById(Long id) {
		return bookRepository.findBookById(id);
	}

	private Book createBook(BookTO to) {
		Book d = new Book();
		d.setName(to.getName());
		d.setAuthor(to.getAuthor());
		d.setRating(to.getRating());
		d.setImage(to.getImage());
		d.setDescription(to.getDescription());
		d.setYear(to.getYear());
		d.setId(to.getId());
		return d;
	}

	@Override
	public BookTO saveBook(BookTO to) {
		Book book = createBook(to);
		book = bookRepository.save(book);

		to.setId(book.getId());
		return to;
	}

}
