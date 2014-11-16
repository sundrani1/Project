package net.bluetoothchat;

import javax.microedition.lcdui.*;

/**
 * A screen for entering chat message.
 * <p>Description: This is a screen for user to enter chat message. User should press
 * Send button to send the message to the virtual chat room. Message is limited
 * to 200 characters.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
public class InputUI extends TextBox {

  /**Construct the displayable*/
  public InputUI() {
    super("Enter Message", "", 200, TextField.ANY);
    addCommand(new Command("Send", Command.SCREEN, 1));
    addCommand(new Command("Back", Command.SCREEN, 1));
    addCommand(new Command("Erase", Command.SCREEN, 1));
    addCommand(new Command("Backspace", Command.SCREEN, 1));
    setCommandListener( ChatMain.instance );
  }


  public void showUI()
  {
    this.setString("");
  }

}
