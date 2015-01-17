package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExpressionCommand(name = "substr")
public class Substr
    implements ExecutableExpressionCommand {

  private static Logger log = LogManager.getLogger(Substr.class);

  @RequestParameter(name = "1", type = ParameterType.STRING, mandatory = true)
  public String value;

  @RequestParameter(name = "2", type = ParameterType.INT, mandatory = true)
  private int offset = -1;

  @RequestParameter(name = "3", type = ParameterType.INT, mandatory = false)
  private int length = -1;

  @Override
  public String execute() throws Exception {

    if (length >= 0) {
      return value.substring(offset, offset + length);
    } else {
      return value.substring(offset);
    }
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }
}
