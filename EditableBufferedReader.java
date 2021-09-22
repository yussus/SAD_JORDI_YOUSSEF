import java.io.*;
import java.util.*;
public class EditableBufferedReader extends BufferedReader{
    Line linia;
    Console terminal;
        int posicio, length;
        static final int ESC = 27;
        static final int BACKSPACE = 127;
        static final int RIGHT = 67;
        static final int LEFT = 68;
        static final int HOME = 72;
        static final int END = 70;
        static final int INSERT = 50;
        static final int DELETE = 51;
        static final int CORCHETE = 91;
        static final int TILDE = 126; //simbol ~
        static final int INTERROGANT = 63;
        static final int ENTER = 13;

        static final int SEC_BACKSPACE = 127;
        static final int ESCAPE_SEC = 2999;
        static final int SEC_HOME = 3000;
        static final int SEC_RIGHT = 3001;
        static final int SEC_LEFT = 3002;
        static final int SEC_FIN = 3003;
        static final int SEC_INSERT = 3004;
        static final int SEC_DELETE = 3005;

    public EditableBufferedReader(Reader in){
        super(in);
        linia = new Line();
    }
    //https://qastack.mx/programming/1066318/how-to-read-a-single-char-from-the-console-in-java-as-the-user-types-it
        String[] cmd = {"/bin/sh", "-c", "stty raw -echo </dev/tty"}; 
    public void setRaw() throws IOException, InterruptedException{ 
        Runtime.getRuntime().exec(cmd).waitFor();       
}

 public void unsetRaw() throws IOException, InterruptedException{
        String[] cmd2 = {"/bin/sh", "-c", "stty cooked </dev/tty"};
        Runtime.getRuntime().exec(cmd).waitFor();        
    }