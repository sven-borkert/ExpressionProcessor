package net.borkert.util.cmd;

import net.borkert.util.ExecutableExpressionCommand;
import net.borkert.util.ExpressionCommand;
import net.borkert.util.ParameterType;
import net.borkert.util.RequestParameter;

import java.io.File;

@ExpressionCommand(name = "fileSize")
public class FileSize
    implements ExecutableExpressionCommand {

  @RequestParameter(name = "1", type = ParameterType.FILE, mandatory = true)
  public File file;

  @Override
  public String execute() throws Exception {
    if (file != null) {
      return Long.toString(file.length());
    }
    return "";
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }
}
