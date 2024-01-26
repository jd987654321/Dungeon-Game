import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;


//this monster has a pattern, runs at you, attacks, idles for 2 seconds and repeats
public class Enemy{
  int x,y;
  
  //regular direction variables
  private static final int FRONT = 0, LEFT = 1, BACK = 2, RIGHT = 3;
  private int direction;
  
  private int health;
  
  //variables to control the state of this enemy
  private static final int IDLE = 0, WALK = 1, ATTACK = 2, HURT = 3, DYING = 4, DEAD = 5;
  private int action;
  
  //booleans to set state of enemy
  
  //attacking in particular is used to make sure the method
  //returning the attack hitbox only returns it once
  private boolean walking, attacking, attacked, hurting, dead;
  
  //used to return the hitbox of an attack once
  
  //one image set is unfortunately 19 images
  Image[][] frames = new Image[6][20];
  Image displayed;
  private int frame;
  
  //timers
  delayTimer frameDelay;
  delayTimer idleTimer;
  delayTimer attackDelay;
  
  //constructor
  public Enemy(int xx, int yy, int dir){
    x = xx;
    y = yy;
    direction = RIGHT;
    action = HURT;
    frame = 0;
    
    health = 100;
    
    //booleans
    walking = false;
    attacking = false;
    attacked = false;
    hurting = false;
    dead = false;
    
    //all the images needed
    for(int fr = 0; fr < 19; fr++){
      frames[IDLE][fr] = new ImageIcon("Enemy/idle/idle" + fr + ".png").getImage();
      frames[WALK][fr] = new ImageIcon("Enemy/walk/walk" + fr + ".png").getImage();
      frames[ATTACK][fr] = new ImageIcon("Enemy/attack/attack" + fr + ".png").getImage();
      frames[HURT][fr] = new ImageIcon("Enemy/hurt/hurt" + fr + ".png").getImage();
      frames[DYING][fr] = new ImageIcon("Enemy/death/death" + fr + ".png").getImage();
    }
    
    frameDelay = new delayTimer();
    idleTimer = new delayTimer();
    attackDelay = new delayTimer();
  }
  
  //for getting hit
  public void loseHP(){
    if(health > 0){
      health-=20;
      action = HURT;
      frame = 0;
    }
  }
  
  public void dieIfZeroHP(){
    if(action != DYING && action != DEAD){
      if(health <= 10){
        action = DYING;
      }
    }
  }
  
  //if returns true, other method will remove from existence
  public boolean checkDead(){
    if(action == DEAD){
      return true;
    }
    else{
      return false;
    }
  }
  
  //depending on the direction entered, will move 5 units in that direction
  public void move(int dir){
    if(action == WALK){
      if(dir == FRONT || dir == BACK){
        y+= (dir-1)*-5;
      }
      else if(dir == RIGHT || dir == LEFT){
        x+= (dir-2)*5;
      }
    }
  }
  

  
  //dev tool
  int count = 0;
  
  //rename to attackPlayer
  public void goToPlayer(int playerX, int playerY, Player player){
    int offsetX = playerX - x;
    int offsetY = playerY - y;
    
    if(offsetX < 0){
      direction = LEFT;
    }
    else if(offsetX > 0){
      direction = RIGHT;
    }
    
    int abX = Math.abs(offsetX);
    int abY = Math.abs(offsetY);
    count +=1;
    /*if(count == 100){
      System.out.println("offsetX: " + offsetX);
      System.out.println("offsetY: " + offsetY);
      count = 0;
    }*/
      
    //System.out.println("offsetX: " + abOffX);
    //System.out.println("offsetY: " + abOffY);
    
    //conditionals that decide the direction this monster goes, it is based off the x,y 
    //distances from the player
    
    //area from center
    int distC = 0;
    
    //move upwards (BACK)
    if(abY >= abX && ((offsetX <= distC && offsetY <= distC) || (offsetX >= distC && offsetY <= distC))){
      move(BACK);
      //System.out.println("up");
    }
    //move downwards (FRONT)
    if(abY >= abX && ((offsetX <= distC && offsetY >= distC) || (offsetX >= distC && offsetY >= distC))){
      move(FRONT);
      //System.out.println("down");
    }
    if(abY <= abX && ((offsetX <= distC && offsetY >= distC) || (offsetX <= distC && offsetY <= distC))){
      move(LEFT);
      //System.out.println("left");
    }
    if(abY <= abX && ((offsetX >= distC && offsetY >= distC) || (offsetX >= distC && offsetY <= distC))){
      move(RIGHT);
      //System.out.println("right");
    }
    
    //checks if player is in range for an attack
    if(attackDelay.delay(50)){
      if(abX <= 40 && abY <= 50){
        attack(player);
      }
    }
  }
  
