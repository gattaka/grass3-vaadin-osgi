package org.myftp.gattserver.grass3.model;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.model.config.ModelConfiguration;
import org.myftp.gattserver.grass3.model.service.IEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("entityServicesAggregator")
public class EntityServicesAggregator {

	/**
	 * Hibernate {@link Configuration}
	 */
	private Configuration configuration;

	/**
	 * Hibernate {@link SessionFactory}
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(EntityServicesAggregator.class);

	@Autowired
	public EntityServicesAggregator(List<IEntityService> services) {

		/**
		 * Získej konfigurace
		 */
		ModelConfiguration modelConfiguration = null;
		try {
			modelConfiguration = loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		/**
		 * Konfigurace hibernate
		 */
		configuration = new Configuration();

		configuration.setProperty("hibernate.connection.driver_class",
				Driver.class.getName());
		configuration.setProperty("hibernate.connection.url",
				modelConfiguration.getURL());
		configuration.setProperty("hibernate.connection.username",
				modelConfiguration.getUsername());
		configuration.setProperty("hibernate.connection.password",
				modelConfiguration.getPassword());
		configuration.setProperty("hibernate.connection.pool_size", "1");
		configuration.setProperty("hibernate.dialect",
				H2Dialect.class.getName());
		configuration.setProperty("hibernate.current_session_context_class",
				"thread");
		configuration.setProperty("hibernate.show_sql", "true");
		configuration.setProperty("hibernate.hbm2ddl.auto", "update");

		/**
		 * Zde se přidávají třídy, které budou persistovány
		 */
		for (IEntityService service : services) {
			for (Class<?> entityClass : service.getDomainClasses()) {
				configuration.addAnnotatedClass(entityClass);
				logger.info("EntityClass " + entityClass.getName()
						+ " registred");
			}
		}
		
		/**
		 * Vytvoří SessionFactory na základě konfigurace 
		 */
		ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder();
		serviceRegistryBuilder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = serviceRegistryBuilder
				.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);

	}

	/**
	 * Získá aktuální konfiguraci ze souboru konfigurace
	 * 
	 * @return soubor konfigurace FM
	 * @throws JAXBException
	 */
	private ModelConfiguration loadConfiguration() throws JAXBException {
		return new ConfigurationUtils<ModelConfiguration>(
				new ModelConfiguration(), ModelConfiguration.CONFIG_PATH)
				.loadExistingOrCreateNewConfiguration();
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
