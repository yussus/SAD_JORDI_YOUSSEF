package noMVC;

import java.util.ArrayList;

public class Line {

    private int cursor; //posición del cursor
    private boolean insert; //Modo insercción/sobreescritura
    private ArrayList<Character> line;

    //Escogemos un ArrayList porque necesitamos una lista ordenada que permita duplicados

    public Line() {
        this.cursor = 0;
        this.insert = true; //Empezamos en modo insercción (tecla insert no pulsada)
        this.line = new ArrayList<>();
    }

    public boolean setLine(char c) {

        if (insert) {  //En modo insercción 
            line.add(cursor, c);  
            cursor++;
            return true; //Para "controlar" la función readLine() de EditableBufferedReader
        } else { //En modo sobreescritura 

            if (cursor >= line.size() - 1) {
                line.add(cursor, c);  //el método add añade el carácter y desplaza los siguientes
            } else {
                line.set(cursor, c);  //el método set reemplaza el carácter anterior por el nuevo sin desplazar el resto 
            }
            cursor++;
            return false;
        }
    }

    public String getLine() { //Recorremos el string para mostrarlo por pantalla
        String linia = "";

        for (Character c : line) { 
            linia += c;
        }

        return linia;
    }

    public boolean right() {
        if (cursor < line.size()) {
            cursor++;
            return true;
        } else {
            return false;
        }
    }

    public boolean left() {
        if (cursor > 0) {
            cursor--;
            return true;
        } else {
            return false;
        }
    }

    public int home() {
        int cursorActual = cursor;
        this.cursor = 0;
        return cursorActual; //Número de índices que nos tendremos que mover a la izda.
    }

    public int end() {
        int cursorActual = cursor;
        this.cursor = line.size();
        return (line.size() - cursorActual); //Mismo que home() pero a dcha.
    }

    public void insert() {
        this.insert = !insert;
    }

    public boolean supr() {
        if (cursor < line.size()) { //Comprobamos que el cursor no está al final de la línea
            line.remove(cursor);
            return true;
        } else {
            return false;
        }
    }

    public boolean bksp() {
        if (cursor > 0) {
            line.remove(cursor - 1); //Se borra el carácter a la izda.
            cursor--; //Además en este caso el cursor también se mueve
            return true;
        } else {
            return false;
        }
    }

}
