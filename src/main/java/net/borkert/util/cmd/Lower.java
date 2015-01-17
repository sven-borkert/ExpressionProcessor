package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExpressionCommand(name = "lc")
public class Lower
    implements ExecutableExpressionCommand {

  private static Logger log = LogManager.getLogger(Lower.class);

  @RequestParameter(name = "1", type = ParameterType.STRING, mandatory = true)
  public String value;

  @Override
  public String execute() throws Exception {
    return value.toLowerCase();
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
