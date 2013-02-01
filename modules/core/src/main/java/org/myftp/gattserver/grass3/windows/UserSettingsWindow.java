package org.myftp.gattserver.grass3.windows;

import java.util.List;

import org.myftp.gattserver.grass3.facades.UserFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class UserSettingsWindow extends SettingsWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private UserFacade userFacade = UserFacade.INSTANCE;

	private final Table userTable = new Table();

	/**
	 * Přehled sloupců tabulky
	 */
	private enum ColumnId {

		ID, JMÉNO, ROLE, REGISTROVÁN_OD, POSLEDNÍ_PŘIHLÁŠENÍ, AKTIVNÍ, EMAIL

	}

	public UserSettingsWindow() {
		setName("user-settings");
		setCaption("Gattserver");
	}

	@Override
	protected Component createRightColumnContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new Label("<h2>Správa uživatelů</h2>",
				Label.CONTENT_XHTML));

		usersLayout.addComponent(userTable);

		final HorizontalLayout userMenuLayout = new HorizontalLayout();
		layout.addComponent(userMenuLayout);
		userMenuLayout.setSpacing(true);

		userTable.setColumnHeader(ColumnId.POSLEDNÍ_PŘIHLÁŠENÍ,
				"POSLEDNÍ PŘIHLÁŠENÍ");
		userTable.setColumnHeader(ColumnId.REGISTROVÁN_OD, "REGISTROVÁN OD");
		userTable.setSizeFull();
		userTable.setSelectable(true);
		userTable.setImmediate(true);

		userTable.addListener(new Table.ValueChangeListener() {

			private static final long serialVersionUID = -6605391938100454104L;

			public void valueChange(ValueChangeEvent event) {
				UserInfoDTO user = (UserInfoDTO) event.getProperty().getValue();
				if (null == user) {
					userMenuLayout.setVisible(false);
				} else {
					userMenuLayout.removeAllComponents();
					userMenuLayout.addComponent(user.isConfirmed() ? createBanButton(user)
							: createActivateButton(user));
					userMenuLayout.addComponent(createSetRolesButton(user));
					userMenuLayout.setVisible(true);
				}
			}
		});

		return layout;

	}

	@Override
	protected void onShow() {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty(ColumnId.ID, String.class, null);
		container.addContainerProperty(ColumnId.JMÉNO, String.class, null);
		container.addContainerProperty(ColumnId.ROLE, String.class, null);
		container.addContainerProperty(ColumnId.REGISTROVÁN_OD, String.class,
				null);
		container.addContainerProperty(ColumnId.POSLEDNÍ_PŘIHLÁŠENÍ,
				String.class, null);
		container.addContainerProperty(ColumnId.AKTIVNÍ, String.class, null);
		container.addContainerProperty(ColumnId.EMAIL, String.class, null);
		userTable.setContainerDataSource(container);

		List<UserInfoDTO> users = userFacade.getUserInfoFromAllUsers();

		for (UserInfoDTO user : users) {
			Item item = userTable.addItem(user);
			item.getItemProperty(ColumnId.ID).setValue(user.getId());
			item.getItemProperty(ColumnId.JMÉNO).setValue(user.getName());
			item.getItemProperty(ColumnId.ROLE).setValue(user.getRoles());
			item.getItemProperty(ColumnId.REGISTROVÁN_OD).setValue(
					user.getRegistrationDate());
			item.getItemProperty(ColumnId.POSLEDNÍ_PŘIHLÁŠENÍ).setValue(
					user.getLastLoginDate());
			item.getItemProperty(ColumnId.AKTIVNÍ).setValue(user.isConfirmed());
			item.getItemProperty(ColumnId.EMAIL).setValue(user.getEmail());
		}

		super.onShow();
	}

	private Button createActivateButton(final UserInfoDTO user) {
		Button button = new Button("Aktivovat");
		button.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -6499299273236613312L;

			public void buttonClick(ClickEvent event) {
				user.setConfirmed(true);
				if (userFacade.activateUser(user)) {
					showInfo("Uživatel '" + user.getName()
							+ "' byl úspěšně aktivován");
					userTable.getContainerProperty(user, ColumnId.AKTIVNÍ)
							.setValue(user.isConfirmed());
					userTable.unselect(user);
					userTable.select(user);
				} else {
					showError("Nezdařilo se uložit úpravy provedené na uživateli '"
							+ user.getName() + "'");
				}

			}

		});
		return button;
	}

	private Button createBanButton(final UserInfoDTO user) {
		Button button = new Button("Zablokovat");
		button.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6949925629334741559L;

			public void buttonClick(ClickEvent event) {
				user.setConfirmed(false);
				if (userFacade.banUser(user)) {
					showInfo("Uživatel '" + user.getName()
							+ "' byl úspěšně zablokován");
					userTable.getContainerProperty(user, ColumnId.AKTIVNÍ)
							.setValue(user.isConfirmed());
					userTable.unselect(user);
					userTable.select(user);
				} else {
					showError("Nezdařilo se uložit úpravy provedené na uživateli '"
							+ user.getName() + "'");
				}
			}

		});
		return button;
	}

	private Button createSetRolesButton(final UserInfoDTO user) {
		Button button = new Button("Upravit oprávnění");
		button.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8422572581790539334L;

			public void buttonClick(ClickEvent event) {

				final Window subwindow = new GrassSubWindow("Uživatelské role");
				subwindow.center();
				addWindow(subwindow);
				subwindow.setWidth("220px");
				((VerticalLayout) subwindow.getContent()).setSpacing(true);

				for (final Role value : Role.values()) {
					final CheckBox checkbox = new CheckBox(value.getRoleName());
					checkbox.setValue(user.getRoles().contains(value));
					checkbox.addListener(new Button.ClickListener() {

						private static final long serialVersionUID = -4168795771060533842L;

						public void buttonClick(ClickEvent event) {
							if (checkbox.booleanValue()) {
								user.getRoles().add(value);
							} else {
								user.getRoles().remove(value);
							}
						}

					});
					subwindow.addComponent(checkbox);
				}

				Button applyBtn = new Button("Upravit oprávnění");
				applyBtn.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = -6032630714904379342L;

					public void buttonClick(ClickEvent event) {
						if (userFacade.changeUserRoles(user)) {
							showInfo("Oprávnění uživatele '" + user.getName()
									+ "' byly úspěšně upraven");
							userTable.getContainerProperty(user, ColumnId.ROLE)
									.setValue(user.getRoles());
							userTable.unselect(user);
							userTable.select(user);
						} else {
							showError("Nezdařilo se uložit úpravy provedené na uživateli '"
									+ user.getName() + "'");
						}
					}

				});
				subwindow.addComponent(applyBtn);
				subwindow.focus();
			}

		});
		return button;
	}
}
