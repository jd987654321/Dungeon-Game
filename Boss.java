import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;


//this handles everything in the boss stage, there will be some duplicate methods 
//that I decided to have here to keep everything in the boss stage in this class
public class Boss{
  
  //spawnX and spawnY will be helpful with reseting the boss
  private int x,y, spawnX, spawnY;
  
  //these determine boss size and will grow when boss is hit
  private int sizeX, sizeY, offsetX, offsetY;
 
  private int direction;
  private int LEFT = 1, RIGHT = 3;
  
  private int action;
  //for reference
  private int SHOOTLASERS = 0, SPAWNMINIONS = 1, BOTH = 2;
  
  //to keep things simpler and less congested, the boss class has its own ArrayList of enemies
  //they'll be called minions throughout the code
  private ArrayList<Enemy> minions = new ArrayList<Enemy>();
  private ArrayList<Laser> bossLasers = new ArrayList<Laser>();
  
  private int health = 800;
  
  private int frame;
  private Image[] frames = new Image[10];
  
  private int state;
  private int passive = 0, growing = 1, fighting = 2;
  
  
  //delayTimer
  delayTimer frameDelay;
  delayTimer growTimer;
  delayTimer actionChangeTimer;
  
  delayTimer minionTimer;
  delayTimer shootingDelay;
  
  public Boss(int xx, int yy){
    x = xx;
    y = yy;
    spawnX = xx;
    spawnY = yy;
    
    //size when boss is passive
    sizeX = 60;
    sizeY = 80;
    
    offsetX = 15;
    
    direction = RIGHT;
    
    frame = 0;
    
    state = passive;
    
    action = SHOOTLASERS;
    
    frameDelay = new delayTimer();
    growTimer = new delayTimer();
    actionChangeTimer = new delayTimer();
    minionTimer = new delayTimer();
    shootingDelay = new delayTimer();
    
    //9 frames
    for(int i = 0; i < 10; i++){
      frames[i] = new ImageIcon("boss/boss" + i + ".gif").getImage();
    }
  }
  
  //resets the entire boss, removes all the lasers, minions
  //used to restart the level when the player dies
  public void reset(){
    state = passive;
    direction = RIGHT;
    health = 800;
    sizeX = 60;
    sizeY = 80;
    frame = 0;
    x = spawnX;
    y = spawnY;
    
    minions.clear();
    bossLasers.clear();
    
    resetTimers();
  }
  
  public void resetTimers(){
    frameDelay.reset();
    growTimer.reset();
    actionChangeTimer.reset();
    minionTimer.reset();
    shootingDelay.reset();
  }
  
  static int randint(int low, int high){
    return(int)(Math.random() * (high-low+1)+low);
  }
  
  public int getx(){
    return x;
  }
  public int gety(){
    return y;
  }
  public int getEyeX(){
    if(direction == LEFT){
      return x-90;
    }
    else{
      return x-30;
    }
  }
  public int getEyeY(){
    return y-135;
  }
  
  public int getState(){
   return state; 
  }
  
  public boolean checkDead(){
    if(health <= 0){
      return true;
    }
    else{
      return false;
    }
  }
  
  //changes direction and moves boss character
  public void moveSideways(int speed){
    if(direction == LEFT){
      x-=speed;
      if(x < 230){
        direction = RIGHT;
      }
    }
    else if(direction == RIGHT){
      x+=speed;
      if(x > 1480){
        direction = LEFT;
      }
    }
  }
  
  //similar as the spawn method, handles spawning and cleaning up dead minions
  public void spawnMinions(int limit, int spawnDelay){
    //adding minions
    if(minionTimer.delay(spawnDelay)){
      if(minions.size() < limit){
        int spawnX = randint(7,153);
        int spawnY = randint(14, 76);
        minions.add(new Enemy(spawnX*10, spawnY*10, LEFT));
      }
      
      //removes minions
      ArrayList<Enemy> junkMonsters = new ArrayList<Enemy>();
      for(Enemy monster: minions){
        if(monster.checkDead()){
          junkMonsters.add(monster);
        }
      }
      minions.removeAll(junkMonsters);
    }
  }
  
  //same method as check Enemy hit but for boss minions instead
  public void checkMinionHit(Player player){
    if(player.attackHitbox() != null){
      for(Enemy minion: minions){
        if(player.attackHitbox().intersects(minion.hitbox())){
          minion.loseHP();
      }
      /*if(player.attackHitbox().intersects(enemy2.hitbox())){
        enemy2.loseHP();
        System.out.println("yuh");
      }
      if(player.attackHitbox().intersects(enemy3.hitbox())){
        enemy3.loseHP();
        System.out.println("yuh");
      }*/
      }
    }
  }
  
