package org.myftp.gattserver.grass3.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.model.domain.ConfigurationItem;

public abstract class AbstractConfiguration {

	private String prefix;

	public AbstractConfiguration(String prefix) {
		this.prefix = prefix;
	}

	private String createConfigName(String name) {
		return prefix + "." + name;
	}

	private String createMethodName(String prefix, String fieldName) {
		return prefix + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
	}

	private String createGetMethodName(String fieldName) {
		return createMethodName("get", fieldName);
	}

	private String createIsMethodName(String fieldName) {
		return createMethodName("is", fieldName);
	}

	private String createSetMethodName(String fieldName) {
		return createMethodName("set", fieldName);
	}

	public List<ConfigurationItem> getConfigurationItems() {

		List<ConfigurationItem> items = new ArrayList<ConfigurationItem>();

		Class<? extends AbstractConfiguration> type = this.getClass();
		for (Field field : type.getDeclaredFields()) {
			NonConfigValue annotation = field
					.getAnnotation(NonConfigValue.class);

			if (annotation == null) {

				String value = null;
				Object[] args = {};
				Class<?>[] params = {};
				try {
					Method getMethod = null;
					try {
						getMethod = type.getDeclaredMethod(
								createGetMethodName(field.getName()), params);
					} catch (NoSuchMethodException e) {
						getMethod = type.getDeclaredMethod(
								createIsMethodName(field.getName()), params);
					}
					value = StringSerializer.serialize((Serializable) getMethod
							.invoke(this, args));
					items.add(new ConfigurationItem(createConfigName(field
							.getName()), value));
				} catch (Exception e) {
					continue;
				}

			}

		}

		return items;
	}

	public void populateConfigurationFromMap(List<ConfigurationItem> items) {

		Class<? extends AbstractConfiguration> type = this.getClass();
		for (Field field : type.getDeclaredFields()) {

			NonConfigValue annotation = field
					.getAnnotation(NonConfigValue.class);
			if (annotation != null)
				continue;

			for (ConfigurationItem item : items) {
				int subindex = (getPrefix().length() + 1) >= item.getName()
						.length() ? 0 : getPrefix().length() + 1;
				if (field.getName().equals(item.getName().substring(subindex))) {

					try {
						Object[] args = { StringSerializer.deserialize(
								item.getValue(), field.getType()) };

						Class<?>[] params = { field.getType() };
						Method setMethod = null;
						setMethod = type.getDeclaredMethod(
								createSetMethodName(field.getName()), params);
						setMethod.invoke(this, args);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}

				}
			}

		}

	}

	public String getPrefix() {
		return prefix;
	}

}
