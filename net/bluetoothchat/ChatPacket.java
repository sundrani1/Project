package net.bluetoothchat;

/**
 * A holder object for BlueChat network packet data.
 * <p>Description: ChatPacket can represent severl type of message, which is defined
 * by NetLayer.SIGNAL_XXX enumeration. The common type is SIGNAL_MESSAGE, which
 * hold an user entered message to sent across the virtual chat room.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */

public class ChatPacket
{
  // signal, must be one of NetLayer.SIGNAL_XXX
  public int signal;
  // indicate the nick name of the sender
  public String sender;
  // the message content
  public String msg;

  public ChatPacket(int signal, String msg)
  {
    this.signal = signal;
    this.msg = msg;
  }

  public ChatPacket(int signal, String sender, String msg)
  {
    this.signal = signal;
    this.sender = sender;
    this.msg = msg;
  }

  public ChatPacket()
  {
  }

}