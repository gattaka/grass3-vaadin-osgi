package cz.gattserver.grass3.pages.settings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.components.GridButton;
import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.window.WebWindow;

public class UsersSettingsPage extends ModuleSettingsPage {

	@Autowired
	private UserFacade userFacade;

	private Grid<UserInfoTO> grid;

	private List<UserInfoTO> users;

	public UsersSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		grid = new Grid<>();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new H2Label("Správa uživatelů"));
		usersLayout.addComponent(grid);

		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.SINGLE);

		grid.addColumn(UserInfoTO::getName, new TextRenderer()).setCaption("Jméno");
		grid.addColumn(u -> u.getRoles().toString(), new TextRenderer()).setCaption("Role");
		grid.addColumn(UserInfoTO::getRegistrationDate, new LocalDateTimeRenderer("dd.MM.yyyy"))
				.setCaption("Registrován");
		grid.addColumn(UserInfoTO::getLastLoginDate, new LocalDateTimeRenderer("dd.MM.yyyy"))
				.setCaption("Naposledy přihlášen");
		grid.addColumn(UserInfoTO::getEmail, new TextRenderer()).setCaption("Email");
		grid.addColumn(u -> u.isConfirmed() ? "Ano" : "Ne", new TextRenderer()).setCaption("Aktivní");
		users = userFacade.getUserInfoFromAllUsers();
		grid.setItems(users);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		usersLayout.addComponent(buttonLayout);

		buttonLayout.addComponent(new GridButton<>("Aktivovat", (e, user) -> {
			user.setConfirmed(true);
			userFacade.activateUser(user.getId());
			grid.getDataProvider().refreshItem(user);
		}, grid).setEnableResolver(user -> user.isPresent() && !user.get().isConfirmed()));

		buttonLayout.addComponent(new GridButton<>("Zablokovat", (e, user) -> {
			user.setConfirmed(false);
			userFacade.banUser(user.getId());
			grid.getDataProvider().refreshItem(user);
		}, grid).setEnableResolver(user -> user.isPresent() && user.get().isConfirmed()));

		buttonLayout.addComponent(new GridButton<>("Upravit oprávnění",
				(e, user) -> UI.getCurrent().addWindow(new WebWindow("Uživatelské role") {
					private static final long serialVersionUID = -2416879310811585155L;

					{
						setWidth("220px");

						for (final Role value : Role.values()) {
							final CheckBox checkbox = new CheckBox(value.getRoleName());
							checkbox.setValue(user.getRoles().contains(value));
							checkbox.addValueChangeListener(event -> {
								if (checkbox.getValue()) {
									user.getRoles().add(value);
								} else {
									user.getRoles().remove(value);
								}
							});
							addComponent(checkbox);
						}

						addComponent(new Button("Upravit oprávnění", event -> {
							userFacade.changeUserRoles(user.getId(), user.getRoles());
							grid.getDataProvider().refreshItem(user);
							close();
						}));
					}
				}), grid));

		return layout;

	}
}
