package siMVC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class EditableBufferedReaderMVC extends BufferedReader {

    //Valores que devuelve read() cuando detecta secuencias de escape
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int HOME = 2;
    public static final int END = 3;
    public static final int INS = 4;
    public static final int SUPR = 5;
    public static final int BKSP = 127;

    protected LineMVC line;
	protected ConsoleMVC console;
        
	public EditableBufferedReaderMVC(Reader reader) {
		super(reader);
		line = new LineMVC();
		console = new ConsoleMVC(line);
		line.addObserver(console);
	}

    public void setRaw() throws IOException {
        String[] command = {"/bin/bash", "-c", "stty -echo raw </dev/tty"};
        Runtime.getRuntime().exec(command);
    }

    public void setCooked() throws IOException { //Con "stty cooked no funciona correctamente el terminal
        String[] command = {"/bin/bash", "-c", "stty sane </dev/tty"};
        Runtime.getRuntime().exec(command);

    }

    /*
    El método read() tiene que tratar de forma especial las siguientes 
    secuencias de escape:
    
    RightArrow --> CSI C
    LeftArrow --> CSI D
    Home --> SS3 H
    End --> SS3 F
    Ins --> CSI 2 ~
    Supr --> CSI 3 ~     
    
     */
    @Override
    public int read() throws IOException {
        int rd;

        switch (rd = super.read()) { 
            case '\033': //Inicio de seq. de escape en octal
                super.read(); //Ignoramos el [

                switch (super.read()) {
                    case 'C':
                        return RIGHT;
                    case 'D':
                        return LEFT;
                    case 'H':
                        return HOME;
                    case 'F':
                        return END;
                    case '2':
                        super.read(); //Ignoramos el ~
                        return INS;
                    case '3':
                        super.read();
                        return SUPR;
                }

            default: //Para cualquier otro caso devolvemos directamente el valor leído
                return rd;

        }
    }


    @Override
    public String readLine() throws IOException {
        this.setRaw();

        int pls;
        while ((pls = this.read()) != 13) {
            console.output(pls);
        }
        
        this.setCooked();
        return line.getLine();
    }

    


}