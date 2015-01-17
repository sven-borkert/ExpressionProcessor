package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;

@ExpressionCommand(name = "echo")
public class Echo
    implements ExecutableExpressionCommand {

  @RequestParameter(name = "1", type = ParameterType.STRING, mandatory = true)
  public String value;

  @Override
  public String execute() throws Exception {
    return value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
