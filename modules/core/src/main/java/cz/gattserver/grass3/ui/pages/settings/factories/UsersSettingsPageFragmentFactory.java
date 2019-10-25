package cz.gattserver.grass3.ui.pages.settings.factories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.modules.register.ModuleRegister;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.WebDialog;

public class UsersSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private UserService userFacade;

	@Lazy
	@Autowired
	protected ModuleRegister moduleRegister;

	private Grid<UserInfoTO> grid;

	@Override
	public void createFragment(Div layout) {
		grid = new Grid<>();

		layout.add(new H2("Správa uživatelů"));
		layout.add(grid);

		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
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
		buttonLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		buttonLayout.setSpacing(true);
		layout.add(buttonLayout);

		GridButton<UserInfoTO> activateBtn = new GridButton<>("Aktivovat", selectedUsers -> selectedUsers.forEach(u -> {
			u.setConfirmed(true);
			userFacade.activateUser(u.getId());
			grid.getDataProvider().refreshItem(u);
		}), grid).setEnableResolver(
				selectedUsers -> !selectedUsers.isEmpty() && !selectedUsers.iterator().next().isConfirmed());
		activateBtn.setIcon(new Image(ImageIcon.TICK_16_ICON.createResource(), "Aktivovat"));
		buttonLayout.add(activateBtn);

		GridButton<UserInfoTO> blockBtn = new GridButton<>("Zablokovat", selectedUsers -> users.forEach(user -> {
			user.setConfirmed(false);
			userFacade.banUser(user.getId());
			grid.getDataProvider().refreshItem(user);
		}), grid).setEnableResolver(
				selectedUsers -> !selectedUsers.isEmpty() && selectedUsers.iterator().next().isConfirmed());
		blockBtn.setIcon(new Image(ImageIcon.BLOCK_16_ICON.createResource(), "Zablokovat"));
		buttonLayout.add(blockBtn);

		GridButton<UserInfoTO> editBtn = new GridButton<>("Upravit oprávnění", u -> {
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
		}, grid);
		editBtn.setIcon(new Image(ImageIcon.PENCIL_16_ICON.createResource(), "Upravit oprávnění"));
		buttonLayout.add(editBtn);
	}
}
