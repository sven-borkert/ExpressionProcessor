package net.borkert.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ExpressionProcessorTest {

  private static Logger log = LogManager.getLogger(ExpressionProcessorTest.class);

  private ExpressionProcessor expressionProcessor = new ExpressionProcessor();

  private char decimalSeparator = ((DecimalFormat)DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator();

  @Test
  public void functionTest() throws ExpressionProcessorException {
    String result = getExpressionProcessor().process("#[sum](2,#[sum](2,2))");
    Assert.assertEquals("6", result);
    result = getExpressionProcessor().process("#[sum](#[sum](1,1),#[sum](1,1))");
    Assert.assertEquals("4", result);
    result = getExpressionProcessor().process("#[sum](#[sum](1,1),#[multiply](6,6))");
    Assert.assertEquals("38", result);
  }

  @Test(expected = ExpressionProcessorException.class)
  public void unknownCommandTest() throws ExpressionProcessorException {
    getExpressionProcessor().process("#[sum](#[sum](1,1),#[multipl](6,6))");
  }

  @Test()
  public void timeTest() throws ExpressionProcessorException {
    String result = getExpressionProcessor().process("#[time]()");
    log.info("Time: " + result);
  }

  @Test(expected = ExpressionProcessorException.class)
  public void notTerminatedCommandTest() throws ExpressionProcessorException {
    String result = getExpressionProcessor().process("#[sum](#[sum](1,1),#[multiply](6,6)");
    log.info("Result: " + result);
  }

  @Test(expected = ExpressionProcessorException.class)
  public void notTerminatedInnerCommandTest() throws ExpressionProcessorException {
    String result = getExpressionProcessor().process("#[sum](#[sum](1,1,#[multiply](6,6))");
    log.info("Result: " + result);
  }

  @Test()
  public void variableResolverTest()
      throws ExpressionProcessorException {
    Map<String, String> context = new HashMap<>();
    context.put("SOMETHING.FORM.1.VAL", "10");
    context.put("SOMETHING.FORM.2.VAL", "15");
    context.put("ELSE.FORM.3.VAL", "2");
    String result = getExpressionProcessor().process("#[echo](${*.FORM.1.VAL})", context);
    Assert.assertEquals("10", result);
    result = getExpressionProcessor().process("#[echo](#[sum](${*.FORM.1.VAL},${*.FORM.2.VAL}))", context);
    Assert.assertEquals("25", result);
    log.info("Result: " + result);
  }

  @Test()
  public void substrTest()
      throws ExpressionProcessorException {
    Map<String, String> context = new HashMap<>();
    context.put("SOMETHING.FORM.1.VAL", "Hello world");
    String result = getExpressionProcessor().process("#[substr](${*.FORM.1.VAL},1,8)", context);
    Assert.assertEquals("ello wor", result);
    result = getExpressionProcessor().process("#[substr](${*.FORM.1.VAL},0,5)", context);
    Assert.assertEquals("Hello", result);
    result = getExpressionProcessor().process("#[substr](${*.FORM.1.VAL},6,5)", context);
    Assert.assertEquals("world", result);
  }

  @Test()
  public void leftrightTest()
      throws ExpressionProcessorException {
    Map<String, String> context = new HashMap<>();
    context.put("SOMETHING.FORM.1.VAL", "Hello world");
    String result = getExpressionProcessor().process("#[left](${*.FORM.1.VAL},4)", context);
    Assert.assertEquals("Hell", result);
    result = getExpressionProcessor().process("#[right](${*.FORM.1.VAL},5)", context);
    Assert.assertEquals("world", result);
  }

  @Test()
  public void upperlowerTest()
      throws ExpressionProcessorException {
    Map<String, String> context = new HashMap<>();
    context.put("SOMETHING.FORM.1.VAL", "Hello world");
    String result = getExpressionProcessor().process("#[uc](#[left](${*.FORM.1.VAL},4))", context);
    Assert.assertEquals("HELL", result);
    result = getExpressionProcessor().process("#[lc](#[left](${*.FORM.1.VAL},5))", context);
    Assert.assertEquals("hello", result);
  }

  @Test()
  public void formatTest()
      throws ExpressionProcessorException {
    Map<String, String> context = new HashMap<>();
    context.put("SOMETHING.FORM.1.VAL", "124"+decimalSeparator+"46");
    String result = getExpressionProcessor().process("#[format](\"${*.FORM.1.VAL}\",#####0.0000)", context);
    Assert.assertEquals("124"+decimalSeparator+"4600", result);
  }

  public ExpressionProcessor getExpressionProcessor() {
    return expressionProcessor;
  }

  public void setExpressionProcessor(ExpressionProcessor expressionProcessor) {
    this.expressionProcessor = expressionProcessor;
  }
}
