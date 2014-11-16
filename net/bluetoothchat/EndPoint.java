package net.bluetoothchat;
import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.*;


/**
 * A EndPoint object represent all the connection attribute of an active BlueChat node.
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
public class EndPoint
{
  // remote device object
  RemoteDevice remoteDev;
  // remote device class
  DeviceClass remoteClass;
  // remote service URL
  String remoteUrl;
  // connection to remote service
  StreamConnection con;
  // bluetooth discovery transId, obtainsed from searchServices
  int transId = -1; // -1 must be used for default. cannot use 0

  // sender thread
  Sender sender;
  // reader thread
  Reader reader;

  // local user nick name
  String localName;
  // remote user nick name
  String remoteName;

  // BTListener implementation for callback NetLayer event
  BTListener callback;

  // reference to NetLayer
  NetLayer btnet;

  // vector of ChatPacket pending to be sent to remote service.
  // when message is sent, it is removed from the vector.
  Vector msgs = new Vector();

  public EndPoint( NetLayer btnet, RemoteDevice rdev, StreamConnection c )
  {
    this.btnet = btnet;

    remoteDev = rdev;

    try {
      // NOTE in 6600, this parameter must be false because
      // according to some observation from other developer
      // setting this to true mean the Bluetooth system need to make
      // another connection to remote device, however, there is no available
      // free connection, so it will give you exception
      remoteName = rdev.getFriendlyName(false); // this is a temp name
    }
    catch (IOException ex) {
      remoteName = "Unknown";
      // ignore
    }
    localName = btnet.localName;
    callback = btnet.callback;
    con = c;

    sender = new Sender();
    sender.endpt = this;

    reader = new Reader();
    reader.endpt = this;


  }

  public synchronized void putString( int signal, String s )
  {
    log("invoke putString "+signal+" "+s);
    // put the message on the queue, pending to be sent by Sender thread
    msgs.addElement( new ChatPacket( signal, s ) );
    synchronized( sender )
    {
      // tell sender that there is a message pending to be sent
      sender.notify();
    }
  }

  public synchronized ChatPacket getString()
  {
//    log("invoke getString()");
    if ( msgs.size() > 0 )
    {
      // if there are message pending, return it and remove it from the vector
      ChatPacket s = (ChatPacket) msgs.firstElement();
      msgs.removeElementAt(0);
      return s;
    } else
    {
      // if there is no message pending. return null
      return null;
    }
  }

  public synchronized boolean peekString()
  {
    return ( msgs.size() > 0 );
  }

  private static void log( String s )
  {
    System.out.println("EndPoint: "+s);
  }
}