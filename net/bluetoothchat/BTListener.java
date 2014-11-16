package net.bluetoothchat;

/**
 * Interface for BlueChat NetLayer callback.
 * <p>Description: Implementation of this interface will handle BlueChat network event.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
public interface BTListener
{
  public final static String EVENT_JOIN = "join";
  public final static String EVENT_LEAVE = "leave";
  public final static String EVENT_RECEIVED = "received";
  public final static String EVENT_SENT = "sent";


  /**
   * BlueChat network activity action handler.
   * @param action must be one of NetLayer.ACT_XXX field
   * @param param1 usually the EntPoint object that correspond to the action
   * @param param2 varies by action value
   */
  public void handleAction( String action, Object param1, Object param2 );
}