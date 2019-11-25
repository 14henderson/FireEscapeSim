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

public class Particle extends Actor{
    private Point2D randomVel;
    private boolean expired;
    private double x ,y, fade;
    private int r,g,b;


    public Particle(Rectangle rec, Tile tile) {
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
        this.randomVel = new Point2D(0, rand.nextInt(4)-4 );
        this.velocity = randomVel;
        this.fade = ((double)(rand.nextInt(25) + 1)) /100;
        this.expired = false;
    }

    //@Override
    public void update(Floor floor){
        double opacity = this.fxNode.getOpacity();
        if(opacity > 0){
            this.fxNode.setOpacity(opacity - this.fade);
            this.x = this.velocity.getX();
            this.y = this.velocity.getY() - 0.5;
            this.setVelocity(new Point2D(this.x,this.y));
        }else{
            this.expired = true;
        }
        this.fxNode.setLayoutX(this.velocity.getX());
        this.fxNode.setLayoutY(this.velocity.getY());
    }

    public void reset(int y){
        Random rand = new Random();
        this.y = y;
        this.fxNode.setLayoutY(this.y);
        this.fxNode.setOpacity(1);
        this.velocity = this.randomVel;
        this.fade = ((double)(rand.nextInt(25) + 1)) /100;
        this.expired = false;
    }

    public boolean isExpired(){return this.expired;}


}
