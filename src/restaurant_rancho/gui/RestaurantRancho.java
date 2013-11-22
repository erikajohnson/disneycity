package restaurant_rancho.gui;

import agent_rancho.Agent;
import restaurant_rancho.CashierAgent;
import restaurant_rancho.CookAgent;
import restaurant_rancho.CustomerAgent;
import restaurant_rancho.HostAgent;
import restaurant_rancho.MarketAgent;
import restaurant_rancho.WaiterAgent;
import restaurant_rancho.interfaces.Bank; 

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantRancho extends JPanel {

    //Host, cook, waiters and customers
	String name;
	Bank bank;
    private HostAgent host;
    private CookAgent cook;
    private CashierAgent cashier;
   // private MarketAgent market = new MarketAgent("Whole Foods", 0, 0, 0, 7, 0);
   // private MarketAgent market2 = new MarketAgent("Ralphs", 20, 20, 20, 20, 20);
   // private MarketAgent market3 = new MarketAgent("Costco", 20, 20, 20, 20, 20);
    
    private List<WaiterAgent> waiters = new ArrayList<WaiterAgent>();
    private List<CustomerAgent> customers = new ArrayList<CustomerAgent>();
    private List<MarketAgent> markets = new ArrayList<MarketAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel (this, "Waiters");
    private JPanel group = new JPanel();


    private RestaurantRanchoGui gui; //reference to main gui
    private CookGui cookgui;

    //public RestaurantBase(RestaurantGui gui, String n) {
    public RestaurantRancho(RestaurantRanchoGui g, String n) {
    	name = n;
        this.gui = g;
        //host.startThread();
        // market.setCashier(cashier);
        // market2.setCashier(cashier);
        // market3.setCashier(cashier);
        // market.startThread();
        // market2.startThread();
        // market3.startThread();
        // cashier.startThread();
        // cook.addMarket(market);
        // cook.addMarket(market2);
        // cook.addMarket(market3);
        // cook.setGui(cookgui);
        // gui.animationPanel.addGui(cookgui);
        //cook.startThread();
        //cookgui.updatePosition();
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        group.add(customerPanel);
        group.add(waiterPanel);

        
        add(restLabel);
        add(group);
    }
    public RestaurantRanchoGui getGui() {
    	return gui;
    }
    
    public void setBank(Bank b) {
    	bank = b;
    }
    
    
   // public void personAs(String type, String name, PersonAgent p) {
    public void personAs(String type, String name){
    	addPerson(type, name, true);
    }
    public void PauseandUnpauseAgents() {
    	for (WaiterAgent w : waiters) {
    		w.pauseOrRestart();
    	}
    	for (CustomerAgent c : customers) {
    		c.pauseOrRestart();
    	}
    	cook.pauseOrRestart();
    	host.pauseOrRestart(); 	
    	for (MarketAgent m : markets) {
    		m.pauseOrRestart();
    	}
    	cashier.pauseOrRestart();
    }
    
    public void waiterWantsBreak(WaiterAgent w) {
    	host.msgWantBreak(w);
    }
    
    public void waiterWantsOffBreak(WaiterAgent w) {
    	host.msgBackFromBreak(w);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() +  "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$14.50</td></tr><tr><td>Chicken</td><td>$12.50</td></tr><tr><td>Salad</td><td>$7.50</td></tr><tr><td>Pizza</td><td>$10.50</td></tr><tr><td>Latte</td><td>$3.25</td></tr></table><br></html>");
        label.setFont(new Font("Helvetica", Font.PLAIN, 13));
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
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiters")) {
        	for (int i = 0; i < waiters.size(); i++) {
        		WaiterAgent temp = waiters.get(i);
        		if(temp.getName() == name) {
        			gui.updateWInfoPanel(temp);
        		}
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean isHungry) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui, customers.size());
    	//new	
    		if(isHungry==true) { 
    			g.setHungry();
    		}
    		// new
    		gui.animationPanel.addGui(g);// dw
    		if (host!=null) c.setHost(host);
    		c.setGui(g);
    		c.setCashier(cashier);
    		Random rand = new Random();
    		c.setCash(rand.nextInt(20)+10);
    		//c.setCash(10);
    		customers.add(c);
    		c.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name);
    		WaiterGui g = new WaiterGui(w, waiters.size());
    		gui.animationPanel.addGui(g);
    		if (host!=null) w.setHost(host);
    		if (cook!= null) w.setCook(cook);
    		if (cashier!=null)w.setCashier(cashier);
    		if (host!=null) host.addWaiter(w);
    		w.setGui(g);
    		waiters.add(w);
    		w.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Host")) {
    		if (host == null) {
    			host = new HostAgent(name);
    			host.startThread();
    			initRestLabel();
    			for (WaiterAgent w : waiters) {
    				host.addWaiter(w);
    				w.setHost(host);
    			}
    		}
    	}
    	else if (type.equals("Cook")) {
    		if (cook == null) {
    			cook = new CookAgent(name);
    			cookgui = new CookGui(cook, gui);
    			cook.setGui(cookgui);
    			cookgui.updatePosition();
    			cook.startThread();
    			for (WaiterAgent w : waiters) {
    				w.setCook(cook);	
    			}
    			for (MarketAgent m : markets) {
    				cook.addMarket(m);
    			}
    			gui.animationPanel.addGui(cookgui);
    		}
    	}
    	else if (type.equals("Cashier")) {
    		if (cashier == null) {
    			cashier = new CashierAgent(name);
    			cashier.startThread();
    			for (WaiterAgent w : waiters) {
    				w.setCashier(cashier);
    			}
    			for (MarketAgent m : markets) {
    				m.setCashier(cashier);
    			}
    		}
    	}
    	else if (type.equals("Market")) {
    		MarketAgent market = new MarketAgent(name, 10, 10, 10, 10, 10);
    		markets.add(market);
    		if (cashier!=null) market.setCashier(cashier);
    		if (cook!= null) cook.addMarket(market);
    		market.startThread();
    		
    	}
    			
    			
    		
    		
    }
    

}