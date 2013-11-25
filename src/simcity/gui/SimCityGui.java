package simcity.gui;

import housing.Housing;
import housing.ResidentAgent.State;
import housing.gui.HousingAnimationPanel;
import housing.test.mock.LoggedEvent;
import bank.gui.Bank;
import bank.gui.BankAnimationPanel;

import javax.swing.*;

import market.Market;
import market.gui.MarketAnimationPanel;

import java.awt.*;
import java.awt.event.*;

import restaurant_bayou.gui.BayouAnimationPanel;
import restaurant_bayou.gui.RestaurantBayou;
import restaurant_rancho.gui.RanchoAnimationPanel;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_pizza.gui.RestaurantPizza;
import restaurant_pizza.gui.PizzaAnimationPanel;
import restaurant_haus.gui.HausAnimationPanel;
import restaurant_haus.gui.RestaurantHaus;
import simcity.PersonAgent;
import transportation.TransportationPanel;
import restaurant_cafe.gui.CafeAnimationPanel;
import restaurant_cafe.gui.RestaurantCafe;

public class SimCityGui extends JFrame implements ActionListener  {

	public static final int WINDOWX = 800;
	public static final int WINDOWY = 600;
	
	String name;
	
	SimCityPanel simCityPanel;
	
	JPanel cards;
	
	enum Panel {Mansion, Apt1, Apt2, Apt3, Apt4, Apt5, Apt6, Rancho, Bayou, Market, Bank, Pizza, Cafe, Haus};
	Panel currP;
			
	public static RestaurantRancho restRancho;
	public RanchoAnimationPanel ranchoAniPanel = new RanchoAnimationPanel();
	
	public static RestaurantBayou restBayou;
	public BayouAnimationPanel bayouAniPanel = new BayouAnimationPanel();
	
	public static RestaurantPizza restPizza;
	public PizzaAnimationPanel pizzaAniPanel = new PizzaAnimationPanel();
	
	public static Housing mainStApts1;
	public HousingAnimationPanel housAniPanel1 = new HousingAnimationPanel();
	
	public static Housing mainStApts2;
	public HousingAnimationPanel housAniPanel2 = new HousingAnimationPanel();
	
	public static Housing mainStApts3;
	public HousingAnimationPanel housAniPanel3 = new HousingAnimationPanel();
	
	public static Housing mainStApts4;
	public HousingAnimationPanel housAniPanel4 = new HousingAnimationPanel();
	
	public static Housing mainStApts5;
	public HousingAnimationPanel housAniPanel5 = new HousingAnimationPanel();
	
	public static Housing mainStApts6;
	public HousingAnimationPanel housAniPanel6 = new HousingAnimationPanel();
	
	public static Housing hauntedMansion;
	public HousingAnimationPanel housAniPanel7 = new HousingAnimationPanel();	
	
	public static Market mickeysMarket;
	public MarketAnimationPanel markAniPanel = new MarketAnimationPanel();
		
	public static Bank pirateBank;
	public BankAnimationPanel bankAniPanel = new BankAnimationPanel();
	
	public static RestaurantCafe restCafe;
	public CafeAnimationPanel cafeAniPanel = new CafeAnimationPanel();
	
	public static RestaurantHaus restHaus; 
	public HausAnimationPanel hausAniPanel = new HausAnimationPanel();
	
	TransportationPanel cityAniPanel = new TransportationPanel(this);
	private JPanel cityBanner = new JPanel();
	private JPanel zoomBanner = new JPanel();
	private JPanel cityAnimation = new JPanel();
	private JPanel zoomAnimation = new JPanel();
	private JPanel cityPanel = new JPanel();
	private JPanel zoomPanel = new JPanel();
	private JLabel cityLabel = new JLabel("Disney City View                                                   ");
	private JLabel zoomLabel = new JLabel("Click on a Building to see Animation Inside");
	private JButton panelB = new JButton("next panel");
		
