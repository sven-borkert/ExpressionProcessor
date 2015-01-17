package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ExpressionCommand(name = "time")
public class Time implements ExecutableExpressionCommand {

  @RequestParameter(name = "1", type = ParameterType.STRING, mandatory = false)
  public String format = "yyyyMMddHHmmssSSS";

  @Override
  public String execute() throws Exception {
    DateFormat formatter = new SimpleDateFormat(getFormat());
    return formatter.format(new Date());
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
}
