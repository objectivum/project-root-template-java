package eu.objectivum.foundation.exception;

import static eu.objectivum.foundation.exception.Severity.ERROR;
import static java.lang.Thread.currentThread;

/**
 * Convenient generic application {@link RuntimeException exception} to "throw and forget," at whatever level.
 * Many times, no further refinements (i.e., extending classes) are required. {@link InvalidInputException A}
 * {@link NotFoundException few} {@link ForbiddenException subclasses} describe situations commonly handled in
 * web-based applications (which can easily be mapped to HTTP status codes), but that might also make sense in
 * other contexts.
 * <p>
 * Try to keep the {@link #getMessage() message} concise and meaningful to the end-user!
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, Oct 3, 2021
 * @see <a href="https://victorrentea.ro/blog/exception-handling-guide-in-java/">Exception Handling Guide in Java</a>
 * @see <a href="https://victorrentea.ro/blog/presenting-exceptions-to-users/">Presenting Exceptions to Users</a>
 * @see <a href="https://reflectoring.io/spring-boot-exception-handling/">Complete Guide to Exception Handling in Spring
 *   Boot</a>
 */
public class ApplicationException extends RuntimeException {

  public static final String NO_ADDITIONAL_DETAILS_AVAILABLE_MSG =
    "An error has occurred but no additional details are currently available.";

  private Severity severity = ERROR;

  private String threadName;

  private long threadId;

  {
    final Thread currentThread = currentThread();
    threadName = currentThread.getName();
    threadId = currentThread.getId();
  }

  public ApplicationException() {}

  public ApplicationException(String message) {
    super(message);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public Severity getSeverity() {
    return severity;
  }

  public ApplicationException setSeverity(Severity severity) {
    this.severity = severity;
    return this;
  }

  public String getThreadName() {
    return threadName;
  }

  public ApplicationException setThreadName(String threadName) {
    this.threadName = threadName;
    return this;
  }

  public long getThreadId() {
    return threadId;
  }

  public ApplicationException setThreadId(long threadId) {
    this.threadId = threadId;
    return this;
  }
}
