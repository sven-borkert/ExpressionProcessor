package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;

@ExpressionCommand(name = "sum")
public class Sum
    implements ExecutableExpressionCommand {

  @RequestParameter(name = "1", type = ParameterType.INT, mandatory = true)
  public int p1;

  @RequestParameter(name = "2", type = ParameterType.INT, mandatory = true)
  public int p2;

  @Override
  public String execute() throws Exception {
    return String.valueOf(p1 + p2);
  }

  public int getP1() {
    return p1;
  }

  public void setP1(int p1) {
    this.p1 = p1;
  }

  public int getP2() {
    return p2;
  }

  public void setP2(int p2) {
    this.p2 = p2;
  }
}
