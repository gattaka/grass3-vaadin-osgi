package cz.gattserver.grass3.books.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.books.model.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

}
