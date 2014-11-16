package net.bluetoothchat;

/**
 * Sender thread that send out signal and data to a bluetooth connection.
 * <p>Description: Sender is a Runnable implementation that send signal and data (String)
 *  to connected DataInputStream. Each EndPoint has it own sender thread.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
import java.io.*;

public class Sender implements Runnable
{
  // end point that this sender sends data to
  public EndPoint endpt;

  private boolean done = false;

  public Sender()
  {
  }

  /**
   * set 'done' flag to true, which will exit the while loop
   */
  public void stop()
  {
    done = true;
  }

  public void run()
  {
    try
    {
      DataOutputStream dataout = endpt.con.openDataOutputStream();
      while( !done )
      {

        // check to see if there are any message to send.
        // if not, then wait for 5 second
        if ( ! endpt.peekString()  )
        {
          synchronized (this) {
            this.wait(5000);
          }
        }

        // wake up and get next string
        ChatPacket s = endpt.getString();

        if ( s != null )
        {
          // if there is a message to send, send it now
          log("sending signal "+s.signal+" string '"+s.msg+"' to "+endpt.remoteName);
          dataout.writeInt(s.signal);
          dataout.writeUTF(s.msg );
          dataout.flush();
        }

        if ( s != null && s.signal == NetLayer.SIGNAL_TERMINATE )
        {
          // if the message is a TERMINATE signal, then break the run loop as well
          stop();
        }

      } // while !done

      // close the output stream
      dataout.close();
    } catch (Exception e)
    {
      e.printStackTrace();
      log(e.getClass().getName()+" "+e.getMessage());
    }
    log("sender thread exit for "+endpt.remoteName);

  }
  private static void log( String s)
  {
    System.out.println("Sender: "+s);

    // "S" means Sender class
    if ( ChatMain.isDebug )
      ChatMain.instance.gui_log( "S", s );

  }

}