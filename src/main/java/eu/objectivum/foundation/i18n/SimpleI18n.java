package eu.objectivum.foundation.i18n;

import jakarta.validation.constraints.NotNull;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.lang.System.getProperty;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Collections.addAll;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.ResourceBundle.getBundle;
import static org.apache.commons.lang3.StringUtils.appendIfMissing;

/**
 * A fairly <em>simple</em>, <a
 * href="https://en.wikipedia.org/wiki/Curiously_recurring_template_pattern">curiously recurring template
 * pattern</a>-based {@link I18n} implementation.
 * <p/>
 * Very similar in behavior to <code><a
 * href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/ResourceBundleMessageSource.html">
 * Spring's ResourceBundleMessageSource</a></code>, this class relies on the underlying JDK's {@link ResourceBundle}
 * implementation in combination with the standard message parsing provided by {@link MessageFormat}. It can be
 * configured concerning how both the {@link #setResourceBundleSupplier(BiFunction) resource bundles} and the {@link
 * #setMessageFormatSupplier(BiFunction) message formats} are created. It suffers from the same shortcomings as {@code
 * ResourceBundleMessageSource}, so a {@code ReloadableResourceBundleMessageSource} might be a better choice if
 * the application is based on Spring.
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, May 23, 2019
 * @see <a href="https://blog.joda.org/2011/08/implementations-of-interfaces-prefixes.html">Implementations of
 *   interfaces - prefixes and suffixes</a>
 * @see <a href="https://stackoverflow.com/a/23895571/272939">Answer to <em>Fluent API with inheritance and
 *   generics</em></a>
 * @see <a href="https://stackoverflow.com/a/7355094/272939">Answer to <em>Is there a way to refer to the current type
 *   with a type variable?</em></a>
 */
public class SimpleI18n<SELF extends SimpleI18n<SELF>> implements I18n {

  public static final String L10N_BASEDIR_KEY = "l10n.basedir";

  public static final String L10N_BASEDIR_DEF = "l10n/";

  protected final Set<String> bundleBaseNames = new LinkedHashSet<>(4);

  protected String bundlePrefix;

  protected Locale locale;

  protected BiFunction<String, Locale, MessageFormat> messageFormatSupplier = MessageFormat::new;

  protected BiFunction<String, Locale, ResourceBundle> resourceBundleSupplier =
    (baseName, locale) -> locale == null ? getBundle(baseName) : getBundle(baseName, locale);

  protected DateTimeFormatter dateTimeFormatter = RFC_1123_DATE_TIME;

  protected ZoneId zoneId = ZoneId.systemDefault();

  public SimpleI18n(String... bundleBaseNames) {
    setBundlePrefix(getProperty(L10N_BASEDIR_KEY, L10N_BASEDIR_DEF));
    addBundleBaseNames(bundleBaseNames);
  }

  public SELF addBundleBaseNames(String... bundleBaseNames) {
    if (bundleBaseNames != null && bundleBaseNames.length > 0) {
      addAll(this.bundleBaseNames, bundleBaseNames);
    }
    return self();
  }

  public SELF setBundleBaseNames(Collection<String> bundleBaseNames) {
    this.bundleBaseNames.clear();
    if (bundleBaseNames != null && !bundleBaseNames.isEmpty()) {
      this.bundleBaseNames.addAll(bundleBaseNames);
    }
    return self();
  }

  public SELF setBundlePrefix(String bundlePrefix) {
    this.bundlePrefix = appendIfMissing(bundlePrefix == null ? null : bundlePrefix.trim(), "/");
    return self();
  }

  public SELF setMessageFormatSupplier(BiFunction<String, Locale, MessageFormat> messageFormatSupplier) {
    this.messageFormatSupplier = messageFormatSupplier;
    return self();
  }

  public SELF setResourceBundleSupplier(BiFunction<String, Locale, ResourceBundle> resourceBundleSupplier) {
    this.resourceBundleSupplier = resourceBundleSupplier;
    return self();
  }

  public SELF setLocale(Locale locale) {
    this.locale = locale == null ? Locale.getDefault() : locale;
    return self();
  }

  public SELF setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
    this.dateTimeFormatter = dateTimeFormatter;
    return self();
  }

  public SELF setZoneId(ZoneId zoneId) {
    this.zoneId = zoneId;
    return self();
  }

  @Override
  public Locale getLocale() {return locale;}

  @Override
  public DateTimeFormatter getDateTimeFormatter() {return dateTimeFormatter;}

  @Override
  public ZoneId getZoneId() {return zoneId;}

  @NotNull
  @Override
  public String t(String key, Object... args) {
    if (key == null) {
      return "";
    }

    final Locale locale = firstNonNull(this::getLocale, this::getDefaultLocale).orElseGet(Locale::getDefault);

    final String message = message(key, locale);
    if (args == null || args.length == 0 || messageFormatSupplier == null) {
      return message;
    }

    final MessageFormat format = messageFormatSupplier.apply(message, locale);
    return format == null ? message : format.format(args);
  }

  @NotNull
  protected String message(@NotNull String key, Locale locale) {
    if (resourceBundleSupplier == null) {
      return key;
    }

    for (String baseName : bundleBaseNames) {
      if (baseName == null) {
        continue;
      }

      if (bundlePrefix != null) {
        baseName = bundlePrefix + baseName;
      }

      try {
        final ResourceBundle resourceBundle = resourceBundleSupplier.apply(baseName, locale);
        if (resourceBundle != null) {
          return resourceBundle.getString(key);
        }
      } catch (MissingResourceException ex) {
        // continue to the next bundle (base name)
      }
    }

    return key;
  }

  @NotNull
  @Override
  public String l(Instant instant) {
    if (instant == null) {
      return "";
    }

    final DateTimeFormatter dtf =
      firstNonNull(this::getDateTimeFormatter, this::getDefaultDateTimeFormatter).orElse(RFC_1123_DATE_TIME);
    final Locale locale = firstNonNull(this::getLocale, this::getDefaultLocale).orElseGet(Locale::getDefault);
    final ZoneId zoneId = firstNonNull(this::getZoneId, this::getDefaultZoneId).orElseGet(ZoneId::systemDefault);

    return dtf.withLocale(locale).format(instant.atZone(zoneId));
  }

  @NotNull
  @SafeVarargs
  protected static <T> Optional<T> firstNonNull(Supplier<T>... suppliers) {
    if (suppliers != null) {
      for (Supplier<T> supplier : suppliers) {
        if (supplier != null) {
          final T t = supplier.get();
          if (t != null) {
            return of(t);
          }
        }
      }
    }
    return empty();
  }

  /**
   * @see <a href="https://stackoverflow.com/a/23895571/272939">Answer to <em>Fluent API with inheritance and
   *   generics</em></a>
   * @see <a href="https://stackoverflow.com/a/7355094/272939">Is there a way to refer to the current type with a type
   */
  @SuppressWarnings("unchecked")
  protected final SELF self() {
    return (SELF) this;
  }
}
