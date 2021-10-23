package eu.objectivum.foundation.exception;

/**
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, Oct 13, 2021
 * @see ApplicationException
 */
public class InvalidInputException extends ApplicationException {

  public InvalidInputException() {}

  public InvalidInputException(String message) {
    super(message);
  }

  public InvalidInputException(Throwable cause) {
    super(cause);
  }

  public InvalidInputException(String message, Throwable cause) {
    super(message, cause);
  }
}
