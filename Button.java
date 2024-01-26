import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.imageio.*;
import java.io.*;

//old recycled code from past projects, makes a button with a hitbox that the mouse coordinates can intersect with

public class Button{
  //A button class, mainly used to draw a String and contain a rectangle aka hitbox.
  //Mouse methods can be used to "click" on mouse hitbox
  
  public int x, y, h, w;
  int color, colorChange;
  String contents;
  Font fontSansBig = new Font("Times New Roman", Font.PLAIN, 60);
  Font fontCool = null;
  
  
  public Button(int xx, int yy, int width, int height, String content){
    x = xx;
    y = yy;
    w = width;
    h = height;
    contents = content;
    
    color = 100;
    colorChange = 1;
    
    InputStream is = GamePanel.class.getResourceAsStream("PressStart2P-Regular.ttf");
    
    //similar to code provided by Mr. Mackenzie
    try{
      fontCool = Font.createFont(Font.PLAIN, is).deriveFont(32f);
    }
     catch(IOException ex){
      System.out.println(ex); 
     }
     catch(FontFormatException ex){
      System.out.println(ex); 
     }
  }
  
  static int randint(int low, int high){
    return(int)(Math.random() * (high-low+1)+low);
  }
  
  public void draw(Graphics g){
    //used to testing clickbox area
    // g.setColor(new Color(255, 255, 255));
    // g.fillRect(x-7,y-37,w,h); // -7, -37
    //g.setColor(new Color(0,0,0));
     g.setFont(fontCool);
    
    //draws the addional font detail
    /*for(int fr = 0; fr < 7; fr++){
        g.drawString(contents, x-fr, y-fr);
      }*/
    if(color == 255 || color == 99){
      colorChange*=-1;
    }
    color+=colorChange;
    
    g.setColor(new Color(color,color,color));
    g.drawString(contents, x, y);
    
  }
  
  //provides an area for interaction
  public Rectangle clickBox(){
    return new Rectangle(x-7,y-37, w, h);
  }
}