	public SimCityGui(String name) {
		
//		int delay = 1000; //milliseconds
//		  ActionListener taskPerformer = new ActionListener() {
//		      public void actionPerformed(ActionEvent evt) {
////		          housAniPanel.update();
//		      }
//		  };
//		  new Timer(delay, taskPerformer).start();
				
		cards = new JPanel(new CardLayout());
		cards.add(cafeAniPanel, "Cafe");
		cards.add(housAniPanel7, "Mansion");
		cards.add(housAniPanel1, "Apt1");
		cards.add(housAniPanel2, "Apt2");
		cards.add(housAniPanel3, "Apt3");
		cards.add(housAniPanel4, "Apt4");
		cards.add(housAniPanel5, "Apt5");
		cards.add(housAniPanel6, "Apt6");
		cards.add(markAniPanel, "Market");
		cards.add(bankAniPanel, "Bank");
		cards.add(ranchoAniPanel, "Rancho");
		cards.add(bayouAniPanel, "Bayou");
		cards.add(pizzaAniPanel, "Pizza");
		cards.add(cafeAniPanel, "Cafe");
		cards.add(hausAniPanel, "Haus");
				
		panelB.addActionListener(this);
		panelB.setPreferredSize(new Dimension(0, 0));
		currP = Panel.Apt1;
					
		// Restaurants etc. must be created before simCityPanel is constructed, as demonstrated below
		restRancho = new RestaurantRancho(this, "Rancho Del Zocalo");
		restBayou = new RestaurantBayou(this, "The Blue Bayou");
		restPizza = new RestaurantPizza(this);
		restHaus = new RestaurantHaus(this);
		restCafe = new RestaurantCafe(this, "Carnation Cafe");
		hauntedMansion = new Housing(housAniPanel7, "Haunted Mansion");
		mainStApts1 = new Housing(housAniPanel1, "Main St Apartments #1");
		mainStApts2 = new Housing(housAniPanel2, "Main St Apartments #2");
		mainStApts3 = new Housing(housAniPanel3, "Main St Apartments #3");
		mainStApts4 = new Housing(housAniPanel4, "Main St Apartments #4");
		mainStApts5 = new Housing(housAniPanel5, "Main St Apartments #5");
		mainStApts6 = new Housing(housAniPanel6, "Main St Apartments #6");
		mickeysMarket = new Market(this, "Mickey's Market");
		pirateBank = new Bank(this, "Pirate Bank");
		
		simCityPanel = new SimCityPanel(this);
		
		setLayout(new GridBagLayout());
		setBounds(WINDOWX/20, WINDOWX/20, WINDOWX, WINDOWY);
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.BOTH;
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = .5;
		c1.gridx = 0;
		c1.gridy = 0; 
		c1.gridwidth = 3;
		cityBanner.setBorder(BorderFactory.createTitledBorder("City Banner"));
		cityBanner.add(cityLabel);
		add(cityBanner, c1);
		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.BOTH;
		c3.weightx = .5; 
		c3.gridx = 5;
		c3.gridy = 0;
		c3.gridwidth = 5;
		zoomBanner.setBorder(BorderFactory.createTitledBorder("Zoom Banner"));
		zoomBanner.add(zoomLabel);
		add(zoomBanner, c3);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.weightx = .5;
		c2.weighty = .32;
		c2.gridx = 0;
		c2.gridy = 1;
		c2.gridwidth = 3;
		c2.gridheight = 3;
		cityAnimation.setLayout(new BoxLayout(cityAnimation, BoxLayout.Y_AXIS));
		cityAnimation.add(cityAniPanel);
		//cityAnimation.setBorder(BorderFactory.createTitledBorder("City Animation"));
		add(cityAnimation, c2);
		GridBagConstraints c4 = new GridBagConstraints();
		c4.fill = GridBagConstraints.BOTH;
		c4.weightx = .5;
		c4.weighty=.32;
		c4.gridx = 3;
		c4.gridy=1;
		c4.gridwidth = GridBagConstraints.REMAINDER;
		c4.gridheight = 3;
		zoomAnimation.setLayout(new BoxLayout(zoomAnimation, BoxLayout.Y_AXIS));
		zoomAnimation.add(cards);
//		ranchoAniPanel.setVisible(false);
//		zoomAnimation.add(housAniPanel);
		//zoomAnimation.setBorder(BorderFactory.createTitledBorder("Zoom Animation"));
		add(zoomAnimation, c4);
		GridBagConstraints c5 = new GridBagConstraints();
		c5.fill = GridBagConstraints.BOTH;
		c5.weightx= .5;
		c5.weighty = .18;
		c5.gridx=0;
		c5.gridy=5;
		c5.gridwidth = 3;
		c5.gridheight = 2;
		cityPanel.setBorder(BorderFactory.createTitledBorder("City Panel"));
		add(cityPanel, c5);
		GridBagConstraints c6 = new GridBagConstraints();
		c6.fill = GridBagConstraints.BOTH;
		c6.weightx= 0;
		c6.weighty = .18;
		c6.gridx=3;
		c6.gridy=5;
		c6.gridwidth = GridBagConstraints.REMAINDER;
		c6.gridheight =2;
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom Panel"));
		add(panelB, c6);			
	}
	
