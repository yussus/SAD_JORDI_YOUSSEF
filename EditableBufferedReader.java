import java.io.*;
import java.util.*;
public class EditableBufferedReader extends BufferedReader { // CONTROLLER

    private Line line;
    private Console console;

    public EditableBufferedReader(Reader in) {
        super(in);
        this.line = new Line(); // Model
        this.console = new Console(); // View
        this.line.addObserver(this.console);
    }

    // Metodes
    // setRaw: passa la consola de mode cooked a mode raw.
    public void setRaw() {
        String[] cmd = { "/bin/sh", "-c", "stty -echo raw </dev/tty" }; // Comana per canviar la consola a mode Raw
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
            if (p.waitFor() == 1) {
                p.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // unsetRaw: passa la consola de mode raw a mode cooked.
    public void unsetRaw() {
        String[] cmd = { "/bin/sh", "-c", "stty echo cooked </dev/tty" }; // Comana per canviar la consola a mode Cooked
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
            if (p.waitFor() == 1) {
                p.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // read: llegeix el seguent caracter o la seguent tecla de cursor.
    public int read() {
        int numRead = 6666;
        try {
            int n = super.read();
            if (n != Diccionari.ESC) { // Si entra es un caracter normal o borrar(Backspace)
                if (n == Diccionari.BS) {
                    numRead = Diccionari.xBS;
                } else {
                    numRead = n;
                }
            } else { // Si entra detectem que es una sequencia de ESC
                n = super.read(); // El CSI el podem obviar i fer condicions del qe hi ha darrere de ESC+CSI
                n = super.read();
                switch (n) {
                case Diccionari.DEL1:
                    int n1 = super.read();
                    if (n1 == Diccionari.DEL2) {
                        numRead = Diccionari.xDEL;
                    }
                    break;
                case Diccionari.INSERT1:
                    numRead = Diccionari.xINSERT;
                    break;
                case Diccionari.RIGHT:
                    numRead = Diccionari.xRIGHT;
                    break;
                case Dictionary.LEFT:
                    numRead = Diccionari.xLEFT;
                    break;
                case Diccionari.HOME:
                    numRead = Diccionari.xHOME;
                    break;
                case Diccionari.END:
                    numRead = Diccionari.xEND;
                    break;
                default:
                    System.err.println("Invalid input!!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numRead;
    }

    public String readLine() {
        this.setRaw();
        int llegit = this.read();
        while (llegit != Diccionari.ENTER_1 && llegit != Diccionari.ENTER_2) {
            switch (llegit) {
            case Diccionari.xBS:
                if (line.getPosCursor() > 0) {
                    line.backspace();
                }
                break;
            case Diccionari.xDEL:
                if (line.getPosCursor() < line.getLength()) {
                    line.delete();
                }
                break;
            case Diccionari.xEND:
                line.moveEnd();
                break;
            case Diccionari.xHOME:
                line.moveHome();
                break;
            case Diccionari.xLEFT:
                if (line.getPosCursor() != 0) {
                    line.moveLeft();
                }
                break;
            case Diccionari.xRIGHT:
                if (line.getPosCursor() < line.getLength()) {
                    line.moveRight();
                }
                break;
            case Dictionary.xINSERT:
                line.insertMode();
                break;
            default:
                if (line.getMode()) { // Distinct mode insert
                    line.insertChar((char) llegit);
                } else {
                    line.replaceChar((char) llegit);
                }
                break;
            }
            llegit = this.read();
        }
        this.unsetRaw();
        return line.getLine().toString();
    }
}
