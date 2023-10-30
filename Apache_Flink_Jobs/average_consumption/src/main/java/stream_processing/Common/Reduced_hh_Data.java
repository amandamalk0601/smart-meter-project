package stream_processing.Common;

/*this class is used to map raw data from the source (CSV lines) into a structured format,
 where each object of this class holds properties of a household's energy data
 this class provides getters, setters, and toString functions
 */

public class Reduced_hh_Data {
    public String date;
    public long timestamp, energy, power, numberOfPeople;
    public double squareMeters;
    public String userId, homeType;
    public long avg_power;

    public long getAvg_power() {
        return avg_power;
    }

    public void setAvg_power(long avg_power) {
        this.avg_power = avg_power;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getEnergy() {
        return energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    public long getPower() {
        return power;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public long getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(long numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public double getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(double squareMeters) {
        this.squareMeters = squareMeters;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHomeType() {
        return homeType;
    }

    public void setHomeType(String homeType) {
        this.homeType = homeType;
    }

    @Override
    public String toString() {
        return "Reduced_hh_Data [date=" + date + ", timestamp=" + timestamp + ", avg_power=" + avg_power + ", energy="
                + energy + ", power=" + power
                + ", numberOfPeople=" + numberOfPeople + ", squareMeters=" + squareMeters + ", userId=" + userId
                + ", homeType=" + homeType + "]";
    }

    public String avg_toCSV_String() {
        return date + "," + userId + "," + timestamp + "," + avg_power;
    }

    public String peak_toCSV_String() {
        return date + "," + userId + "," + timestamp + "," + power;
    }
}
