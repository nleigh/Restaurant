package uk.co.nathanleigh.restaurant;

public class Nutrition {
// nutrition for calories, sugars, fat, saturates
	// and salts
	public Nutrition(){
		
	}

	public static double getCalorieGuidelineDailyAmount(){
		return 2000;
	}
	
	public static double getSugarsGuidelineDailyAmount(){
		return 90;
	}
	
	public static double getFatGuidelineDailyAmount(){
		return 70;
	}
	
	public static double getSaltGuidelineDailyAmount(){
		return 6;
	}
	
	public static double getSaturatesGuidelineDailyAmount(){
		return 20;
	}

	/*
	 
	 Total fat
	High: more than 17.5g of fat per 100g
	Low: 3g of fat or less per 100g
	
	Saturated fat
	High: more than 5g of saturated fat per 100g 
	Low: 1.5g of saturated fat or less per 100g 
	
	Sugars
	High: more than 22.5g of total sugars per 100g 
	Low: 5g of total sugars or less per 100g
	
	Salt
	High: more than 1.5g of salt per 100g (or 0.6g sodium) 
	Low: 0.3g of salt or less per 100g (or 0.1g sodium)
	 
	 */
	
	public static int calculateSaturatesStatus(double saturatesPercentage) {
		if ( saturatesPercentage <= 1.5){
			return 1;
		}
		if ( saturatesPercentage > 5){
			return 3;
		}
		return 2;
	}
	
	public static int calculateSaltStatus(double saltPercentage) {
		
		if ( saltPercentage <= 0.3){
			return 1;
		}
		if ( saltPercentage > 1.5){
			return 3;
		}
		return 2;
	}
	
	public static int calculateFatStatus(double fatPercentage) {
		if ( fatPercentage <= 3){
			return 1;
		}
		if ( fatPercentage > 17.5){
			return 3;
		}
		return 2;
	}
	
	public static int calculateSugarStatus(double sugarsPercentage) {
		if ( sugarsPercentage <= 5){
			return 1;
		}
		if ( sugarsPercentage > 22.5){
			return 3;
		}
		return 2;
	}
}
