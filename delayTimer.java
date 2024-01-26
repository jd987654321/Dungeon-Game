import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;


class delayTimer{
  //A useful timer class used to set delays 
  private long time;
  
  //in the case of pausing the screen, this variable will
  //hold the period of time passed while paused
  private long pauseTime;
    
  public delayTimer(){
    time = System.currentTimeMillis();
    pauseTime = 0;
  }
    
  //the most popular and fabulous delay method
  //returns true if d milliseconds have passed
  public boolean delay(int d){
    long newTime = System.currentTimeMillis();
    if((newTime - d) > (time + pauseTime)){
      time = newTime;
      return true;
    }
    else{
      return false;      
    }
   }
  
  //returns time passed since last reset
  public long tellTime(){
    long newTime = System.currentTimeMillis();
    long timepassed = (newTime - time) /1000;
    return timepassed;
  }
  
  //adds time to the pauseTime variable (used for in game pauses)
  public void addPauseTime(){
    pauseTime = System.currentTimeMillis() - time;
  }
  
  public void reset(){
    time = System.currentTimeMillis();
  }
  
  public void prtTime(){
    System.out.println(time);
  }
  
}