package siMVC;

import static ambMVC.EditableBufferedReader.*;

public class Console {
    private Line line;
    
    public Console(Line line){
        this.line = line;
    }
    
    public void output(int rd){
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
}