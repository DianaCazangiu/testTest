package com.gemma.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.gemma.utils.PropertyFileReader.getPropertyFile;
import static com.gemma.utils.encryptDecrypt.*;


public class sqlConnection {
  public static LocalDateTime date = LocalDateTime.now();

  public static DateTimeFormatter currentDate = DateTimeFormatter.ofPattern("dd-MMM-yy");

  public static String formattedDate = date.format(currentDate).toUpperCase();

  public static int finalCount;
  public static String value;

  public static String noteTextValue;

  public static String callID;

  public static Connection con;

  public static ResultSet rs;

  public static ResultSet resultSet;

  static Properties propertyReader;
  private static Logger logger = LoggerFactory.getLogger(sqlConnection.class);

  static {
    try {
      propertyReader = getPropertyFile("config.properties");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public static Connection connectToOracleDatabase() {
    String url
        = "jdbc:oracle:thin:@10.50.174.15:1521/gem2.citrix.local"; // table details
//    String username = "GEMMA_OWN"; // MySQL credentials
//    String password = "GEMMA_OWN2014";

    String username = (String) getDecryptOracleUsername(decryptedData3); // MySQL credentials
    String password = (String) getDecryptOraclePassword(decryptedData4);

    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      con = DriverManager.getConnection(
          url, username, password);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    logger.info("Connection Established successfully");
    return con;
  }


  public static String getValueFromQueryResultSet() {
    String query
        = "select * from GEMMA_PUBLIC.vw_wi_note where user_id = 8088 and DATE_CREATED like '%" + formattedDate + "%' and rownum = 1"; // query to be run
    try {
      Statement st = con.createStatement();
      rs = st.executeQuery(query);
      logger.info(query);
      while(rs.next()) {
        value = (rs.getString("ID_WI"));
        logger.info(value);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return value;
  }

  public static ResultSet runFinalQuery()  {
    // query to be run
    String query1
        = "select count(*) from GEMMA_PUBLIC.vw_wi_note where user_id = 8088 and DATE_CREATED like '%" + formattedDate + "%' and ID_WI = " + getValueFromQueryResultSet(); // query to be run
    try {
      Statement st = con.createStatement();
      resultSet = st.executeQuery(query1);
      logger.info(query1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }



  public static int getFinalCount()  {
    waitHelper.hardWait(5000);
    ResultSet resultSet = runFinalQuery();
    try {
      while(resultSet.next()){
        finalCount = (resultSet.getInt(1));
        logger.info(String.valueOf(finalCount));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return finalCount;
  }

  public static String getCallIDValue() {
    waitHelper.hardWait(3000);
    String query2
        = "select rnum, NOTE_TEXT from (SELECT rownum rnum, NOTE_TEXT FROM GEMMA_PUBLIC.vw_wi_note where user_id = 8088 and DATE_CREATED like '%" + formattedDate + "%' and ID_WI = " + getValueFromQueryResultSet() + ") where rnum = 5"; // query to be run
//    String query2 = "select NOTE_TEXT from GEMMA_PUBLIC.vw_wi_note where user_id = 8088 and DATE_CREATED like '%" + formattedDate + "%' and ID_WI = "+ getValueFromQueryResultSet();
    try {
      Statement st = con.createStatement();
      rs = st.executeQuery(query2);
      logger.info(query2);
      while(rs.next()) {
        noteTextValue = (rs.getString("NOTE_TEXT"));
        logger.info(noteTextValue);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return noteTextValue;
  }

  public static String callID() {
    Pattern pat = Pattern.compile("C+[0-9]+-+[0-9]+");

    Matcher mat = pat.matcher(getCallIDValue());

    while (mat.find()) {
       callID = mat.group();
    }
    System.out.println("Match: " + callID);

    return callID;
  }


}
