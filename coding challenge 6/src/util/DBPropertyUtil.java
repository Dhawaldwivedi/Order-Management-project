package util;

public class DBPropertyUtil {

    // Static method to return the connection string
    public static String getDBConnectionString(String propertyFileName) {
        // Here, you can ignore the propertyFileName parameter and return the hardcoded connection string
        return "jdbc:mysql://localhost:3306/CodingChallenge";
    }
}

