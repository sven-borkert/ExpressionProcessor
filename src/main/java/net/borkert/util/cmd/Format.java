package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExpressionCommand(name = "format")
public class Format
    implements ExecutableExpressionCommand {

  private static Logger log = LogManager.getLogger(Format.class);

  @RequestParameter(name = "1", type = ParameterType.DOUBLE, mandatory = true)
  public double value;

  @RequestParameter(name = "2", type = ParameterType.STRING, mandatory = true)
  private String format;

  @Override
  public String execute() throws Exception {
    java.text.DecimalFormat f = new java.text.DecimalFormat(format);
    return f.format(value);
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
}
