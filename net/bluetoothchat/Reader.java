package net.bluetoothchat;

/**
 * Reader thread that read in signal and data from a bluetooth connection.
 * <p>Description: Reader is a Runnable implementation that read in signal and data (String)
 *  from connected DataInputStream. Each EndPoint has it own reader thread.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
import java.io.*;

public class Reader implements Runnable
{
  // end point that this reader reads data from
  public EndPoint endpt;

  private boolean done = false;

  public Reader() {
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
      DataInputStream datain = endpt.con.openDataInputStream();

      while ( !done )
      {
        log("waiting for next signal from "+endpt.remoteName);
        // read in the next signal (an integer)
        // this will block until there is data to read
        int signal = datain.readInt();

        if ( signal == NetLayer.SIGNAL_MESSAGE )
        {
          String s = datain.readUTF();

          ChatPacket packet = new ChatPacket( NetLayer.SIGNAL_MESSAGE, endpt.remoteName, s );

          log("read in MESSAGE string '"+s+"' from "+endpt.remoteName);

          // read in a string message. emit RECEIVED event to BTListener implementation
          endpt.callback.handleAction( BTListener.EVENT_RECEIVED, endpt, packet );

        } else if ( signal == NetLayer.SIGNAL_HANDSHAKE )
        {
          String s = datain.readUTF();
          log("read in HANDSHAKE name "+s+" from "+endpt.remoteName);
          // update the remote user nick name
          endpt.remoteName = s;

          // echo acknowledgment and local user friendly name back to remote device
          endpt.putString( NetLayer.SIGNAL_HANDSHAKE_ACK, endpt.localName );


          endpt.callback.handleAction( BTListener.EVENT_JOIN, endpt, null );

        } else if ( signal == NetLayer.SIGNAL_TERMINATE )
        {
          log("read in TERMINATE from "+endpt.remoteName);

          // echo acknowledgment and local friendly name back to remote device
          endpt.putString( NetLayer.SIGNAL_TERMINATE_ACK, "end" );

          // emit LEAVE event to BTListener implementation
          endpt.callback.handleAction( BTListener.EVENT_LEAVE, endpt, null );

          // clean up end point resources and associated connections
          endpt.btnet.cleanupRemoteEndPoint( endpt );

          // stop this reader, no need to read any more signal
          stop();

        } else if ( signal == NetLayer.SIGNAL_HANDSHAKE_ACK )
        {
          // the string data is the remote user nick name
          String s = datain.readUTF();
          log("read in  HANDSHAKE_ACK name "+s+" from "+endpt.remoteName);
          // update remote user nick name
          endpt.remoteName = s;

        } else if ( signal == NetLayer.SIGNAL_TERMINATE_ACK )
        {

          System.out.println("read in TERMINATE_ACK from "+endpt.remoteName);
          // doesn't do anything, just wake up from readInt() so that the thread can stop


        } else
        {
          log("Unkonwn signal, probably connection closed");
        }

      } // while !done

      datain.close();
    } catch (Exception e)
    {
      e.printStackTrace();
      log(e.getClass().getName()+" "+e.getMessage());
    }
    log("reader thread exit for "+endpt.remoteName);

  }
  private static void log( String s)
  {
    System.out.println("Reader: "+s);

    // "R" means Reader class
    if ( ChatMain.isDebug )
      ChatMain.instance.gui_log( "R", s );



  }

}