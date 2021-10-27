import java.io.*;


public class TestReadLine {
    public static void main(String[] args) {
    BufferedReader in = new EditableBufferedReader(
      new InputStreamReader(System.in));
    String str = "";
    try {
      str = in.readLine();
    } catch (IOException e) { e.printStackTrace(); }
    System.out.println("\n\rline is: " + str);
  }
}
