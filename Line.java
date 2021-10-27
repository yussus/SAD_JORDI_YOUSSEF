
public class Line{

    static final int SEC_BACKSPACE = 127;
    static final int ESCAPE_SEC = 2999; //a partir de 2999 hem definit les escape secuences
    static final int SEC_HOME = 3000;
    static final int SEC_RIGHT = 3001;
    static final int SEC_LEFT = 3002;
    static final int SEC_FIN = 3003;
    static final int SEC_INSERT = 3004;
    static final int SEC_DELETE = 3005;
    static final int CHARACTER = 3006;
    static final int FINAL = 3007;

    ArrayList<Integer> lineBuffer;
    Boolean insert;
    char lastChar;
    public int getPos(){
        return this.cursor;
    }
    public boolean getInsert(){
        return insert;
    }
    public char getLastChar(){
        return this.lastChar;
    }
    public Line(){
        this.insert = false;
        this.lineBuffer = new ArrayList<>();
        this.cursor = 0;
        this.length = 0;

    }

    public String toString(){
        String str = "";
        int i = 0;
        int aux = 0;
        for(i=0; i<this.lineBuffer.size(); i++){
            aux = this.lineBuffer.get(i);
            str += (char)aux;
        }
        return str;
    }
    public void removeCaracter(int pos){
        this.lineBuffer.remove(pos);
    }
    public void changeInsert(){
        this.insert = !this.insert;
        //System.out.print(ANSI_INSERT);
        this.setChanged();
    }

    public void addCaracter(int a){
        if(this.insert){
            if(this.cursor < this.lineBuffer.size()){
                this.lineBuffer.set(this.cursor, a);
            }else{
                this.lineBuffer.add(this.cursor, a);
            }
        }else{
            int i = 0;
            this.length = this.lineBuffer.size();
            if(this.cursor < this.length){
                for(i=this.length; i>this.cursor; i--){
                    this.lineBuffer.add(i,this.lineBuffer.get(i-1));
                    this.lineBuffer.remove(i-1);
                }
            }
            this.lineBuffer.add(this.cursor, a);
        }
        char aux = (char)a;
        this.lastChar = (char)a;
        this.cursor ++;
        this.setChanged();
    }

    public void Home(){
        this.cursor = 0;
        this.setChanged();
    }

    public void Fin(){
        this.cursor = this.lineBuffer.size();
        this.setChanged();
    }

    public void Delete(){
      if(this.cursor = this.lineBuffer.size()){
        this.lineBuffer.remove(this.cursor);
        this.length --;
      }
      this.setChanged();
    }

    public void Backspace(){
        if((this.cursor <= this.lineBuffer.size())&&(this.lineBuffer.size()>0) && this.cursor > 0){
            this.lineBuffer.remove(this.cursor-1);
            this.Left();
            this.length--;
        }
        this.setChanged();
    }

    public void Right(){
      if(this.cursor < this.lineBUffer.size()){
        this.cursor ++;
      }
      this.setChanged();
    }

    public void Left(){
      if(this.cursor > 0){
        this.cursor --;
      }
      this.setCanged();
    }

    public void enter(){
      this.setChanged();
    }

}
