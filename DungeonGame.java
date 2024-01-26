import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class DungeonGame extends JFrame{
  public static String username;
  GamePanel game = new GamePanel();
  
  public DungeonGame(){
    super("DungeonGame");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    add(game);
    pack();
    setVisible(true);
    setResizable(false);
  }
  
  public static void main(String [] args){
    //username = JOptionPane.showInputDialog(null,"Enter your Username", JOptionPane.QUESTION_MESSAGE);
    
    DungeonGame Game = new DungeonGame();
  }
}

class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener{
  private boolean[] keys;
  private Timer timer;
  private delayTimer mTimer;
  private delayTimer spawnTimer;
  private delayTimer mapSwitchDelay;
  //delayTimer shootingDelay;
  
  //used during menu selection
  private Image background;
  private Image instructions;

  private int screen;
  private int menuScreen = 0, instructionScreen = 1, gameScreen = 2, deathScreen = 3, victoryScreen = 4;
  
  //direction variables
  private static final int FRONT = 0, LEFT = 1, BACK = 2, RIGHT = 3;
  private int walkKey = 0;
  
  //maybe set some global window size variables?
  private Player player;
  
  private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
  private int numEnem;
  private int score;
  
  private Boss boss = new Boss(860,220);
  //private ArrayList<Laser> bossLasers = new ArrayList<Laser>();
  private boolean fightBoss;
  
  //map variables
  private int location;
  //possible locations
  private int LOBBY = 0, TRAINING = 1, BOSSROOM = 2;
  
  private Image map1;
  private Image lobby;
  private Image bossMap;
  
  private Font roomText;
  private Font titleText;
  private Font subTitleText;
  
  private Button playButton;
  private Button continueButton;
  private Button playAgainButton;
  
  //constructor 
  public GamePanel(){
    
    player = new Player(600, 450);
    
    background = new ImageIcon("background.png").getImage();
    instructions = new ImageIcon("instructionScreen.png").getImage();
    
    keys = new boolean[KeyEvent.KEY_LAST+1];
    mTimer = new delayTimer();
    spawnTimer = new delayTimer();
    //shootingDelay = new delayTimer();
    
    //used to only allow one map change per second
    mapSwitchDelay = new delayTimer();
    
    map1 = new ImageIcon("maps/map.png").getImage();
    lobby = new ImageIcon("maps/lobby.png").getImage();
    bossMap = new ImageIcon("maps/bossmap.png").getImage();
    
    location = LOBBY;
    
   
    //enemies.add(new Enemy(750,250, LEFT));
    //enemies.add(new Enemy(800,300, LEFT));
    numEnem = 1;
    
    //font
    InputStream f = Player.class.getResourceAsStream("PressStart2P-Regular.ttf");
    InputStream ff = Player.class.getResourceAsStream("PressStart2P-Regular.ttf");
    InputStream fff = Player.class.getResourceAsStream("PressStart2P-Regular.ttf");
    
    try{
      roomText = Font.createFont(Font.PLAIN,f).deriveFont(20f);
      titleText = Font.createFont(Font.PLAIN,ff).deriveFont(46f);
      subTitleText = Font.createFont(Font.PLAIN,fff).deriveFont(28f);
    }
    catch(IOException ex){
      System.out.println(ex);
    }
    catch(FontFormatException ex){
      System.out.println(ex); 
    }
    screen = 0;
    
    playButton = new Button(750, 500, 140,33, "PLAY");
    continueButton = new Button(1100, 800, 300, 33, "PLAY GAME");
    playAgainButton = new Button(620, 500, 400, 33,"Play Again?");
    
    //the art is in 64x64 pixel tiles, both are multiples of 64
    //1600 = 64 x 25
    // 896 = 64 x 14
    setPreferredSize(new Dimension(1600,896));
    timer = new Timer(10, this);
    timer.start();
    setFocusable(true);
    requestFocus();
    addKeyListener(this);
    addMouseListener(this);
  }
  
  static int randint(int low, int high){
    return(int)(Math.random() * (high-low+1)+low);
  }
  
