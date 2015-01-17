package net.borkert.util;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExpressionProcessor {

  private static Logger log = LogManager.getLogger(ExpressionProcessor.class);

  private static DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
  private Map<String,Class> commandMap = new HashMap<>();

  public ExpressionProcessor() {
    scanForCommandPlugins();
  }

  private void scanForCommandPlugins() {
    AnnotationDB db = new AnnotationDB();
    for (URL u : ClasspathUrlFinder.findClassPaths()) {
      try {
        db.scanArchives(u);
      } catch (IOException ex) {
      }
    }
    Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
    Set<String> expressionCommands = annotationIndex.get(ExpressionCommand.class.getName());
    for(String cmd : expressionCommands){
      try {
        String cmdName = Class.forName(cmd).getAnnotation(ExpressionCommand.class).name();
        commandMap.put(cmdName, Class.forName(cmd));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public String process(String input)
      throws ExpressionProcessorException {
    return process(input, null);
  }

  public String process(String input, Map<String, String> context)
      throws ExpressionProcessorException {

    log.debug("process: " + input);

    if (context != null) {
      ContextHandler contextHandler = new ContextHandler(context);
      input = contextHandler.resolveVariables(input);
    }

    if (input == null) {
      return "";
    }

    char[] inputChars = input.toCharArray();
    StringBuilder result = new StringBuilder();
    StringBuilder command = new StringBuilder();
    StringBuilder commandParameters = new StringBuilder();
    boolean inCommand = false;
    boolean inParameters = false;
    boolean justExecuted = false;
    int depth = 0;

    for (int i = 0; i < inputChars.length; i++) {
      if (inputChars[i] == '#' && !inCommand) {
        if (inputChars.length > i + 1 && inputChars[i + 1] == '[') {
          inCommand = true;
        }
        continue;
      }
      if (inputChars[i] == '(' && inCommand) {
        if (depth++ == 0) {
          inParameters = true;
          continue;
        }
      }
      if (inputChars[i] == ')') {
        if (inCommand && inParameters && --depth == 0) {
          inParameters = false;
          inCommand = false;
          String cmd = command.toString().replaceAll("[\\[\\]]", "");
          log.debug("Command: " + cmd + " Parameters: " + commandParameters.toString());
          if (commandParameters.indexOf("#[") >= 0) {
            result.append(execute(cmd, process(commandParameters.toString(), context)));
          } else {
            result.append(execute(cmd, commandParameters.toString()));
          }
          command = new StringBuilder();
          commandParameters = new StringBuilder();
          justExecuted = true;
        }
      }
      if (!inCommand && !justExecuted) {
        result.append(inputChars[i]);
      } else {
        if (!justExecuted) {
          if (inParameters) {
            commandParameters.append(inputChars[i]);
          } else {
            command.append(inputChars[i]);
          }
        } else {
          justExecuted = false;
        }
      }
    }
    if (inCommand) {
      throw new ExpressionProcessorException("Command not terminated: " + command);
    }
    return result.toString();
  }

  protected ExecutableExpressionCommand getCommandInstance(String name)
      throws ExpressionProcessorException {
    if (!commandMap.containsKey(name)) {
      throw new ExpressionProcessorException("Command not available: " + name + " ("+name+")");
    }
    try {
      return (ExecutableExpressionCommand) commandMap.get(name).newInstance();
    } catch (Exception ex) {
      throw new ExpressionProcessorException("Failure instantiating: "+commandMap.get(name).getName(),ex);
    }
  }

  public String execute(String command, String parameters)
      throws ExpressionProcessorException {

    log.debug("execute(" + command + ",\"" + parameters + "\")");

    try {
      ExecutableExpressionCommand c = getCommandInstance(command);

      CSVReader r = new CSVReader(new StringReader(parameters), ',');
      String[] parameterArray = r.readNext();
      r.close();

      if (parameterArray == null) {
        parameterArray = new String[0];
      }

      for (Field field : c.getClass().getDeclaredFields()) {

        RequestParameter rp = field.getAnnotation(RequestParameter.class);
        if (rp == null) {
          log.debug("Field without RequestParameter annotation. Skipping: " + field.getName());
          continue;
        }

        int parameterIndex;
        try {
          parameterIndex = Integer.parseInt(rp.name());
        } catch (NumberFormatException ex) {
          log.error("execute(): NumberFormatException: " + ex.getMessage());
          throw new ExpressionProcessorException("Invalid parameter name: " + rp.name());
        }

        if (parameterArray.length < parameterIndex && rp.mandatory()) {
          throw new ExpressionProcessorException("Command error. No parameter available with index: " + parameterIndex);
        }

        if (parameterArray.length >= parameterIndex) {
          String pRaw = parameterArray[parameterIndex - 1];

          log.debug("Index: " + parameterIndex + " Value: " + pRaw);

          if (rp.type() == ParameterType.STRING) {
            if (pRaw == null) {
              PropertyUtils.setProperty(c, field.getName(), "");
            } else {
              PropertyUtils.setProperty(c, field.getName(), pRaw);
            }
          }
          if (rp.type() == ParameterType.INT) {
            try {
              if (pRaw == null) {
                PropertyUtils.setProperty(c, field.getName(), 0);
              } else {
                Integer i = Integer.parseInt(pRaw);
                PropertyUtils.setProperty(c, field.getName(), i);
              }
            } catch (NumberFormatException ex) {
              throw new InvalidParameterException(rp.name(), rp.type(), ex);
            }
          }
          if (rp.type() == ParameterType.LONG) {
            try {
              if (pRaw == null) {
                PropertyUtils.setProperty(c, field.getName(), 0);
              } else {
                Long l = Long.parseLong(pRaw);
                PropertyUtils.setProperty(c, field.getName(), l);
              }
            } catch (NumberFormatException ex) {
              throw new InvalidParameterException(rp.name(), rp.type(), ex);
            }
          }
          if (rp.type() == ParameterType.DOUBLE) {
            try {
              if (pRaw == null) {
                PropertyUtils.setProperty(c, field.getName(), 0);
              } else {
                Double d = new java.text.DecimalFormat("0.00").parse(pRaw).doubleValue();
                PropertyUtils.setProperty(c, field.getName(), d);
              }
            } catch (NumberFormatException ex) {
              throw new InvalidParameterException(rp.name(), rp.type(), ex);
            }
          }
          if (rp.type() == ParameterType.BOOL) {
            if (pRaw == null) {
              PropertyUtils.setProperty(c, field.getName(), false);
            } else {
              Boolean b = Boolean.parseBoolean(pRaw);
              PropertyUtils.setProperty(c, field.getName(), b);
            }
          }
          if (rp.type() == ParameterType.DATE) {
            try {
              if (pRaw == null) {
                PropertyUtils.setProperty(c, field.getName(), null);
              } else {
                Date date = formatter.parse(pRaw);
                PropertyUtils.setProperty(c, field.getName(), date);
              }
            } catch (ParseException ex) {
              throw new InvalidParameterException(rp.name(), rp.type(), ex);
            }
          }
          if (rp.type() == ParameterType.FILE) {
            if (pRaw == null) {
              PropertyUtils.setProperty(c, field.getName(), null);
            } else {
              PropertyUtils.setProperty(c, field.getName(), new File(pRaw));
            }
          }
        }
      }
      log.debug("Executing: " + command);
      String result = c.execute();
      log.debug("Returns: " + result);
      return result;

    } catch (Exception ex) {
      if (ex instanceof ExpressionProcessorException) {
        throw (ExpressionProcessorException) ex;
      }
      ex.printStackTrace();
      throw new ExpressionProcessorException("Error in command processing: " + ex.getMessage());
    }
  }

}
