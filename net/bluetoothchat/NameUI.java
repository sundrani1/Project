package net.bluetoothchat;

import javax.microedition.lcdui.*;

/**
 *
 * <p>Title: A screen to enter local user name</p>
 * <p>Description: This is a screen for user to enter a nick name for the virtual
 * chat room. User must enter a nick name, in order for BlueChat to operate correctly
 * however it is not enforced. </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
public class NameUI extends Form
{

  TextField text;
  public NameUI()
  {
    super("Enter Your Name");
    setCommandListener( ChatMain.instance );

    addCommand(new Command("Chat", Command.SCREEN, 1));
    //addCommand(new Command("Chat (Debug)", Command.SCREEN, 1));

    append( new StringItem( "", "Please select your nickname for chat session." ) );
    append( text = new TextField( "Your Name", "", 10, TextField.ANY ) );
  }
}
