package uk.co.nathanleigh.restaurant.waiter;

public class OrderDetails{
	
	// class for order history displaying order details in order completion class
	
	private String itemName;
	private String  optionName;
	private int  optionPrice;
	private int orderGroupId;
	private int totalPrice;
	private int orderId;
	private int addonTotalPrice;
	
	public OrderDetails(int orderId, String itemName, 
			String optionName, int optionPrice,
			int addonTotalPrice){
		
		this.orderId = orderId;
		this.itemName = itemName;
		this.optionName = optionName;
		this.optionPrice = optionPrice;
		this.addonTotalPrice = addonTotalPrice;
		
	}
	public OrderDetails(int orderGroupId, 
			 int totalPrice){
		
		this.orderGroupId = orderGroupId;
		this.totalPrice = totalPrice;
		
		
	}
	
	public String getItemName(){
		return itemName;
	}
	public String getOptionName(){
		return optionName;
	}
	public int getOptionPrice(){
		return optionPrice;
	}
	public int getOrderGroupId(){
		return orderGroupId;
	}
	public int getOrderId(){
		return orderId;
	}
	public int getTotalPrice(){
		return totalPrice;
	}
	public int getAddonTotalPrice(){
		return addonTotalPrice;
	}
	
	
	
	
}