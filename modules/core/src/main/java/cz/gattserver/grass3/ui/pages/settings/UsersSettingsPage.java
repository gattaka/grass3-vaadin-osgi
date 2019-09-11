package cz.gattserver.grass3.ui.pages.settings;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.modules.register.ModuleRegister;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.ui.components.GridButton;
import cz.gattserver.web.common.ui.window.WebDialog;

public class UsersSettingsPage extends AbstractSettingsPage {

	@Autowired
	private UserService userFacade;

	@Lazy
	@Autowired
	protected ModuleRegister moduleRegister;

	private Grid<UserInfoTO> grid;

	public UsersSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		grid = new Grid<>();

		VerticalLayout layout = new VerticalLayout();

		layout.setPadding(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.add(usersLayout);

		usersLayout.add(new H2("Správa uživatelů"));
		usersLayout.add(grid);

		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.SINGLE);

		grid.addColumn(UserInfoTO::getName).setHeader("Jméno");
		grid.addColumn(u -> u.getRoles().stream().map(Role::getRoleName).collect(Collectors.joining(", ")))
				.setHeader("Role");
		grid.addColumn(new LocalDateTimeRenderer<>(UserInfoTO::getRegistrationDate, "dd.MM.yyyy"))
				.setHeader("Registrován");
		grid.addColumn(new LocalDateTimeRenderer<>(UserInfoTO::getLastLoginDate, "dd.MM.yyyy"))
				.setHeader("Naposledy přihlášen");
		grid.addColumn(UserInfoTO::getEmail).setHeader("Email");
		grid.addColumn(u -> u.isConfirmed() ? "Ano" : "Ne").setHeader("Aktivní");

		List<UserInfoTO> users = userFacade.getUserInfoFromAllUsers();
		grid.setItems(users);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		usersLayout.add(buttonLayout);

		buttonLayout.add(new GridButton<>("Aktivovat", u -> users.forEach(user -> {
			user.setConfirmed(true);
			userFacade.activateUser(user.getId());
			grid.getDataProvider().refreshItem(user);
		}), grid).setEnableResolver(u -> !users.iterator().next().isConfirmed()));

		buttonLayout.add(new GridButton<>("Zablokovat", u -> users.forEach(user -> {
			user.setConfirmed(false);
			userFacade.banUser(user.getId());
			grid.getDataProvider().refreshItem(user);
		}), grid).setEnableResolver(u -> users.iterator().next().isConfirmed()));

		buttonLayout.add(new GridButton<>("Upravit oprávnění", u -> {
			WebDialog w = new WebDialog("Uživatelské role");

			UserInfoTO user = users.iterator().next();
			w.setWidth("220px");

			for (final Role role : moduleRegister.getRoles()) {
				final Checkbox checkbox = new Checkbox(role.getRoleName());
				checkbox.setValue(user.getRoles().contains(role));
				checkbox.addValueChangeListener(event -> {
					if (checkbox.getValue()) {
						user.getRoles().add(role);
					} else {
						user.getRoles().remove(role);
					}
				});
				w.addComponent(checkbox);
			}

			w.addComponent(new Button("Upravit oprávnění", event -> {
				userFacade.changeUserRoles(user.getId(), user.getRoles());
				grid.getDataProvider().refreshItem(user);
				w.close();
			}));
			w.open();
		}, grid));

		return layout;

	}
}
