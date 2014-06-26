package uk.co.nathanleigh.restaurant.manager.editMenu;

public class Ingredient {

	int ingId;
	String ingName, ingUnit;
	
	public Ingredient(int ingId, String ingName, String ingUnit){
		ingId = this.ingId;
		ingName = this.ingName;
		ingUnit = this.ingUnit;
	}
	
	public String getIngName(){
		return ingName;
	}
	public int getIngId(){
		return ingId;
	}
	public String getIngUnit(){
		return ingUnit;
	}
}
