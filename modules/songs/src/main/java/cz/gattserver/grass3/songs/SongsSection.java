package cz.gattserver.grass3.songs;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("songsSection")
public class SongsSection implements SectionService {

	@Resource(name = "songsPageFactory")
	private PageFactory songsPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return songsPageFactory;
	}

	public String getSectionCaption() {
		return "Zpěvník";
	}

	@Override
	public Role[] getSectionRoles() {
		return SongsRole.values();
	}

}
