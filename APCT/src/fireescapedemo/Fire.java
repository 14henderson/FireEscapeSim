package fireescapedemo;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Fire extends Actor {
    private ArrayList<Particle> particals;
    private final int MAX_PARTICALS= 20;
    private Random rand;
    private int x,y,size;
    public Fire(int x, int y, Tile tile) {
        super(new Pane(), tile);
        this.x = x; this.y = y; this.size =20;
        this.view.setLayoutX(this.x);
        this.view.setLayoutY(this.y);
        this.particals = new ArrayList<>();
        this.rand = new Random();
        int i;
        for(i = 0; i < this.MAX_PARTICALS; i++){this.addPartical();}
    }

    private boolean lock = false;

    public void update(Floor floor){
        if(!lock){
            lock = true;
            for(Particle part : this.particals){
                if(!part.isExpired()){
                    part.update(floor);
                }else{
                    part.reset(this.y + this.size);
                }
            }
            lock = false;
        }
    }


    public final ArrayList<Particle> getParticals(){return this.particals;}
    public final int getMAX_PARTICALS(){return this.MAX_PARTICALS;}

    public final boolean addToPane(Particle p) {return ((Pane)(this.view)).getChildren().add(p.view);}
    public void addPartical(){
        int pos = this.rand.nextInt(size) ;
        Particle p = new Particle(new Rectangle(pos,this.y + this.size,30,30),this.oriTile);
        this.particals.add(p);
        this.addToPane(p);
    }

}
