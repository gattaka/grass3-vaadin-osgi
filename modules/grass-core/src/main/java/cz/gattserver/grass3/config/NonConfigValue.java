package cz.gattserver.grass3.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Anotace označující pole, které není součástí konfigurace
 * 
 * @author Gattaka
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NonConfigValue {
}