  public void attack(Player player){
    if(attacked == false){
      action = ATTACK;
      attacked = true;
      //used for the attackHitbox method
      attacking = true;
      frame = 0;
      player.loseHP();
    }
  }
  
  //these two are called during setFrame, they help with the monster attack pattern
  public void toIdle(){
    action = IDLE;
    attacked = true;
    attacking = false;
    idleTimer.reset();
  }
  
  public void idleToWalk(){
    action = WALK;
    attacked = false;
    frame = 0;
  }
  
  
  //provides hitbox when called
  public Rectangle hitbox(){
    if(action != DEAD){
      return new Rectangle(x-20,y-50, 40,50);
    }
    //if dead return an obscure hitbox that can't be hit
    else{
      return new Rectangle(-1000,-1000,1,1);
    }
  }
  
  //not needed for now
  public Rectangle attackHitbox(){
    //used to make sure this only returns the hitbox once
    // so that the player can't be hit multiple times during one attack animation
    
      if(action == ATTACK /*&& attacking == true*/){
        System.out.println("how????");
        //attacking = false;
        
        if(direction == LEFT){
          System.out.println("left");
          return new Rectangle(x-20, y-50, 20, 50);
        }
        else if(direction == RIGHT){
          System.out.println("right");
          return new Rectangle(x, y-50, 20, 50);
        }
        else{
          System.out.println("null");
          return null;
        }
      }
      else{
        System.out.println("HOW THE FUCK");
        return null;
      }
    }

  public int getFrame(){
    return frame;
  }
  
  public int getAction(){
    return action;
  }
  
  
  public void setFrame(){
     
    //9 frames in idle
    if(action == IDLE){
      if(frameDelay.delay(50)){
        frame+=1;
        if(frame == 9){
          frame = 0;
        }
        if(idleTimer.delay(3000)){
          idleToWalk();
        }
      }
    }
    
    //12 frames in attack
    else if(action == ATTACK){
      if(frameDelay.delay(10)){
        frame+=1;
        if(frame == 12){
          frame = 0;
          toIdle();
        }
      }
    }
    
    //6 frames in walk
    else if(action == WALK){
      if(frameDelay.delay(100)){
        frame+=1;
        if(frame == 6){
          frame = 0;
        }
      }
    }

    //5 frames in hurt
    else if(action == HURT){
      if(frameDelay.delay(80)){
        frame+=1;
        if(frame == 5){
          frame = 0;
          toIdle();
          
        }
      }
    }
    //19 frames in death
    else if(action == DYING){
      if(frameDelay.delay(30)){
        frame+=1;
        if(frame == 19){
          frame = 0;
          action = DEAD;
        }
      }
    }
  }
  
  //will be using the same idea as player class, one 
  //image variable is changed depending on conditions 
  public void draw(Graphics g){
    dieIfZeroHP();
    if(action != DEAD){
      setFrame();
      
      //sets and draws frame
      
      //hitbox testing
      //g.setColor(new Color(255,255,255));
      //g.fillRect(x-20,y-50, 40,50);
      
      //g.setColor(new Color(0,255,0));
      //g.fillRect(x,y, 2,2);
      
      //the chosen frame, the almighty
      displayed = frames[action][frame];
      
      //different heights for each set of pictures
      if(action == ATTACK || action == DYING){
        if(direction == RIGHT){
          g.drawImage(displayed, x-30,y-90, 50,100, null);
        }
        else if(direction == LEFT){
          g.drawImage(displayed, x+30,y-90, -50,100, null);
        }
      }
      if(action == IDLE || action == WALK || action == HURT){
        //draws them depending on direction
        if(direction == RIGHT){
          g.drawImage(displayed, x -30,y-50, 50,50, null);
        }
        else if(direction == LEFT){
          g.drawImage(displayed, x+30,y-50, -50,50, null);
        }
      }
      
      
      //draws healthbar
      g.setColor(new Color(255,0,0));
      g.fillRect(x-30, y-70, 60,10); 
      g.setColor(new Color(0,0,0));
      g.fillRect(x-30, y-70, health/10 * 6,10); 
      
      /*g.setColor(new Color(255,0,255));
      g.fillRect(x-20, y-50, 20, 50);
      g.setColor(new Color(0,255,0));
      g.fillRect(x, y-50, 20, 50);*/
    }
  }
  
}