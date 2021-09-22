import java.io.*;
/**
 *
 * @author isaac.grau laura.macia
 */
class TestReadLine {

  public static final String GREEN_BACKGROUND = "\033[42m";
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\033[1;92m";

  public static void main(String[] args) {
    BufferedReader in = new EditableBufferedReader(new InputStreamReader(System.in));
    String str = null;
    try {
      str = in.readLine();
      in.close();
    } catch (IOException e) { e.printStackTrace(); }
    System.out.println(ANSI_GREEN + "\n\nLa teva línia és: " + str + ANSI_RESET + "\n");
  }
}
