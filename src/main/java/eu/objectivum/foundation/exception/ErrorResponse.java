package eu.objectivum.foundation.exception;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.format.DateTimeFormatter;

import static eu.objectivum.foundation.exception.ApplicationException.NO_ADDITIONAL_DETAILS_AVAILABLE_MSG;
import static eu.objectivum.foundation.exception.Severity.ERROR;
import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

/**
 * Models <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807 Problem Details for HTTP APIs</a> error
 * detail payloads.
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, Oct 3, 2021
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">Problem Details for HTTP APIs</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ErrorResponse {

  public static final DateTimeFormatter ER_DATE_TIME_FORMATTER_DEF = ISO_OFFSET_DATE_TIME.withZone(systemDefault());

  public static final String ER_TYPE_PREFIX_DEF = "https://objectivum.eu/errors/";

  public static final String ER_TYPE_DEF = ER_TYPE_PREFIX_DEF + "generic";

  public static String defaultType(Object object) {
    return defaultType(object == null ? null : object.getClass());
  }

  public static String defaultType(Class<?> clazz) {
    return clazz == null
      ? ER_TYPE_DEF
      : ER_TYPE_PREFIX_DEF +
        stream(splitByCharacterTypeCamelCase(clazz.getSimpleName())).map(String::toLowerCase).collect(joining("-"));
  }

  private String type = ER_TYPE_DEF;

  private String title = ERROR.toCamelCase();

  private String detail = NO_ADDITIONAL_DETAILS_AVAILABLE_MSG;

  private String instance;

  private String timestamp = ER_DATE_TIME_FORMATTER_DEF.format(now());

  private Severity severity = ERROR;
}
