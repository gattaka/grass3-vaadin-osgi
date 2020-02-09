package cz.gattserver.grass3.books;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("booksSection")
public class BooksSection implements SectionService {

	@Resource(name = "booksPageFactory")
	private PageFactory booksPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return booksPageFactory;
	}

	public String getSectionCaption() {
		return "Knihy";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
