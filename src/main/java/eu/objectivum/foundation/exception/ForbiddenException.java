package eu.objectivum.foundation.exception;

/**
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, Oct 13, 2021
 * @see ApplicationException
 */
public class ForbiddenException extends ApplicationException {

  public ForbiddenException() {}

  public ForbiddenException(String message) {
    super(message);
  }

  public ForbiddenException(Throwable cause) {
    super(cause);
  }

  public ForbiddenException(String message, Throwable cause) {
    super(message, cause);
  }
}
