package net.borkert.util;

import net.borkert.util.cmd.Echo;
import org.junit.Assert;
import org.junit.Test;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class PluginScannerTest {

  @Test
  public void scanForPluginsTest()
      throws Exception {
    AnnotationDB db = new AnnotationDB();
    for (URL u : ClasspathUrlFinder.findClassPaths()) {
      try {
        db.scanArchives(u);
      } catch (IOException ex) {
      }
    }
    Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
    Set<String> entities = annotationIndex.get(ExpressionCommand.class.getName());
    Assert.assertTrue(entities.contains(Echo.class.getName()));
    Assert.assertEquals("echo",Class.forName(Echo.class.getName()).getAnnotation(ExpressionCommand.class).name());
  }

}
