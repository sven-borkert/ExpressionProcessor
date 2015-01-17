package net.borkert.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ContextHandlerTest {

  @Test
  public void testVariableResolution() {
    Map<String, String> context = new HashMap<>();
    context.put("WF.5.ARCHIVE.TARGET.FILE.NAME", "C:\\Archive\\5.arc");
    context.put("WF.1.ARCHIVE.TARGET.FILE.NAME", "C:\\Archive\\1.arc");
    context.put("WF.3.ARCHIVE.TARGET.FILE.NAME", "C:\\Archive\\3.arc");
    context.put("WF.4.ARCHIVE.TARGET.FILE.NAME", "C:\\Archive\\4.arc");
    context.put("WF.6.ARCHIVE.TARGET.FILE.NAME", "C:\\Archive\\6.arc");
    context.put("WF.2.ARCHIVE.TARGET.FILE.NAME", "C:\\Archive\\2.arc");

    ContextHandler contextHandler = new ContextHandler(context);
    assertEquals("C:\\Archive\\6.arc", contextHandler.resolveVariables("${*.ARCHIVE.TARGET.FILE.NAME}"));
  }

}
