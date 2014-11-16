package net.bluetoothchat;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * Main MIDlet class that execute BlueChat application.
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author P Coder
 * @version 1.0
 */
public class ChatMain extends MIDlet implements BTListener, CommandListener {
    // shared static variables

    public static ChatMain instance;
    public static Display display;

    // debug flag
    public static boolean isDebug = false;

    // Chat app GUI components
    public InputUI inputui;
    public MessageUI messageui;
    public NameUI nameui;
    // Bluetooth network layer for BlueChat app
    public NetLayer btnet;

    /** Constructor */
    public ChatMain() {
        instance = this;
    }

    /** Handle starting the MIDlet */
    public void startApp() {
        log("invoke startApp()");

        // obtain reference to Display singleton
        display = Display.getDisplay(this);

        // initialize the GUI component, and prompt for name input
        inputui = new InputUI();
        messageui = new MessageUI();
        nameui = new NameUI();
        display.setCurrent(nameui);

    }

    /** Handle pausing the MIDlet */
    public void pauseApp() {
        log("invoke pauseApp()");
    }

    /** Handle destroying the MIDlet */
    public void destroyApp(boolean unconditional) {
        log("invoke destroyApp()");
    }

    /** Quit the MIDlet */
    public static void quitApp() {
        instance.destroyApp(true);
        instance.notifyDestroyed();
        instance = null;
        display = null;
    }

    /**
     * Handle event/activity from Bluetooth Network layer.
     * This class is an implementation of BTListener; therefore, it handles
     * all the bluetooth network event that received by NetLayer.
     * The list of possible event are defined in BTListener.EVENT_XXX.
     * @param action event type. see NetLayer constants
     * @param param1 parameter 1 is usually the remote EndPoint that trigger the action
     * @param param2 parameter 2 is usually the argument of the action
     */
    public void handleAction(String event, Object param1, Object param2) {
        log("invoke handleAction. action=" + event);

        if (event.equals(BTListener.EVENT_JOIN)) {
            // a new user has join the chat room
            EndPoint endpt = (EndPoint) param1;
            String msg = endpt.remoteName + " joins the chat room";
            ChatPacket packet = new ChatPacket(NetLayer.SIGNAL_HANDSHAKE, endpt.remoteName, msg);

            // display the join message on screen
            messageui.msgs.addElement(packet);
            messageui.repaint();

        } else if (event.equals(BTListener.EVENT_SENT)) {
            // nothing to do
        } else if (event.equals(BTListener.EVENT_RECEIVED)) {
            // a new message has received from a remote user
            EndPoint endpt = (EndPoint) param1;
            ChatPacket msg = (ChatPacket) param2;
            // render this message on screen
            messageui.msgs.addElement(msg);
            messageui.repaint();

        } else if (event.equals(BTListener.EVENT_LEAVE)) {
            // a user has leave the chat room
            EndPoint endpt = (EndPoint) param1;
            String msg = endpt.remoteName + " leaves the chat room";
            ChatPacket packet = new ChatPacket(NetLayer.SIGNAL_TERMINATE, endpt.remoteName, msg);
            // display the leave message on screen
            messageui.msgs.addElement(packet);
            messageui.repaint();


        }

    }

    /**
     * Handle user action from BlueChat application.
     * @param c GUI command
     * @param d GUI display object
     */
    public void commandAction(Command c, Displayable d) {
        log("invoke commandAction. command=" + c.getLabel());
        if (d == inputui && c.getLabel().equals("Send")) {
            String msg = inputui.getString();
            // send the message to all connected BlueChat remote EndPoints
            btnet.sendString(msg);

            // update the message screen to reflect the entered message.
            // create a dummy packet object to hold the entered message.
            ChatPacket packet = new ChatPacket(NetLayer.SIGNAL_MESSAGE, btnet.localName, msg);
            messageui.msgs.addElement(packet);
            display.setCurrent(messageui);
            messageui.repaint();

        } else if (d == nameui && (c.getLabel().equals("Chat") || c.getLabel().equals("Chat (Debug)"))) {
            // turn on debug logging on screen
            // see log() method (there are several)
            if (c.getLabel().equals("Chat (Debug)")) {
                ChatMain.isDebug = true;
            }

            // user enters virtual chat room.
            // create and initialize Bluetooth network layer
            btnet = new NetLayer();
            String localName = nameui.text.getString();
            log("set local nick name to " + localName);

            // initialize the network layer. This will start the local BlueChat server
            btnet.init(localName, this);

            // search for existing BlueChat nodes
            btnet.query();

            // switch screen to message screen
            display.setCurrent(messageui);

        } else if (d == inputui && c.getLabel().equals("Back")) {
            // just does nothing and return to message screen
            display.setCurrent(messageui);

        } else if (d == messageui && c.getLabel().equals("Write")) {
            // enter input screen
            display.setCurrent(inputui);
            inputui.showUI(); // clear the input text field

        } else if (d == messageui && c.getLabel().equals("Clear")) {
            // clear the history of message and refresh the message screen
            messageui.msgs.removeAllElements();
            messageui.repaint();

        } else if (d == messageui && c.getLabel().equals("Exit")) {
            // disconnect from the virtual chat room.
            // this will send out TERMINATE signal to all connected
            // remote EndPoints, wait for the TERMINATE_ACK signal, and
            // disconnect all connections.
            btnet.disconnect();
            quitApp();

        } else if (d == inputui && c.getLabel().equals("Erase")) {

            // this will clear the InputUI's content so that
            // if a user has made any mistake he/she can rewrite it!

            inputui.setString("");
            log("Erased");

        } else if (d == inputui && c.getLabel().equals("Backspace")) {
            // this will clear the InputUI's last character
            // Similar to clear button in Writing Phone's SMS
            String str = inputui.getString();
            if (str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            inputui.setString(str);
            log("Erased");

        } else if (d == messageui && c.getLabel().equals("About Bluetooth Chat")) {
            Alert alert = new Alert("About", "Bluetooth Chat 1.0. (c) 2009.", null, AlertType.INFO);
            alert.setTimeout(Alert.FOREVER);
            display.setCurrent(alert, messageui);

        }

    }

    private static void log(String s) {
        System.out.println("ChatMain: " + s);

        // "M" means Main class
        if (ChatMain.isDebug) {
            ChatMain.instance.gui_log("M", s);
        }
    }

    public static void gui_log(String source, String s) {
        ChatPacket packet = new ChatPacket(NetLayer.SIGNAL_MESSAGE, source, s);
        instance.messageui.msgs.addElement(packet);
        instance.messageui.repaint();

    }
}
