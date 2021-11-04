package noMVC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class EditableBufferedReader extends BufferedReader {
	Line line;
    //Valores que devuelve read() cuando detecta secuencias de escape
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int HOME = 2;
    public static final int END = 3;
    public static final int INS = 4;
    public static final int SUPR = 5;

    public static final int BKSP = 127;


    public EditableBufferedReader(Reader in) {
        super(in);
        line = new Line();
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
        int rd_l;
        this.setRaw();
        
        while ((rd_l = this.read()) != 13) { //ENTER es 13 en la tabla ASCII
            switch (rd_l) {
                case RIGHT:
                    if (line.right()) {
                        System.out.print("\033[C");
                    }
                    break;
                case LEFT:
                    if (line.left()) {
                        System.out.print("\033[D");
                    }                                               
                    break;
                case HOME:
                    System.out.print("\033[" + line.home() + "D"); //Movemos tantas veces a izda. como el resultado de line.home()
                    break;
                case END:
                    if(line.end() > 0){
                    System.out.print("\033[" + line.end() + "C");
                    }
                    break;
                case INS:
                    line.insert();
                    break;
                case SUPR:
                    if (line.supr()) {
                        System.out.print("\033[P");
                    }
                    break;
                case BKSP:
                    if (line.bksp()) {
                        System.out.print("\033[D"); //Movemos cursor a dcha.
                        System.out.print("\033[P"); //Insertamos espacio en blanco
                    }
                    break;
                default:
                    if (line.setLine((char) rd_l)) { //Casteamos el int para convertirlo a char
                        System.out.print("\033[@");
                        System.out.print((char) rd_l);
                    } else {
                        System.out.print((char) rd_l);
                    }

            }
        }

        this.setCooked();
        return line.getLine();

    }

}
