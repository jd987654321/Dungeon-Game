import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

  //recycled code from past projects
  //name was changed from bullets to lasers for some added coolness and to make more sense
public class Laser{
  //bullets in this class are seperated by their direction, going down means the bullets belong to the enemy,
  //while going up means the bullets belong to the player ship. The color changes as well.
  
  static int randint(int low, int high){
    return(int)(Math.random() * (high-low+1)+low);
  }
  
  private int x, y;
  private int direction; //  1 is up, 0 is down
  final long bulletStart = System.currentTimeMillis();
    
  public Laser(int xx, int yy, int dir){
    y = yy;
    x = xx;
    direction = dir;
  }
  
  //getter changes depending on direction
  public int getx(){
    return x + 1;
  }
  public int gety(){
    if(direction == 1){
      return y;
    }
    else{
      return y + 30;
    }
  }
    
  //changes color depending on direction
  public void draw(Graphics g){
    if(direction == 1){
      g.setColor(new Color(0,randint(100,255),randint(100,255)));
    }
    else if(direction == 0){
      g.setColor(new Color(255,0,0));
    }
    g.fillRect(x,y, 6,40);
  }
  public Rectangle hitbox(){
    return new Rectangle(x,y, 2,40);
  }
    
  
  public void move(int speed){
    if(direction == 1){        //when diretion is up, shoot up
      y -= speed;
    }
    else if(direction == 0){   //when direction is down, shoot down
      y += speed;
    }
  }
    
  //helps with removing extra bullets
  public boolean outOfBounds(){
    if(y < -30 || y > 765){
      return true;
    }
    else{
      return false;
    }
  }
}