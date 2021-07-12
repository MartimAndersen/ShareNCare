package pt.unl.fct.di.apdc.sharencare.util;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Value;


public class EventData {

    public String durability;
    public String endingDate;
    public String initialDate;
    public String institutionName;
    public Double lat, lon;
    public List<String> members;
    public String minParticipants, maxParticipants;
    public String name, description;
    public int points;
    public List<Integer> tags;
    public String time;

    public EventData() {

    }

    public EventData(String durability, String endingDate, String initialDate, String institutionName,  Double lat, Double lon, List<String> members, String minParticipants, String maxParticipants, String name, String description, int points, List<Integer> tags, String time){   	
        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.durability = durability;
        this.initialDate = initialDate;
        this.endingDate = endingDate;
        this.tags = tags;
        this.institutionName = institutionName;
        this.members = members;
        this.points = points;
    }

    public boolean atLeastOneEmptyParameter() {
        return name.equals("") || description.equals("") || minParticipants.equals("")
                || maxParticipants.equals("") || time.equals("") || durability.equals("") || initialDate.equals("") ||
                endingDate.equals("") ||lat == null || lon == null || tags.size() == 0 || institutionName.equals("");
    }
    
    public boolean validParticipants() {
		return Integer.parseInt(minParticipants) > 0
				&& Integer.parseInt(maxParticipants) >= Integer.parseInt(minParticipants);
    }
    
    public boolean isHourValid() {
    	try {
            LocalTime.parse(time);
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    	return true;
    }

    private boolean isLeap(int year) {
        // Return true if year
        // is a multiple pf 4 and
        // not multiple of 100.
        // OR year is multiple of 400.
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0));
    }

    private boolean isValidDate(int d, int m, int y) {

        if (m < 1 || m > 12)
            return false;
        if (d < 1 || d > 31)
            return false;

        // Handle February month
        // with leap year
        if (m == 2) {
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

    public boolean verifyDate() {
        if (!initialDate.contains("/") && !endingDate.contains("/")) {
            return false;
        } else {
            String[] parts1 = initialDate.split("/");
            String[] parts2 = endingDate.split("/");
            return isValidDate(Integer.parseInt(parts1[0]), Integer.parseInt(parts1[1]), Integer.parseInt(parts1[2]))
            		&& isValidDate(Integer.parseInt(parts2[0]), Integer.parseInt(parts2[1]), Integer.parseInt(parts2[2]));
        }
        
    }
}
