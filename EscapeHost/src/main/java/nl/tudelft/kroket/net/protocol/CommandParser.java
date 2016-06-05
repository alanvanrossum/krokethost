package nl.tudelft.kroket.net.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

  /** Regular expression pattern to recognize parameters in commands. */
  private static Pattern paramPattern = Pattern.compile("\\[(.*?)\\]");

  /**
   * Parse a client's raw input command and parameters into a HashMap.
   * 
   * @param input
   *          the raw input
   * @return the HashMap of the command and parameters
   */
  public static HashMap<String, String> parseInput(String input) {

    HashMap<String, String> result = new HashMap<String, String>();

    // parse the command and set it in the map
    String command = parseCommand(input);
    result.put("command", command);

    // parse the parameters and set them in the map
    List<String> paramList = parseParams(input);
    int paramIndex = 0;

    for (String param : paramList) {
      result.put(String.format("param_%d", paramIndex), param);
      paramIndex += 1;
    }

    return result;
  }

  /**
   * Parse a client's command.
   * 
   * @param input
   *          the raw input
   * @return the command
   */
  public static String parseCommand(String input) {

    // find the position of the first bracket
    int bracketPosition = input.indexOf("[");

    // if bracket wasn't found, the entire input string is
    // our command
    if (bracketPosition == -1) {
      return input;
    }

    // otherwise, return the string up until the
    // bracket was found
    return input.substring(0, bracketPosition);
  }

  /**
   * Parse a client command's parameters.
   * 
   * @param input
   *          the raw input
   * @return list of all parameters
   */
  public static List<String> parseParams(String input) {
    // because there potentially could be many occurrences
    // we use regular expressions here

    List<String> paramList = new ArrayList<String>();

    Matcher matcher = paramPattern.matcher(input);
    while (matcher.find()) {
      paramList.add(matcher.group(1));
    }

    return paramList;
  }

}
