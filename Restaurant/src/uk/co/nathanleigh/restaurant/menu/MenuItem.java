package uk.co.nathanleigh.restaurant.menu;

// creates a menu item, each menu item has a name, type and a
// image icon
public class MenuItem {
	private String sectionName;
	private String menuItemName;
	private int sectionId;
	private int menuItemId;
	
	
	public MenuItem( int sectionId, String sectionName,
			int menuItemId, String menuItemName) {
		super();
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.menuItemId = menuItemId;
		this.menuItemName = menuItemName;

	}

	public int getMenuItemId() {
		return menuItemId;

	}
	
	public String getMenuItemName() {
		return menuItemName;
		
	}
	
	public int getSectionId() {
		return sectionId;
	}
		
	public String getSectionName() {
		return sectionName;

	}

}
