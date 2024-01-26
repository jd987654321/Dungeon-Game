import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class Player{
  
  //spawnX and spawnY will be helpful when reseting the player after death
  private int x,y, spawnX, spawnY;      
  private int speed;
  
  //these are the VK values for each key used to move that direction
  private static final int W = 87, A = 65, S = 83, D = 68;
  
  //these variables are used to determine and change direction
  private static final int FRONT = 0, LEFT = 1, BACK = 2, RIGHT = 3;
  private int direction;
  private int directionChange;
  
  
  private int frame = 0;
  private boolean idle = false;
  private boolean inAttack = false;
  
  //int action will be changed for each action function
  //action will also control which frame set to use for each action
  private static final int IDLE = 0, WALK = 1, ATTACK = 2, ROLL = 3, BLOCK = 4;  //we may or may not use these, right now we are not using them
  private int action = 0; 
    
  //these are used to make sure you finish an attack animation before you being another one
  boolean moving;
  boolean attacking;
  boolean rolling;
  boolean rollOnCD;
  boolean blocking;

  
  //five images per frame
  //reference direction values above
  private Image[][][] frames = new Image[5][4][4];
  private Image[][] walkFrames = new Image[4][4];
  private Image[][] attackFrames = new Image[4][4];
  private Image[][] idleFrames = new Image[4][4];
  
  private Image heart;
  
  private String name;
  
  //fonts
  //abilities
  private Font abilityText = null;
  private Font nameText = null;
  
  //image/frame being displayed
  private Image displayFrame;
  
  //of course we have to have a handy dandy delayTimer
  private delayTimer delay;
  private delayTimer atkDelay;
  
  //ability cooldowns
  private delayTimer rollCD;
  private delayTimer healCD;
  
  //health 
  private int health;
  
  //roll cooldown
  private int rollCooldown;
  
  //add a attack hitbox
  
  
  
  //constructor
  public Player(int xx, int yy){
    x = xx;
    y = yy;
    
    spawnX = xx;
    spawnY = yy;
    
    speed = 4;
    direction = FRONT;
    directionChange = FRONT;
    
    moving = false;
    attacking = false;
    blocking = false;
    rolling = false;
    rollOnCD = true;

    //name = namee;
    
    delay = new delayTimer();
    atkDelay = new delayTimer();
    
    rollCD = new delayTimer();
    healCD = new delayTimer();
    
    //health is default 100
    health = 100;

    //roll cooldown
    rollCooldown = 0;
    
    //grabs all the images
    
    String [] directions = {"down", "left", "up", "right"};
    
    for(int dir = 0; dir < 4; dir++){
      for(int fra = 0; fra < 4; fra++){
        frames[IDLE][dir][fra] = new ImageIcon("SpriteAnimations/Character with sword and shield/idle/idle " + directions[dir] + (fra+1) + ".png").getImage();
        frames[WALK][dir][fra] = new ImageIcon("SpriteAnimations/Character with sword and shield/walk/walk " + directions[dir] + (fra+1) + ".png").getImage();
        frames[ATTACK][dir][fra] = new ImageIcon("SpriteAnimations/Character with sword and shield/attack/attack " + directions[dir] + (fra+1) + ".png").getImage();
        frames[ROLL][dir][fra] = new ImageIcon("SpriteAnimations/Character with sword and shield/roll/roll " + directions[dir] + (fra+1) + ".png").getImage();
      }
      frames[BLOCK][dir][0] = new ImageIcon("SpriteAnimations/Character with sword and shield/block/block " + directions[dir]+ ".png").getImage();
    }
    
    heart = new ImageIcon("heart.png").getImage();
    
    InputStream f = Player.class.getResourceAsStream("PressStart2P-Regular.ttf");
    InputStream ff = Player.class.getResourceAsStream("PressStart2P-Regular.ttf");
    
    try{
      abilityText = Font.createFont(Font.PLAIN,f).deriveFont(20f);
      nameText = Font.createFont(Font.PLAIN, ff).deriveFont(12f);
    }
    catch(IOException ex){
      System.out.println(ex);
    }
    catch(FontFormatException ex){
      System.out.println(ex); 
    }
  }
  
  //helpful when reseting a player when they die
  public void reset(){
    x = spawnX;
    y = spawnY;
    action = IDLE;
    direction = FRONT;
    health = 100;
    
    resetTimers();
  }
  
  public void resetTimers(){
    delay.reset();
    atkDelay.reset();
    
    rollCD.reset();
    healCD.reset();
  }
  
  
  //getter setters
  public int getx(){
    return x;
  }
  public int gety(){
    return y;
  }
  public int getFrame(){
    return frame;
  }
  public boolean checkDead(){
    if(health <= 0){
      return true;
    }
    else{
      return false;
    }
  }
  
  //helpful when switching maps
  public void setx(int xx){
    x = xx;
  }
  public void sety(int yy){
    y = yy;
  }
  
  public int rollCooldown(){
    int rollcd = (int)rollCD.tellTime();
    if(rollcd >= 4){
      rollOnCD = false;
      return 4;
    }
    else{
      return rollcd;
    }
  }
  
  public int healCooldown(){
    int healcd = (int)healCD.tellTime();
    if(healcd >= 10){
      rollOnCD = false;
      return 10;
    }
    else{
      return healcd;
    }
  }
  
  //new move function
  //have everything be decided at the draw function and everything just changes those variables
  
  //
  
  public void move(int key){
    if(action == IDLE || action == WALK){
      setMoving();
      if(key == S){
        direction = FRONT;
        for(int pp = 0; pp < 10; pp++){
          if(y < 760){ //old limits: 615, 741
            y+=1;
          }
        }
      }
      else if(key == W){
        direction = BACK;
        for(int pp = 0; pp < 10; pp++){
          if(y > 140){
            y-=1;
          }
        }
      }
      else if(key == D){
        direction = RIGHT;
        for(int pp = 0; pp < 10; pp++){
          if(x < 1530){
            x+=1;
          }
        }
      }
      else if(key == A){
        direction = LEFT;
        for(int pp = 0; pp < 10; pp++){
          if(x > 70){
            x-=1;
          }
        }
      }
    }
  }
  
  
  public void setMoving(){
    action = WALK;
    moving = true;
    if(moving = false){
      frame = 0;
    }
  }
  
  //causes the character to be idle if they're not doing anything
  public void setWalkToIdle(){
    if(action == WALK){
      action = IDLE;
      moving = false;
    }
  }
  
  
  //basically each section works very similarly
  //if the character is not doing any action,
  //set the action variable to that action,
  //and resets the frames
  
  //copy and paste for each section
  //(attacking == false && rolling == false && blocking == false)
  
  public boolean attack(){
    if(attacking == false && rolling == false && blocking == false){
      attacking = true;
      action = ATTACK;
      frame = 0;
      return true;
    }
    else{
      return false;
    }
  }
  
  public void roll(){
    if(attacking == false && rolling == false && blocking == false && rollCooldown() == 4){
      rollCD.reset();
      rolling = true;
      action = ROLL;
      frame = 0;
      System.out.println(rollCooldown());
      rollOnCD = true;
    }
  }
  
   public void heal(){
     if(healCooldown() == 10 && health < 100){
       healCD.reset();
       health += 50;
       System.out.println(healCooldown());
     }
  }
  

  
  public void block(){
    if(attacking == false && rolling == false && blocking == false){
      blocking = true;
      action = BLOCK;
      frame = 0;
    }
  }
  
  //returns body hitbox
  public Rectangle hitbox(){
    return new Rectangle(x-20,y-55, 40,55);
  }
  
  //returns hitbox of the attack
  public Rectangle attackHitbox(){
    if(action == ATTACK){
      if(direction == FRONT){
        return new Rectangle(x-40,y,85,30);
      }
      else if(direction == BACK){
        return new Rectangle(x-40,y- 80,85,30);
      }
      else if(direction == LEFT){
        return new Rectangle(x-60,y-35,65,70);
      }
      else if(direction == RIGHT){
        return new Rectangle(x-5,y-35, 65,70);
      }
      else{
        return null;
      }
    }  
    else{
      return null;
    }
  }

  //dev tools
  //lose hp
  public void loseHP(){
    health-=10;
  }
  
  public void fullHP(){
    health = 100;
  }
  
  
  //the frames are redrawn every ten milliseconds, the frames are changed at different rates
  //for each action, since there are consistently four frames 
  public void draw(Graphics g){
    if(action == IDLE || action == WALK){
      if(delay.delay(180)){
        frame+=1;
        if(frame == 4){
          frame = 0;
        }
      }
    }
    
    
    if(action == ATTACK){
      if(delay.delay(50)){
        frame+=1;
        if(frame == 4){
          frame = 0;
          action = IDLE;
          attacking = false;
        }
      }
    }
    
    //rolling is supposed to 
    if(action == ROLL){
      if(delay.delay(100)){
        frame+=1;
        if(direction == BACK){
          for(int pp = 0; pp < 30; pp++){
            if(y > 139){  
              y-=1;
            }
          }
        }
        else if(direction == FRONT){
          for(int pp = 0; pp < 30; pp++){
            if(y < 763){ //old limits: 615, 741
              y+=1;
            }
          }
        }
        else if(direction == RIGHT){
          for(int pp = 0; pp < 30; pp++){
            if(x < 1533){
              x+=1;
            }
          }
        }
        else if(direction == LEFT){
          for(int pp = 0; pp < 30; pp++){
            if(x > 67){
              x-=1;
            }
          }
        }
        if(frame == 4){
          frame = 0;
          action = IDLE;
          rolling = false;
        }
      }
    }
    
    if(action == BLOCK){
      if(delay.delay(800)){
        action = IDLE;
        blocking = false;
      }
    }
     
    
    //g.setColor(new Color(255,255,255));
    //g.fillRect(x-20,y-55, 40,55);
    
    //hitbox tester
    
    //g.setColor(new Color(0,255,0));
  
    //g.fillRect(x-40,y- 80,85,30);
    

    displayFrame = /*frames[ATTACK][BACK][0];*/frames[action][direction][frame];
    g.drawImage(displayFrame, x-125,y-150, 250,250, null);
    
    //g.setColor(new Color(255,0,0));
    //g.fillRect(x,y,2,2);
    
    //player ui
    
    //healthbar
    g.setColor(new Color(255,0,0));
    g.fillRect(130,798, 400,66);
    g.setColor(new Color(0,255,0));
    
    //making sure that HP does not go above 100
    if(health > 100){
      health = 100;
    }
    
    g.fillRect(130,798, health * 4, 66); 
    g.drawImage(heart, 50,788, 80,80, null);
    
    
    //abilities ui
    //ability texts
    g.setFont(abilityText);
    g.setColor(new Color(255,255,255));
    
    g.drawString("ROLL (SHIFT):", 580,823);
    g.drawString("HEAL (Q): " ,580, 863);
    
    //empty bars
    g.setColor(new Color(100,100,100));
    g.fillRect(850,796, 200, 32);
    g.fillRect(850, 836, 200, 32);
      
    //progress bars
    g.setColor(new Color(255,165,0));
    g.fillRect(850,796, rollCooldown() * 50, 32);
    g.setColor(new Color(0,255,255));
    g.fillRect(850, 836, healCooldown() * 20, 32);
    
    
    //g.setFont(nameText);
    
    //g.drawString(name, 823, 1100);
    
            
  }
}
  









