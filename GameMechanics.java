//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
//import java.util.ArrayList;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import javax.imageio.*;
//import javax.sound.sampled.Clip;
//import javax.sound.sampled.AudioSystem;
//
//class GamePanel extends JPanel implements KeyListener, ActionListener{
//  private boolean[] keys;
//  Timer timer;
//  //maybe set some global window size variables?
//  Player player = new Player(600, 450);
//
//  
//  public GamePanel(){
//    
//    keys = new boolean[KeyEvent.KEY_LAST+1];
//    
//    setPreferredSize(new Dimension(1200,900));
//    timer = new Timer(10, this);
//    timer.start();
//    setFocusable(true);
//    requestFocus();
//    addKeyListener(this);
//  }
//  
//  
//  @Override
//  public void keyPressed(KeyEvent ke){
//    int key = ke.getKeyCode();
//    keys[key] = true;
//    System.out.println(key);
//    
//    
//    if(keys[KeyEvent.VK_UP]){
//      player.moveUp();
//    }
//    if(keys[KeyEvent.VK_DOWN]){
//      player.moveDown();
//    }
//    if(keys[KeyEvent.VK_RIGHT]){
//      player.moveRight();
//    }
//    if(keys[KeyEvent.VK_LEFT]){
//      player.moveLeft();
//    }
//  }
//  @Override
//  public void keyReleased(KeyEvent ke){
//    int key = ke.getKeyCode();
//    keys[key] = false;
//  }
//  @Override
//  public void keyTyped(KeyEvent ke){
// 
//  }
//  
//  
//  public void actionPerformed(ActionEvent e){
//    repaint();
//  }
//  
//  
//  public void paint(Graphics g){
//    g.setColor(new Color(255,255,255));
//    g.fillRect(0,0,getWidth(),getHeight());
//    
//    player.draw(g);
//  }
//}