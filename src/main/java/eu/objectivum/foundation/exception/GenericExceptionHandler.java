package eu.objectivum.foundation.exception;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.Thread.UncaughtExceptionHandler;

import static eu.objectivum.foundation.exception.ApplicationException.NO_ADDITIONAL_DETAILS_AVAILABLE_MSG;
import static eu.objectivum.foundation.exception.ErrorResponse.defaultType;
import static eu.objectivum.foundation.exception.Severity.ERROR;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A generic exception handler that conveniently implements {@link UncaughtExceptionHandler},
 * can be invoked on its own or integrated with a framework's exception handling mechanism like
 * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/ResponseEntityExceptionHandler.html">
 * Spring's {@code ResponseEntityExceptionHandler}</a>, etc.
 * <p>
 * The result of the {@code handle()} methods is especially suited to web-based applications
 * or services that provide feedback to web UIs.
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 1.0, Oct 3, 2021
 * @see <a href="https://victorrentea.ro/blog/exception-handling-guide-in-java/">Exception Handling Guide in Java</a>
 * @see <a href="https://victorrentea.ro/blog/presenting-exceptions-to-users/">Presenting Exceptions to Users</a>
 * @see <a href="https://reflectoring.io/spring-boot-exception-handling/">Complete Guide to Exception Handling in Spring
 *   Boot</a>
 */
@Slf4j
public class GenericExceptionHandler implements UncaughtExceptionHandler {

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    handle(thread, throwable);
  }

  @NotNull
  public ErrorResponse handle(Thread thread, Throwable throwable) {

    Severity severity = null;
    if (throwable instanceof final ApplicationException applicationException) {
      severity = applicationException.getSeverity();
    }
    if (severity == null) {
      severity = ERROR;
    }

    // 1. Log the exception, including its stack trace

    if (throwable == null) {
      log.error("null");
    } else {
      switch (severity) {
      case WARNING -> log.warn("", throwable);
      case ERROR, default -> log.error("", throwable);
      }
    }

    // 2. Return an appropriate error response

    String detail = throwable == null ? null : throwable.getMessage();
    if (detail == null && throwable != null) {
      // Single, don't-go-up-the-full-stack attempt to find a relevant message
      final Throwable cause = throwable.getCause();
      if (cause != null) {
        detail = cause.getMessage();
      }
    }
    if (isBlank(detail)) {
      detail = NO_ADDITIONAL_DETAILS_AVAILABLE_MSG;
    }

    return new ErrorResponse()
      .setType(defaultType(throwable))
      .setTitle(severity.toCamelCase())
      .setDetail(detail)
      .setSeverity(severity);
  }

  @NotNull
  public ErrorResponse handle(Throwable throwable) {
    return handle(null, throwable);
  }
}
