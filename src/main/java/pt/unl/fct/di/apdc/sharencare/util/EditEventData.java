package pt.unl.fct.di.apdc.sharencare.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class EditEventData {

	private Gson g = new Gson();
	
	public String name;
	public String description;
	public String durability;
	public String endingDate;
	public String initialDate;
	public Double lat;
	public Double lon;
	public String maxParticipants;
	public String minParticipants;
	public List<Integer> tags;
	public String time;

	public EditEventData() {

	}

	public EditEventData(String name, String description, String durability, String endingDate, String initialDate, Double lat,
			Double lon, String maxParticipants, String minParticipants, List<Integer> tags, String time) {

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

	}

	public boolean isHourValid(String hours) {
		try {
			LocalTime.parse(hours);
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
		return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
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
		if (m == 4 || m == 6 || m == 9 || m == 11)
			return (d <= 30);

		return true;
	}

	public boolean validParticipants() {
		return Integer.parseInt(minParticipants) > 0
				&& Integer.parseInt(maxParticipants) >= Integer.parseInt(minParticipants);
	}

	public boolean verifyDate(String date) {
		if (!date.contains("/")) {
			return false;
		} else {
			String[] parts1 = date.split("/");

			return isValidDate(Integer.parseInt(parts1[0]), Integer.parseInt(parts1[1]), Integer.parseInt(parts1[2]));
		}

	}

	public boolean futureDate(String startDate) throws ParseException {

		String currDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		Date today = new SimpleDateFormat("dd/MM/yyyy").parse(currDate);
		Date start = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);

		if (today.compareTo(start) > 0) { // initial date is before current date
			return false;
		}
		if (today.compareTo(start) == 0) { // inital date is the same day as today
			return false;

		}

		return true;

	}

	public boolean dateOrder(String startDate, String endDate) throws ParseException {



		Date start = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
		Date end = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);

		if (start.compareTo(end) > 0) {// start is after end
			return false;
		}

		return true;

	}
}
