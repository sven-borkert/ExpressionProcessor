package net.borkert.util;

public class InvalidParameterException
    extends RuntimeException {

  private String name;
  private ParameterType type;
  private Throwable cause;

  public InvalidParameterException(String name, ParameterType type, Throwable cause) {
    this.name = name;
    this.type = type;
    this.cause = cause;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ParameterType getType() {
    return type;
  }

  public void setType(ParameterType type) {
    this.type = type;
  }

  public Throwable getCause() {
    return cause;
  }

  public void setCause(Throwable cause) {
    this.cause = cause;
  }

}
