/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import java.awt.*;

public class GUI extends java.applet.Applet
{
    TextArea ta;
    GUI gui;
    boolean proceed = false;
    TextField username;
    TextField password;
    TextField marketId;

    public void init()
    {
        // upper panel
        Panel p;
        setLayout(new BorderLayout());

        // top panel
        p = new Panel();
        Label text1 = new Label("Username:");
        p.add(text1);
        username = new TextField();
        username.setColumns(5);
        p.add(username);
        Label text2 = new Label("Password:");
        p.add(text2);
        password = new TextField();
        password.setColumns(5);
        p.add(password);
        Label text3 = new Label("Market Id:");
        p.add(text3);
        marketId = new TextField();
        marketId.setColumns(5);
        p.add(marketId);
        add("North", p);
        
        // center panel
        p = new Panel();       
        ta = new TextArea();
        p.add(ta);
        add("Center", p);

        // bottom panel
        p = new Panel();
        add("South", p);
        p.add(new Button("Go"));
    }

    public boolean action(Event e, Object o)
    {
        // check username
        if(username.getText().equals("")) {
            textArea().append("Please Enter a Username\n");
        }
        // check password
        else if(password.getText().equals("")) {
            textArea().append("Please Enter a Password\n");
        }
        // check market id
        else if(marketId.getText().equals("")) {
            textArea().append("Please Enter a Market Id\n");
        }
        // proceed...
        else {
            // set flag to allow app to continue.
            // bad but quick solution. should implement listener
            textArea().append("Please Wait...\n");
            proceed = true;
        }

        return false;
    }

    public void create()
    {
        Frame f = new Frame("AgentTrader");
        gui = new GUI();
        gui.init();
        f.add("Center", gui);
        f.pack();
        f.setVisible(true);
    }

    // returns text area in GUI
    public TextArea textArea() {
        return ta;
    }

    // returns gui instance
    public GUI getGui() {
        return gui;
    }

    public boolean proceed() {
        return proceed;
    }

    public String username() {
        return username.getText().toString();
    }

    public String password() {
        return password.getText().toString();
    }

    public String marketId() {
        return marketId.getText().toString();
    }
}