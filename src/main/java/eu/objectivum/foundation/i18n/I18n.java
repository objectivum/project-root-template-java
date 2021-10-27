package eu.objectivum.foundation.i18n;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Entry point / façade of a primary internationalization subsystem that:
 * <ul>
 * <li>encapsulates <em>parameters</em> that play a part in consistently internationalizing the application, e.g.,
 * {@link #getLocale() locale}, {@link #getDateTimeFormatter() date/time formatter}, {@link #getZoneId() time-zone},
 * etc.</li>
 * <li>exposes a set of essential <em>primitives</em> to internationalize the application, regardless of the actual
 * implementation mechanism or framework employed ({@link java.util.ResourceBundle ResourceBundle}-based, <a
 * href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/support/ReloadableResourceBundleMessageSource.html">
 * Spring</a>-based, etc.)</li>
 * <li>employs <em>shorter names</em> for commonly used routines, e.g., {@link #t(String, Object...) t} for
 * <em>translating</em> messages, {@link #l(Instant) l} for <em>localizing</em> values like dates and numbers,
 * etc.</li>
 * </ul>
 * {@code I18n} instances can, for example, be set up/injected to represent user profile or request-specific
 * (i18n-related) configuration settings.
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, May 23, 2019
 * @see <a href="https://stackoverflow.com/a/10571144/272939">This</a> Stack Overflow answer
 */
public interface I18n {

  /**
   * @return the {@link Locale locale} currently used by {@code this} I18n implementation; defaults to
   *   {@link #getDefaultLocale()}
   */
  default Locale getLocale() {
    return getDefaultLocale();
  }

  /**
   * @return the {@link Locale locale} used by default by the application; defaults to {@code Locale.getDefault()}
   */
  default Locale getDefaultLocale() {
    return Locale.getDefault();
  }

  default DateTimeFormatter getDateTimeFormatter() {
    return getDefaultDateTimeFormatter();
  }

  default DateTimeFormatter getDefaultDateTimeFormatter() {
    return DateTimeFormatter.RFC_1123_DATE_TIME;
  }

  default ZoneId getZoneId() {
    return getDefaultZoneId();
  }

  default ZoneId getDefaultZoneId() {
    return ZoneId.systemDefault();
  }

  default @NotNull String t(Object key, Object... args) {
    return t(key == null ? null : key.toString(), args);
  }

  /**
   * If no message can be resolved for the given {@code key} and the {@link #getLocale() currently used locale}, this
   * method could return the key (if non-{@code null}) or an empty string, perhaps based on a configuration property,
   * etc.
   *
   * @return never {@code null}
   */
  @NotNull String t(String key, Object... args);

  /**
   * @return never {@code null} (at worst, an empty string)
   */
  @NotNull String l(Instant instant);
}
