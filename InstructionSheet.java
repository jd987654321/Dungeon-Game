import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

//I just made a class for this to make to easier to work solely on this
public class InstructionSheet{
  
  private int frame;
  private Image[] walk;
  private Image[] attack;
  private Image[] roll;
  
  public InstructionSheet(){
    /*for(int dir = 0; dir < 3; dir++){
      for(int fra = 0; fra < 4; fra++){
        walk[fra] = ImageIcon("SpriteAnimations/Character with sword and shield/idle/idle " + directions[dir] + (fra+1) + ".png").getImage();
        attack[fra] = new ImageIcon("SpriteAnimations/Character with sword and shield/walk/walk " + directions[dir] + (fra+1) + ".png").getImage();
        roll[fra] = new ImageIcon("SpriteAnimations/Character with sword and shield/attack/attack " + directions[dir] + (fra+1) + ".png").getImage();
      }
    }*/
  }
  
  public void draw(Graphics g){
    
  }
}