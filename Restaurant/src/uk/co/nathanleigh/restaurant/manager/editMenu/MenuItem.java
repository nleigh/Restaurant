package uk.co.nathanleigh.restaurant.manager.editMenu;

public class MenuItem {

	int sectionId;
	String menuItemName, menuItemDesc;
	
	public MenuItem(int sectionId, String menuItemName, String menuItemDesc){
	sectionId = this.sectionId;
	menuItemName = this.menuItemName;
	menuItemDesc = this.menuItemDesc;
	
}
	public int getSectionId(){
		return sectionId;
	}
	public void setSectionId(int sId){
		sectionId = sId;
	}
	public String getMenuItemName(){
		return menuItemName;
	}
	public String getMenuItemDesc(){
		return menuItemDesc;
	}
}
