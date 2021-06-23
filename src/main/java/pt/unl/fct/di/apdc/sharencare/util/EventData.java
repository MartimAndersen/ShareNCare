package pt.unl.fct.di.apdc.sharencare.util;



public class EventData {
	
	public String name, description;
	public String minParticipants, maxParticipants;
    //public String coordinates;
    public String temporary;
    public String date;
    public String tag;
    public String lat, lon;
    
    
    public EventData() {
    	
    }
    
    public EventData(String name, String description,String minParticipants,String maxParticipants,
    		String lat, String lon, String temporary, String date, String tag) {
    
    	this.name = name;
    	this.description=description;
    	this.minParticipants = minParticipants;
    	this.maxParticipants=minParticipants;
        this.lat= lat;
        this.lon = lon;
        this.temporary=temporary;
        this.date = date;
        this.tag = tag;
    	
    }
	
	public boolean VerifyDate() {
		String[] parts = date.split("-");
		return isValidDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
	}
	
	private boolean isValidDate(int d, int m, int y)
	{

	    if (m < 1 || m > 12)
	    return false;
	    if (d < 1 || d > 31)
	    return false;
	 
	    // Handle February month
	    // with leap year
	    if (m == 2)
	    {
	        if (isLeap(y))
	        return (d <= 29);
	        else
	        return (d <= 28);
	    }
	 
	    // Months of April, June,
	    // Sept and Nov must have
	    // number of days less than
	    // or equal to 30.
	    if (m == 4 || m == 6 ||
	        m == 9 || m == 11)
	        return (d <= 30);
	 
	    return true;
	}

	private boolean isLeap(int year)
	{
	// Return true if year
	// is a multiple pf 4 and
	// not multiple of 100.
	// OR year is multiple of 400.
	return (((year % 4 == 0) &&
	         (year % 100 != 0)) ||
	         (year % 400 == 0));
	}
}