	public static void main(String[] args) {
		SimCityGui gui = new SimCityGui("Sim City Disneyland");
		gui.setTitle("SimCity Disneyland");
		gui.setVisible(true);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		restRancho.addPerson(null, "Cook", "cook", 50);
        restRancho.addPerson(null, "Waiter", "w", 50);
        restRancho.addPerson(null, "Cashier", "cash", 50);
        restRancho.addPerson(null, "Market", "Trader Joes", 50);
        restRancho.addPerson(null, "Host", "Host", 50);
        restRancho.addPerson(null, "Customer", "Sally", 50);
		//restPizza.addPerson(null, "Cook", "cook", 50);
        //restPizza.addPerson(null, "Waiter", "w", 50);
        //restPizza.addPerson(null, "Cashier", "cash", 50);
        //restPizza.addPerson(null, "Host", "Host", 50);
		//restPizza.addPerson(null, "Customer", "Sally", 50);
        
		restHaus.addPerson(null, "Cook", "cook", 50);
        restHaus.addPerson(null, "Waiter", "w", 50);
        restHaus.addPerson(null, "Cashier", "cash", 50);
        restHaus.addPerson(null, "Host", "Host", 50);
		restHaus.addPerson(null, "Customer", "Sally", 50);
		
		mickeysMarket.addPerson(null, "Manager", "MRAWP");
		mickeysMarket.addPerson(null, "Cashier", "Kapow");
		mickeysMarket.addPerson(null, "Worker", "Bleep");
//		mickeysMarket.addPerson(null, "Customer", "Beebop", 100, "American", 1);

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == panelB) {
			CardLayout cl = (CardLayout)(cards.getLayout());	
			if (currP == Panel.Haus) {
				cl.show(cards, "Bank");
				currP = Panel.Bank;
			}
			else if (currP == Panel.Bank) {
				System.out.println("showing hauntedmansion");
				cl.show(cards, "Mansion");
				currP = Panel.Mansion;
			} else if (currP == Panel.Mansion) {
				System.out.println("showing apt1");
				cl.show(cards, "Apt1");
				currP = Panel.Apt1;
			} else if (currP == Panel.Apt1) {
				System.out.println("showing apt2");
				cl.show(cards, "Apt2");
				currP = Panel.Apt2;
			} else if (currP == Panel.Apt2) {
				System.out.println("showing apt3");
				cl.show(cards, "Apt3");
				currP = Panel.Apt3;
			} else if (currP == Panel.Apt3) {
				System.out.println("showing apt4");
				cl.show(cards, "Apt4");
				currP = Panel.Apt4;
			} else if (currP == Panel.Apt4) {
				System.out.println("showing apt5");
				cl.show(cards, "Apt5");
				currP = Panel.Apt5;
			} else if (currP == Panel.Apt5) {
				System.out.println("showing apt6");
				cl.show(cards, "Apt6");
				currP = Panel.Apt6;
			} else if (currP == Panel.Apt6) {
				System.out.println("showing market");
				cl.show(cards, "Market");
				currP = Panel.Market;
			} else if (currP == Panel.Market) {
				System.out.println("showing rancho");
				cl.show(cards, "Rancho");
				currP = Panel.Rancho;
			} else if (currP == Panel.Rancho) {
				System.out.println("showing bayou");
				cl.show(cards, "Bayou");
				currP = Panel.Bayou;
			} else if (currP == Panel.Bayou) {
				System.out.println("showing pizza");
				cl.show(cards,  "Pizza");
				currP = Panel.Pizza;
			} else if (currP == Panel.Pizza) {
				System.out.println("showing cafe");
				cl.show(cards,  "Cafe");
				currP = Panel.Cafe;
			} else if (currP == Panel.Cafe){
				System.out.println("showing haus"); 
				cl.show(cards,  "Haus");
				currP = Panel.Haus;
			}
		}
	}
	
	public void showPanel(String panel) {
		System.out.println("showing " + panel);
		((CardLayout)(cards.getLayout())).show(cards, panel);
		currP = Panel.valueOf(panel);
	}
}
