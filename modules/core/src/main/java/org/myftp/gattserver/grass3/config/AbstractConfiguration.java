package org.myftp.gattserver.grass3.config;

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
					value = "" + getMethod.invoke(this, args);
					items.add(new ConfigurationItem(createConfigName(field
							.getName()), value));
				} catch (Exception e) {
					continue;
				}

			}

		}

		return items;
	}

	@SuppressWarnings("unchecked")
	public static <T> T itemValueOf(String itemValue, Class<T> type)
			throws Exception {

		Method getMethod = type.getDeclaredMethod("valueOf", String.class);
		return (T) getMethod.invoke(String.class, itemValue);

	}

	public void populateConfigurationFromMap(List<ConfigurationItem> items) {

		Class<? extends AbstractConfiguration> type = this.getClass();
		for (Field field : type.getDeclaredFields()) {

			NonConfigValue annotation = field
					.getAnnotation(NonConfigValue.class);
			if (annotation != null)
				continue;

			for (ConfigurationItem item : items) {
				if (field.getName().equals(item.getName())) {

					try {
						Object[] args = { itemValueOf(item.getValue(),
								field.getType()) };

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
