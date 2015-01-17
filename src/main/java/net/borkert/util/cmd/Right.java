package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExpressionCommand(name = "right")
public class Right
    implements ExecutableExpressionCommand {

  private static Logger log = LogManager.getLogger(Right.class);

  @RequestParameter(name = "1", type = ParameterType.STRING, mandatory = true)
  public String value;

  @RequestParameter(name = "2", type = ParameterType.INT, mandatory = true)
  private int length;

  @Override
  public String execute() throws Exception {
    return value.substring(value.length() - length, value.length());
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }
}
