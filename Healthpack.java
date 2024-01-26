import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;


//potion class, a green plus sign that gives health
public class Healthpack{
  int x,y;
  
  public Healthpack(int xx, int yy){
    x = xx;
    y = yy;
  }
  
  /*public void restoreHP(Player player){
    player.addHP(20);
  }*/
  
  //drawing the symbol
  public void draw(Graphics g){
    g.setColor(new Color(57, 255, 20));
    g.fillRect(x-20,y-20, 40, 20);
    g.fillRect(x-20,y-20, 20, 40);
  }
}