package fireescapedemo;

public class QuadTree{
    private final int cap;
    private final double x,y,w,h;
    private int iter;
    private boolean divided;
    private final Actor[] points;
    private final QuadTree[] children;


    public QuadTree(int cap, double x, double y, double w, double h){
        this.cap = cap;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.iter = 0;
        this.divided = false;
        this.points = new Actor[this.cap];
        this.children = new QuadTree[4];
    }

    private void createChildren(){
        //northwest
        QuadTree northwest = new QuadTree(this.cap,this.x,this.y,this.w/2,this.h/2);
        //northeast
        QuadTree northeast = new  QuadTree(this.cap, this.x - this.w,this.y,this.h/2,this.w/2);
        //southwest
        QuadTree southwest = new QuadTree(this.cap, this.x, this.y - this.h,this.w/2,this.h/2);
        //southeast (represent)
        QuadTree southeast = new QuadTree(this.cap,this.x  - this.w,this.y - this.h,this.w/2,this.h/2);
        this.children[0] = northeast;
        this.children[1] = northwest;
        this.children[2] = southwest;
        this.children[3] = southeast;
    }

    public boolean contains(Actor a){
        double aX = a.view.getLayoutX(), aY =  a.view.getLayoutX();
        return aX > this.x && aX < this.x+this.w && aY > this.y && aY < this.x+this.h;
    }

    public void insert(Actor a){
        if(this.iter+1 <= this.cap){
            this.points[this.iter] = a;
            System.out.println("Child added");
            iter++;
        }else{
            if(!this.divided){
                this.createChildren();
                System.out.println("Split created");
                this.divided = true;
            }
            int i = 0, len = this.children.length;
            //for each child, if the actor is within the bounds, run childs insert method
            for(i  = 0; i < len; i++){ if(this.children[i].contains(a)){ this.children[i].insert(a);} }
        }
    }


}