  public void reset(){
    screen = gameScreen;
    location = LOBBY;
    player.reset();
    boss.reset();
    enemies.clear();
  }
  
  
  //key inputs for player interaction
  public void playerMove(int ky){
    //by checking if all the keys are clicked, it eliminates any key order which can result in bugs
    //the key which is pressed is entered into the move function
    if(keys[KeyEvent.VK_W] || keys[KeyEvent.VK_A] || keys[KeyEvent.VK_S] || keys[KeyEvent.VK_D]){
      player.move(ky);
    }
    if(keys[KeyEvent.VK_E]){ //attack
      if(player.attack()){
        if(location != LOBBY){
          checkEnemyHit();
        }
        if(location == BOSSROOM){
          checkBossHit();
          boss.checkMinionHit(player);
        }
      }
    }
    if(keys[KeyEvent.VK_SHIFT]){ //roll
      player.roll();
    }
    if(keys[KeyEvent.VK_Q]){ //heal
      player.heal();
    }

    if(keys[KeyEvent.VK_F]){ // used to change maps
      changeMaps();
    }
  }
  
  public void checkEnemyHit(){
    if(player.attackHitbox() != null){
      for(Enemy monster: enemies){
        if(player.attackHitbox().intersects(monster.hitbox())){
          monster.loseHP();
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
  
  
  
  public void checkBossHit(){
    if(player.attackHitbox() != null && boss.hitbox() != null && (player.attackHitbox().intersects(boss.hitbox()))){
      boss.bossGetHit();
    }
  }
  
  public void checkPlayerHit(Enemy a){
    if(a.getFrame() == 7 && a.getAction() == 2){
      player.loseHP();
    }
  }
  
  //spawns in enemies but also removes dead monster
  public void spawnAndCleanup(int limit, int spawnDelay){
    if(spawnTimer.delay(spawnDelay)){
      if(enemies.size() < limit){
        int spawnX = randint(7,153);
        int spawnY = randint(14, 76);
        enemies.add(new Enemy(spawnX*10, spawnY*10, LEFT));
      }
      
      ArrayList<Enemy> junkMonsters = new ArrayList<Enemy>();
      for(Enemy monster: enemies){
        if(monster.checkDead()){
          score+=10;
          junkMonsters.add(monster);
        }
      }
      enemies.removeAll(junkMonsters);
    }
  }
  
  public void wipeMonsters(){
    enemies.clear();
  }
  
  public void summonMinions(){
    spawnAndCleanup(20, 2000);
  }
  
  
  
 
  
  //this method is used to change maps, method below calls this with the right parameters and conditions
  //xMin, xMax, yMin, yMax are boundaries that the players need to be in to go to the room
  /*public void switchMaps(int currentMap, int destination, int xMin, int xMax, int yMin, int yMax, boolean setx, int setValue){
    if(location == currentMap){
      if(player.getx() > xMin && player.getx() < xMax && player.gety() > yMin && player.gety() < yMin()){
        location = destination;
        
      }
    }
  }*/
  
  //depending on the player location, allows travel between rooms by changing location variable
  public void changeMaps(){
    if(mapSwitchDelay.delay(1000)){
      //in Lobby
      if(location == LOBBY){
      	score = 0;
        //if player is near the training map entrance
        if(player.gety() > 320 && player.gety() < 512 && player.getx() > 64 && player.getx() < 128){
          location = TRAINING;
          player.setx(1510);
        }
        //if player is near the boss room entrance
        else if(player.gety() > 140 && player.gety() < 192 && player.getx() > 639 && player.getx() < 832){
          location = BOSSROOM;
          player.sety(736);
        } 
      }
      
      //in Training room
      else if(location == TRAINING){
        //if player is near the training map exit
        
        if(player.getx() < 1536 && player.getx() > 1472 && player.gety() > 320 && player.gety() < 512){
          wipeMonsters();
          location = LOBBY;
          player.setx(90);
        }
      }
      
      //in Boss room
      else if(location == BOSSROOM && boss.getState() == 0){
        if(player.getx() > 639 && player.getx() < 832 && player.gety() > 704 && player.gety() < 761){
          location = LOBBY;
          player.sety(160);
        }
      }
    }
  }
  
  public void changeScreens(Rectangle mousePosition){
    if(screen == menuScreen){
      if(mousePosition.intersects(playButton.clickBox())){
        screen = instructionScreen;
      }
    }
    else if(screen == instructionScreen){
      if(mousePosition.intersects(continueButton.clickBox())){
        screen = gameScreen;
      }
    }
    else if(screen == deathScreen || screen == victoryScreen){
      if(mousePosition.intersects(playAgainButton.clickBox())){
        reset();
      }
    }
  }
  
  @Override
  public void keyPressed(KeyEvent ke){
    int key = ke.getKeyCode();
    keys[key] = true;
    
    //this variable is the key clicked, which than gets placed into the player.move function
    walkKey = key;
  }
  @Override
  public void keyReleased(KeyEvent ke){
    int key = ke.getKeyCode();
    keys[key] = false;
    if(key == KeyEvent.VK_W || key == KeyEvent.VK_A || key == KeyEvent.VK_S || key == KeyEvent.VK_D){
      player.setWalkToIdle();
    }
  }
  @Override
  public void keyTyped(KeyEvent ke){}
  
  
  @Override
  public void mouseClicked(MouseEvent e){
    changeScreens(new Rectangle(e.getX(), e.getY(), 1,1));
  }
  
  @Override
  public void mousePressed(MouseEvent e){}
  @Override
  public void mouseEntered(MouseEvent e){}
  @Override
  public void mouseExited(MouseEvent e){}
  @Override
  public void mouseReleased(MouseEvent e){}
  
  
  public void actionPerformed(ActionEvent e){
    if(screen == gameScreen){
      if(mTimer.delay(20)){
        //updates player 50 times per second (this may change for performance issues)
        playerMove(walkKey);
        if(player.checkDead()){
          screen = deathScreen;
        }
        if(boss.checkDead()){
          screen = victoryScreen;
        }
        
        //checkPlayerHit(monster);
        if(location == TRAINING){
          spawnAndCleanup(5, 1000);
          for(Enemy monster: enemies){
            monster.goToPlayer(player.getx(), player.gety(), player);
          }
        }
        if(location == BOSSROOM){
          //boss.shootLasers(player);
          if(boss.fighting()){
            boss.changeAction(player);
          }
        }
      }
    }
    
    repaint();
  }
  
  int[] xp = {720,740,760};
  int[] yp = {160,140,160};
  
  int[] xxp = {410, 430, 390};
  int[] yyp = {80, 100, 100};
  
  public void paint(Graphics g){
    if(screen == gameScreen || screen == deathScreen || screen == victoryScreen){
      //g.setColor(new Color(255,0,255));
      //g.fillRect(0,0,getWidth(),getHeight());
      //if(gameScreen == 
      if(location == LOBBY){
        g.drawImage(lobby, 0,0, getWidth(), getHeight(), null);
        
        //room indicators
        g.setColor(new Color(255,255,255));
        g.fillPolygon(xp, yp, 3);
        //boss room
        g.setFont(roomText);
        g.drawString("Boss", 700, 190);
        //training room
        g.fillPolygon(yyp, xxp, 3);
        g.drawString("Training", 120, 420);
      }
      //training room
      else if(location == TRAINING){
        g.drawImage(map1, 0,0, getWidth(), getHeight(), null);
        g.setFont(subTitleText);
        g.drawString("Score: " + score, 600, 400);
      }
      //bossroom
      else if(location == BOSSROOM){
        g.drawImage(bossMap, 0,0, getWidth(), getHeight(), null);
        boss.draw(g);
      }
      
      //UI bar
      g.setColor(new Color(0,0,0));
      g.fillRect(0,768,getWidth(),128);
      
      //player and monsters 
      player.draw(g);
      for(Enemy monster: enemies){
        monster.draw(g);
        
      }
      
      //adding a shade of black on top of gameScreen for death screen
      if(screen == deathScreen){
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0, getWidth(), getHeight());
        playAgainButton.draw(g);
        
        g.setColor(new Color(255,0,0));
        g.setFont(titleText);
        g.drawString("You Died" , 600, 400);
        
        
      }
      //a light shader plus yellow text for victory screen
      else if(screen == victoryScreen){
        g.setColor(new Color(50,50,50,150));
        g.fillRect(0,0, getWidth(), getHeight());
        playAgainButton.draw(g);
        
        g.setColor(new Color(255,215,0));
        g.setFont(titleText);
        g.drawString("You Beat the Boss!" , 450, 400);
      }
    }
    
    //menu, draws image
    else if(screen == menuScreen){
      g.drawImage(background, 0,0-200, 1600,1200, null);
      
      g.setFont(titleText);
      g.setColor(new Color(255,255,255));
      g.drawString("Dark Souls", 580,300);
      g.setFont(subTitleText);
      g.drawString("but Jacob made it", 570,350);
      
      playButton.draw(g);
    }
    
    //instruction screen
    else if(screen == instructionScreen){
      g.drawImage(instructions , 0,0, 1600, 896, null);
      
      g.setFont(titleText);
      g.drawString("Instructions", 550,200);
      
      g.setFont(subTitleText);
      g.drawString("Use WASD to move", 600, 300);
      g.drawString("Press E to attack", 590, 400);
      g.drawString("Press Shift to roll", 580, 500);
      g.drawString("Press Q to heal", 600, 600);
      
      continueButton.draw(g);
    }
  }
}