  public void moveMinions(Player player){
    if(minions.size() > 0){
      for(Enemy minion: minions){
        minion.goToPlayer(player.getx(), player.gety(), player);
      }
    }
  }
  
  //this class handles creating and cleaning up lasers
  public void shootLasers(Player player, int shootDelay){
    //creating lasers
    if(shootingDelay.delay(shootDelay)){
      bossLasers.add(new Laser(getEyeX(), getEyeY(), 0));
    }
    
    //removing
    ArrayList<Laser> junkLasers = new ArrayList<Laser>();
    if(bossLasers.size() > 0){
      for(Laser laser: bossLasers){
        if(laser.outOfBounds()){
          junkLasers.add(laser);
        }
        else if(laser.hitbox().intersects(player.hitbox())){
          player.loseHP();
          junkLasers.add(laser);
        }
      }
      bossLasers.removeAll(junkLasers);
    }
  }
  
  //a seperate method for moving lasers
  public void moveLasers(){
    if(bossLasers.size() > 0){
      for(Laser laser: bossLasers){
        laser.move(5);
      }
    }
  }
  
  //changes boss actions depending on HP
  public void changeAction(Player player){
    if(state == fighting){
      moveMinions(player);
      moveLasers();
      
      if(health >= 700){
        spawnMinions(7, 1000);
        moveSideways(2);
      }
      else if(health >= 600 && health < 700){
        shootLasers(player, 500);
        moveSideways(2);
      }
      else if(health >= 500 && health < 600){
        shootLasers(player, 1000);
        spawnMinions(6, 2000);
        moveSideways(4);
      }
      else if(health >= 400 && health < 500){   
        spawnMinions(10,2000);
      }
      else if(health >= 400 && health < 500){   
        shootLasers(player, 750);
        moveSideways(8);
      }
      else if(health >= 300 && health < 400){
        spawnMinions(2, 500);
        moveSideways(1);
      }
      else if(health >= 100 && health < 300){
        shootLasers(player, 1000);
        spawnMinions(6,500);
        moveSideways(1);
      }
      else if(health > 0 && health < 100){
        shootLasers(player, 200);
        moveSideways(4);
      }
    }
  }
  
  //runs 
  public void bossActivity(Player player){
    if(state == fighting){
    //changeAction();
    if(action == SHOOTLASERS){
      
    }
    }
  }
  
  //doesnt show a hitbox if boss is growing
  public Rectangle hitbox(){
    if(state == passive || state == fighting){
      return new Rectangle(x-(sizeX*3/4),y-sizeY-20,sizeX/2, sizeY);
    }
    else{
      return null;
    }
  }
  
  //used to trigger boss fight and damage boss 
  //no condition needed for growing, boss won't return a hitbox during that phase
  public void bossGetHit(){
    if(state == passive){
      state = growing;
    }
    else{
      health-=10;
    }
    System.out.println(health);
  }
  
  //tells if boss is fighting or not
  public boolean fighting(){
    if(state == fighting){
      return true;
    }
    else{
      return false;
    }
  }
  
  //changes the frame that is displayed
  public void setFrame(){
    if(frameDelay.delay(100)){
      frame+=1;
      if(frame == 9){
        frame = 0;
      }
    }
  }
  
  //one frame gets selected and drawn
  public void draw(Graphics g){
    setFrame();
    
    //hitbox tester
    //g.setColor(new Color(255,255,255));
    //g.fillRect(x-(sizeX*3/4),y-sizeY-20,sizeX/2, sizeY);
    
    if(state == passive){
      g.drawImage(frames[frame],  x - sizeX,y - sizeY, sizeX,sizeY, null);
    }
    else if(state == growing){
      if(growTimer.delay(100)){
        if(sizeX < 120 && sizeY < 160){
          sizeX += 3;
          sizeY += 4;
        }
        else{
          state = fighting;
        }
      }
      g.drawImage(frames[frame], x - sizeX,y - sizeY, sizeX,sizeY, null);
    }
    
    else if(state == fighting){
      //for drawing them in different directions
      if(direction == RIGHT){
        g.drawImage(frames[frame], x - sizeX,y - sizeY, sizeX,sizeY, null);
        g.setColor(new Color(0,255,0));
        g.fillRect(x-30,y-135,3,3);
      }
      else if(direction == LEFT){
        g.drawImage(frames[frame], x,y - sizeY, -1*sizeX,sizeY, null);
      }
      //healthbar
      g.setColor(new Color(255,0,0));
      g.fillRect(0,0,health*2,20);
     
    }
    
    //lasers (if there are any)
      if(bossLasers.size() > 0){
        for(Laser laser: bossLasers){
          laser.draw(g);
        }
      }
      
      if(minions.size() > 0){
        for(Enemy minion: minions){
          minion.draw(g);
        }
      }
  }
}
  