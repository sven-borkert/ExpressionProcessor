package net.borkert.util;

public class ExpressionProcessorException
    extends RuntimeException {

  public ExpressionProcessorException(String message) {
    super(message);
  }

  public ExpressionProcessorException(String message, Throwable cause) {
    super(message, cause);
  }

}
