package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URISyntaxException;
import java.util.Random;

public class Partical extends Actor{
    private Point2D randomVel;
    private boolean expired;
    private double x ,y;
    private int r,g,b;


    public Partical(Rectangle rec, Tile tile) {
        super(rec, tile);
        this.r = 255; this.g = 255; this.b = 0;
        ColorAdjust c = new ColorAdjust();
        c.setHue(Color.rgb(this.r,this.g,this.b).getHue());

        Image image;
        ImageView p;
        try {
            image = new Image(getClass().getResource("/Assets/Fire/fire0.bmp").toURI().toString());
            p = new ImageView(image);
            p.setEffect(c);
            rec.setFill(new ImagePattern(p.getImage()));
            System.out.println("Complete");
        } catch (URISyntaxException ex) {
            System.out.println(ex);
        }
        c.setHue(Color.rgb(this.r,this.g,this.b).getHue());
        Random rand = new Random();
        this.randomVel = new Point2D((double)rand.nextInt(1) - 1, -1);
        this.velocity = randomVel;
        this.expired = false;
    }

    @Override
    public void update(Floor floor){
        double opacity = this.view.getOpacity();
        /*
        if(opacity > 0){
            this.view.setOpacity(opacity - 0.1);
            this.x = this.velocity.getX() - 0.1;
            this.y = this.velocity.getY() + 0.1;
            this.setVelocity(new Point2D(this.x,this.y));
        }else{
            this.expired = true;
        }*/
    }


    public boolean isExpired(){return this.expired;}


}
