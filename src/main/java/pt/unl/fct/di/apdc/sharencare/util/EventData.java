package pt.unl.fct.di.apdc.sharencare.util;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.google.cloud.datastore.Value;


public class EventData {

    public String name, description;
    public String minParticipants, maxParticipants;
    //public String coordinates;
    public String hour;
    public String temporary;
    public String initial_date;
    public String ending_date;
    public List<Integer> tags;
    public Double lat, lon;


    public EventData() {

    }

    public EventData(String name, String description, String minParticipants, String maxParticipants, String hour,
                     Double lat, Double lon, String temporary, String initial_date, String ending_date, List<Integer> tags) {

        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.hour = hour;
        this.lat = lat;
        this.lon = lon;
        this.temporary = temporary;
        this.initial_date = initial_date;
        this.ending_date = ending_date;
        this.tags = tags;

    }

    public boolean atLeastOneEmptyParameter() {
        return name.equals("") || description.equals("") || minParticipants.equals("")
                || maxParticipants.equals("") || hour.equals("") || temporary.equals("") || initial_date.equals("") ||
                ending_date.equals("") ||lat == null || lon == null || tags.size() == 0;
    }
    
    public boolean isHourValid() {
    	try {
            LocalTime.parse(hour);
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    	return true;
    }

    public boolean verifyDate() {
        if (!initial_date.contains("-") && !ending_date.contains("-")) {
            return false;
        } else {
            String[] parts1 = initial_date.split("-");
            String[] parts2 = ending_date.split("-");
            return isValidDate(Integer.parseInt(parts1[0]), Integer.parseInt(parts1[1]), Integer.parseInt(parts1[2]))
            		&& isValidDate(Integer.parseInt(parts2[0]), Integer.parseInt(parts2[1]), Integer.parseInt(parts2[2]));
        }
        
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

    private boolean isLeap(int year) {
        // Return true if year
        // is a multiple pf 4 and
        // not multiple of 100.
        // OR year is multiple of 400.
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0));
    }
}
