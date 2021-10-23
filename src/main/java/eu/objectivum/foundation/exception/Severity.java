package eu.objectivum.foundation.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.text.CaseUtils;

/**
 * Sometimes, errors should be flagged as warnings.
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, Oct 3, 2021
 */
public enum Severity {

  WARNING,
  ERROR;

  private final String toCamelCase = CaseUtils.toCamelCase(name(), true, '_');

  @JsonValue
  public String toCamelCase() {
    return toCamelCase;
  }
}
