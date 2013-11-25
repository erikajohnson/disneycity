package restaurant_haus.gui;

import restaurant_haus.CashierAgent;
import restaurant_haus.CookAgent;
import restaurant_haus.CustomerAgent;
import restaurant_haus.HostAgent;
import restaurant_haus.MarketAgent;
import restaurant_haus.WaiterAgent;
import simcity.PersonAgent;
import simcity.gui.SimCityGui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;

import javax.swing.JTabbedPane;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantHaus extends JPanel {

    //Host, cook, waiters and customers
    private HostAgent host;
    private CashierAgent cashier;
    CookAgent cook;
    
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    
    private boolean isPaused = false;
    
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui
    
    private JPanel creditsPanel;
    private JLabel nameLabel;
    private JLabel pictureLabel;
    private ImageIcon personalPicture; 
    
    private JTabbedPane restruantPane = new JTabbedPane();

    public RestaurantHaus(SimCityGui gui) {
        this.gui = gui;
        
        MarketAgent tempMarket;
        tempMarket = new MarketAgent("Best Market", 5000, 10, 0, 6, 10);
        tempMarket.startThread();
        markets.add(tempMarket);
        
        tempMarket = new MarketAgent("The Other Market", 7000, 7, 4, 8, 0);
        tempMarket.startThread();
        markets.add(tempMarket);
        
        tempMarket = new MarketAgent("Easy & Fresh", 10000, 3, 6, 4, 9);
        tempMarket.startThread();
        markets.add(tempMarket);
        
        
        
        setLayout(new GridLayout(1, 1));
        group.setLayout(new GridLayout(1, 3, 10, 10));

        group.add(customerPanel);

        initRestLabel();
        
        restruantPane.addTab("Menu", restLabel);
        restruantPane.addTab("Customers", group);
        restruantPane.addTab("Waiters", waiterPanel);
        restruantPane.addTab("Credits", creditsPanel);
        
        add(restruantPane);
        //add(restLabel);
        //add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + "name" + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
  /*  public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if (type.equals("Waiters")) {
        	for (WaiterAgent waiter : waiters) {
        		if(waiter.getName() == name) {
        			gui.updateInfoPanel(waiter);
        		}
        	}
        }
    }
    */

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(PersonAgent p, String type, String name, double money) {

    	if (type.equals("Customer")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		if (p!=null) c.setPerson(p);
    		CustomerGui g = new CustomerGui(c, gui);
    		gui.hausAniPanel.addGui(g);// dw
    		if (host!=null) c.setHost(host);
    		c.setGui(g);
    		c.setMoney(money);
    		customers.add(c);
    		c.startThread();
    		c.getGui().setHungry();
    	}
    	if(type.equals("Waiter")) {
    		WaiterAgent w = new WaiterAgent(name);
    		if (p!=null) w.setPerson(p);
    		WaiterGui g = new WaiterGui(w, gui);    		
    		gui.hausAniPanel.addGui(g);// dw
    		if (host!=null) w.setHost(host);
    		if (cashier!= null) w.setCashier(cashier);
    		w.setGui(g);
    		if (host!=null) host.msgGiveJob(w);
    		if (cook!= null) w.setCook(cook);
    		waiters.add(w);
    		w.startThread();
    	}
    	else if (type.equals("Host")) {
    		host = new HostAgent(name);
    		if (p!=null) host.setPerson(p);
    		for (WaiterAgent w : waiters) {
    			w.setHost(host);
    			host.msgGiveJob(w);
    		}
    		if (cashier!=null) cashier.setMenu(host.getMenu());
    		if (cook!=null) cook.setMenu(host.getMenu());
    		host.startThread();
    	}
    	else if (type.equals("Cook")) { 
    		cook = new CookAgent(name);
    		if (p!=null) cook.setPerson(p);
    		CookGui cG = new CookGui(cook, gui);
    		gui.hausAniPanel.addGui(cG);
    	    cook.setGui(cG);
            if (host!=null) cook.setMenu(host.getMenu());
            if (cashier!=null) cook.setCashier(cashier);
            for (MarketAgent m : markets) {
            	cook.addMarket(m);
            	m.setCook(cook);
            }
            for (WaiterAgent w : waiters) {
            	w.setCook(cook);
            }
    	    cook.startThread();
    	}
    	else if (type.equals("Cashier")) { 
    		cashier = new CashierAgent(name);
    		if (p!=null) cashier.setPerson(p);
    		if (host!=null) cashier.setMenu(host.getMenu());
    		for (WaiterAgent w : waiters) {
    			w.setCashier(cashier);
    		}
    		if (cook!=null) cook.setCashier(cashier);
    		cashier.startThread();
    	}
 
        
    	
    }
    
    public void pause() {
    	if(isPaused) {
    		isPaused = false;
    		cook.unpause();
    		host.unpause();
    		for(CustomerAgent c : customers) {
    			c.unpause();
    		}
    		for(WaiterAgent w : waiters) {
    			w.unpause();
    		}
    		//gui.animationPanel.unpauseAnim();
    	}
    	
    	else {
    		isPaused = true;
    		cook.pause();
    		host.pause();
    		for(CustomerAgent c : customers) {
    			c.pause();
    		}
    		for(WaiterAgent w : waiters) {
    			w.pause();
    		}
    		//gui.animationPanel.pauseAnim();
    	}
    }
}