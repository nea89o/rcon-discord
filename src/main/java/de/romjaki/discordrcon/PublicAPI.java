package de.romjaki.discordrcon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For Intellij to leave me alone when i have public methods.
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.METHOD,
        ElementType.TYPE
})
@PublicAPI
public @interface PublicAPI {
}
