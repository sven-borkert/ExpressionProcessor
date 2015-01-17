package net.borkert.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextHandler {

  private static final Logger log = LogManager.getLogger(ContextHandler.class);
  private static final Pattern variablePattern = Pattern.compile("([\\$\\?])\\{([\\w\\.\\*]*)\\}");
  private static final Pattern positionPattern = Pattern.compile("^WF\\.(\\d+)\\.");

  private final Map<String, String> context;

  public ContextHandler(Map<String, String> context) {
    this.context = context;
  }

  public String resolveVariables(String value) {
    StringBuffer result = new StringBuffer(value);
    Matcher matcher = variablePattern.matcher(result);
    while (matcher.find()) {
      String mode = matcher.group(1);
      String placeholder = matcher.group(2);
      String surrogate = findInContext(placeholder);
      if (surrogate == null) {
        if (mode.equals("$")) {
          throw new UnableToResolveMandatoryVariable("Unable to resolve: ${" + placeholder + "}");
        } else {
          surrogate = "";
        }
      }
      result.replace(matcher.start(0), matcher.end(0), surrogate);
      matcher.reset();
    }
    String resultString = result.toString();
    log.debug("Resolved variables: " + value + " => " + resultString);
    return resultString;
  }

  public String findInContext(String key) {
    String pattern = dosPatternToRegex(key);
    String result = null;
    List<SortItem> resultCandidates = new ArrayList<>();
    for (String cKey : context.keySet()) {
      if (cKey.matches(pattern)) {
        int pos = 0;
        Matcher matcher = positionPattern.matcher(cKey);
        if (matcher.find()) {
          pos = Integer.parseInt(matcher.group(1));
        }
        resultCandidates.add(new SortItem(context.get(cKey), pos));
      }
    }
    if (resultCandidates.size() > 0) {
      Collections.sort(resultCandidates);
      Collections.reverse(resultCandidates);
      result = resultCandidates.get(0).getValue();
    }
    return result;
  }

  public class UnableToResolveMandatoryVariable
      extends RuntimeException {

    public UnableToResolveMandatoryVariable(String message) {
      super(message);
    }

  }

  public static String dosPatternToRegex(String a) {
    return a.replaceAll("([\\^\\\\\\$\\+\\(\\)\\[\\]\\|\\.])", "\\\\$1").replaceAll("\\*", ".+").replaceAll("\\?", ".");
  }

  private class SortItem
      implements Comparable<SortItem> {

    private int pos = 0;
    private String value;

    public SortItem(String value, int pos) {
      this.pos = pos;
      this.value = value;
    }

    private int getPos() {
      return pos;
    }

    private void setPos(int pos) {
      this.pos = pos;
    }

    private String getValue() {
      return value;
    }

    private void setValue(String value) {
      this.value = value;
    }

    @Override
    public int compareTo(SortItem o) {
      return Integer.compare(getPos(), o.getPos());
    }

  }

}
