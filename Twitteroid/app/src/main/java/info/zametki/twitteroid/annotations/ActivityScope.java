package info.zametki.twitteroid.annotations;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @since 07.07.15.
 */
@Retention(RUNTIME)
@Scope
public @interface ActivityScope {
}
