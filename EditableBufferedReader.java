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
        static final int ESCAPE_SEC = 2999; //a partir de 2999 hem definit les escape secuences
        static final int SEC_HOME = 3000;
        static final int SEC_RIGHT = 3001;
        static final int SEC_LEFT = 3002;
        static final int SEC_FIN = 3003;
        static final int SEC_INSERT = 3004;
        static final int SEC_DELETE = 3005;


    public EditableBufferedReader(Reader in){
        super(in);
        this.linia = new Line();
        this.posicio  = 0;
        this.length = 0;
    }


    public  int read() throws IOException{

        int caracter = 0;
        caracter = super.read();
        if(caracter == ESC){
            caracter = super.read();
            if(caracter == CORCHETE){
                caracter = super.read();
                switch(caracter){
                    case RIGHT:
                        return SEC_RIGHT;

                    case LEFT:
                        return SEC_LEFT;

                    case HOME:
                        return SEC_HOME;

                    case END:
                        return SEC_FIN;

                    case INSERT:
                        caracter = super.read();
                        if(caracter == TRONCHO){
                            return SEC_INSERT;
                        }
                        return -1;

                    case DELETE:
                        caracter = this.read();
                        if(caracter == TRONCHO){
                            return SEC_DELETE;
                        }
                        return -1;

                    default:
                        return -1;

                }
            }
        }else if(caracter == BACKSPACE){
            return BACKSPACE;
        }else{
            return caracter;
        }
        return -1;
    }
    public String readLine() throws IOException{

        int caracter = 0;
        do{
            caracter = this.read();
            if(caracter >= ESCAPE_SEC){
                switch (caracter){
                    case SEC_INSERT:
                        this.linia.changeInsert();
                    break;
                    case SEC_DELETE:
                        this.linia.Delete();
                        //System.out.print("\033[1P");
                    break;
                    case SEC_FIN:
                        this.linia.Fin();
                    break;
                    case SEC_HOME:
                        this.linia.Home();
                    break;
                    case SEC_LEFT:
                        this.linia.Left();
                    break;
                    case SEC_RIGHT:
                        this.linia.Right();
                    break;
                }
            }
            else if (caracter == SEC_BACKSPACE){
                this.linia.Backspace();

            }

            else if (caracter != ENTER){
                this.linia.addCaracter(caracter);
            }
                //___DEBUG___
                int aux = this.linia.getPos()+1;                  //CODI BO
                System.out.print("\r"+this.linia.toString());    //CODI BO
                System.out.print("\033["+aux+"G");                //CODI BO
        }while(caracter != ENTER);
        this.linia.enter();
        return this.linia.toString();
    }
}
