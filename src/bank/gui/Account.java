package bank.gui;

public class Account {
	public int number;
	public double balance;
	public double loanAmount;
	public int loanTime;
	
	public Account(int num){
		number = num;
		balance = 0.00;
		loanAmount = 0.00;
		loanTime = 0;
	}
	public void setNumber(int num){
		number = num;
	}
	public int getNumber(){
		return number;
	}
	public void setBalance(double bal){
		balance = bal;
	}
	public double getBalance(){
		return balance;
	}
	public void setLoanAmount(double loanAmt){
		loanAmount = loanAmt;
	}
	public double getLoanAmount(){
		return loanAmount;
	}
	public void setLoanTime(int lt){
		loanTime = lt;
	}
	public double getLoanTime(){
		return loanTime;
	}
}
