package restaurant_cafe;

import agent_cafe.Agent;
import restaurant_cafe.gui.CookGui;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.Order;
import restaurant_cafe.gui.RestaurantCafe;
import restaurant_cafe.interfaces.Cook;
import restaurant_cafe.interfaces.Customer;
import market.Market;
import simcity.interfaces.Market_Douglass;
import restaurant_cafe.interfaces.Waiter;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.interfaces.Person;

import java.util.*;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the CookAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CookAgent extends Agent implements Cook {
	static final int NTABLES = 3;//a global for the number of tables.
	
	Person person;
	public enum OrderState{pending, cooking, reorder, done};
	public Collection<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public Collection<Market> markets = Collections.synchronizedList(new ArrayList<Market>());
	public Collection<Table> tables;
	public Collection<Food> foods;
	List<MarketOrder> marketOrders = new ArrayList<MarketOrder> ();
	private CookGui cookGui;
	Market_Douglass market;
	boolean shiftDone = false;
	int curID;
	public boolean inMarket;
	public boolean isWorking = true;
	double wage;
	
	class MarketOrder {
		String food;
		int amount;
		MktOrderState state;
		int id;
		MarketOrder(String f, int a) {
			amount = a;
			food = f;
			id = curID;
			curID++;
			state = MktOrderState.pending;
		}
	}
	
	private enum MktOrderState {pending, ordered};

	
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	Timer timer = new Timer();
	Timer standTimer = new Timer();
	RestaurantCafe restaurant;
	
	public CookAgent(String name, RestaurantCafe rest, Collection<Food> fds) {
		super();

		this.name = name;
		restaurant = rest;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		synchronized(tables){
		  for (int ix = 1; ix <= NTABLES; ix++) {
			  tables.add(new Table(ix));//how you add to a collections
		  }
		}
		foods = fds;
		inMarket = false;
		curID = 0;
	}

	public String getMaitreDName() {
		return name;
	}

	public void setPerson(Person p) {
		person = p;
	}
	public String getName() {
		return name;
	}

	public Collection<Table> getTables() {
		return tables;
	}
	
	public void setAmount(String food, int am) {
		
	}
	
	// Messages
	public void msgShiftDone(double w) {
		shiftDone = true;
		isWorking = false;
		wage = w;
		cookGui.DoLeave(person, wage);
	}
	
	//msg from mkt
	public void msgHereIsOrder(String choice, int amount, int id) {
		print("Received a delivery of "+amount+" "+choice+"'s from the market!");
		for (int i=0; i<marketOrders.size(); i++){
			MarketOrder mo = marketOrders.get(i);
			if (mo.id == id && mo.amount == amount) {
				Food f = null;
				for(Food food : foods){
					if(mo.food == food.getName()){
						f = food;
					}
				}
				f.setAmount(amount);
				f.setOrderAttempts(0);
				print("removing a market order whee");
				marketOrders.remove(mo);
			} 
			else if (mo.food == choice && mo.amount != 0) {
				Food f = null;
				for(Food food : foods){
					if(mo.food == food.getName()){
						f = food;
					}
				}
				f.setAmount(amount + mo.amount);
				mo.amount -= amount;
			}
		}
	}
	//msg from waiter
	public void msgHereIsOrder(Waiter w, String choice, Integer table){
	    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "cook received msgHereIsOrder");
		print("table "+table+" ordered "+choice);
		orders.add(new Order(w, choice, (int)table));
	    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "orders size is "+orders.size());
	    for(Order o : orders){
		    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "order state: "+o.s);
	    }
	    	stateChanged();
	}
	public void msgAddOrder(Order o){
		orders.add(o);
		stateChanged();
	}
	public void msgFulfilledOrder(Food food, int amount){
		print("GOT " + amount + " MORE "+food.getName()+ "s");
		food.setAmount(food.getAmount()+amount);
		food.setOrderAttempts(0);
	}
	public void msgOutOfFood(Food f, int ex){
	    synchronized(orders){
		  for(Order order : orders){
			  if(order.food == f){
				  if(order.s != OrderState.done){
					  order.s = OrderState.reorder;
					  order.exclude = ex;
				  }
			  }
		  }
	    }
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
	    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "sched orders size is "+orders.size());

	    synchronized(orders){
		  for(Order order : orders){
			    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "order state is " +order.s);
			  if(order.s == OrderState.done){
				  plateIt(order);
				  return true;
			  }
		  }
		}
		
		synchronized(orders){
		  for(Order order : orders){
			  if(order.s == OrderState.pending){
				  AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "cook scheduler cookIt");
				  cookIt(order);
				  return true;
			  }
		  }
		}
		/*synchronized(orders){
		  for(Order order : orders){
			  if(order.s == OrderState.reorder && order.food.getOrderAttempts()<=markets.size()){
				  makeOrder(order);
				  return true;
			  }
		  }
		}*/
		synchronized(marketOrders) {
			for (MarketOrder mo : marketOrders){ 
				if (mo.state == MktOrderState.pending) {
					print("Ordering "+mo.amount+" "+mo.food+"'s");
					createPersonAs(mo);
					return true;
				}
			}
		}

		Order newOrder = restaurant.orderStand.remove();
		if (newOrder!=null) {
			AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "New stand order");
			orders.add(newOrder);
			print("order stand not empty, got order for "+ newOrder.food.getName()); 
			return true;
		}
		else {
			waitTimer();
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void waitTimer() {
		AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "WaitTimer being called");
		standTimer.schedule(new TimerTask() {
			public void run() {
				stateChanged();
			}
		}, 5000);
	}
	
	private void cookIt(final Order o){
		print("COOK " + o.food.getName() + " " + o.food.getAmount());
		  AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "COOK COOKING");
		cookGui.DoGrilling(o.food.getName());
		if(o.food.getAmount() == 0){
			print("NO MORE "+ o.food.getName() + " TO COOK");
			o.waiter.msgOutOfFood(o.food);
			marketOrders.add(new MarketOrder(o.food.getName(), o.food.getCapacity()));
			orders.remove(o);
			//o.s = OrderState.reorder;
			return;
		}
		o.food.decreaseAmount();
		if(o.food.getAmount() <= o.food.getLowAmt()){
			o.exclude = -1;
			marketOrders.add(new MarketOrder(o.food.getName(), o.food.getCapacity()-o.food.getAmount()));
			o.food.setOrderAttempts(o.food.getOrderAttempts()+1);
			//makeOrder(o);
		}
		  AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "COOK TIME: "+o.food.getCookingTime());
		  o.s = OrderState.done;

		timer.schedule(new TimerTask() {
			public void run() {
				cookGui.DoneGrilling();
				stateChanged();
			}
		}, o.food.getCookingTime());
	}
	/*
	private void makeOrder(Order o){
		o.food.setOrderAttempts(o.food.getOrderAttempts()+1);
		int num = (int) (Math.random() * markets.size());
		while(o.exclude == num){
			num = (int) (Math.random() * markets.size());
		}
		print("INCREASE "+ o.food.getName() + " AMT "+num);
		
		//Market market = null;
		//synchronized(markets){
			//int count = 0;
			//for(Market m : markets){
				//if(count == num){
					//market = m; break;
				//}
				//count++;
			//}
		//}
		int orderAmt = o.food.getCapacity()-o.food.getAmount();
		market.msgHereIsOrder(this, o.food, orderAmt);
		if(market.getFoodAmount(o.food) < orderAmt && o.food.getOrderAttempts() == 1){
			o.exclude = num;
			makeOrder(o);
		}
	}
	*/
	private void createPersonAs(MarketOrder mo){
		mo.state = MktOrderState.ordered;
		market.personAs(restaurant, "Mexican", mo.amount, mo.id);
	}
	
	private void plateIt(Order o){
		o.waiter.msgOrderDone(o.food.getName(), o.table);
		cookGui.DoPlating(o.food.getName());
		orders.remove(o);
	}
	
	public void addMarket(Market m){
		markets.add(m);
	}
	
	public void setGui(CookGui cg){
		cookGui = cg;
	}
	
	public CookGui getGui(){
		return cookGui;
	}
	
    public int getQuantity(String name){
    	for(Food food : foods){
    		if(food.getName().equals(name)){
    			return food.getAmount();
    		}
    	}
    	return 0;
    }
    
	 public void setQuantity(String name, int num){
		 for(Food food : foods){
	    		if(food.getName().equals(name)){
	    			 food.setAmount(num);
	    		}
	    	}
	 }
	 
	public void setMarket(Market_Douglass mkt){
		market = mkt;
	}
	
	public Collection<Market> getMarkets(){
		return markets;
	}
	
	public Collection<Food> getFoods(){
		return foods;
	}
	
	public static class Table {
		Customer occupiedBy;
		int tableNumber;
		
		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}

