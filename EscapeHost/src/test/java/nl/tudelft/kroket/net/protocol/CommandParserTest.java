package nl.tudelft.kroket.net.protocol;

import static org.junit.Assert.*;

import java.util.HashMap;

import nl.tudelft.kroket.net.protocol.CommandParser;

import org.junit.Test;

public class CommandParserTest extends CommandParser {

  @Test
  public void testParseInputStart() {
    String input = "START";

    HashMap<String, String> parsed = CommandParser.parseInput(input);

    assertEquals(parsed.get("command"), "START");
  }

  @Test
  public void testParseInputSingleParam() {
    String input = "REGISTER[Player 1]";

    HashMap<String, String> parsed = CommandParser.parseInput(input);

    assertEquals("REGISTER", parsed.get("command"));
    assertEquals("Player 1", parsed.get("param_0"));
  }

  @Test
  public void testParseInputMultiParam() {
    String input = "A[B][C][D][E]";

    HashMap<String, String> parsed = CommandParser.parseInput(input);

    assertEquals("A", parsed.get("command"));
    assertEquals("B", parsed.get("param_0"));
    assertEquals("C", parsed.get("param_1"));
    assertEquals("D", parsed.get("param_2"));
    assertEquals("E", parsed.get("param_3"));
  }

  @Test
  public void testParseInputInvalid() {
    String input = "A[B][C]][[D[][E]]";

    HashMap<String, String> parsed = CommandParser.parseInput(input);

    assertEquals("A", parsed.get("command"));
    assertEquals("B", parsed.get("param_0"));
    assertEquals("C", parsed.get("param_1"));
    assertEquals("[D[", parsed.get("param_2"));
    assertEquals("E", parsed.get("param_3"));
  }

  @Test
  public void testParseInputEmptyCommand() {
    String input = "[A]";

    HashMap<String, String> parsed = CommandParser.parseInput(input);

    assertEquals("", parsed.get("command"));
  }

}
