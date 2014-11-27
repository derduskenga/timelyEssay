package models.orders;

import java.util.Map;
import java.util.HashMap;


public class TempStore{
  //fields
  private static Map<Map<Long,String>,Boolean> documentDeadlines;
  private static Map<Long,String> documentSubjects;
  private static Map<Map<Long,String>,String> numberOfUnits;
 
  public TempStore(){}
  //setters
  public static void setDocumentDeadlines(Map<Map<Long,String>,Boolean> docDeadlines){
      documentDeadlines =  docDeadlines;
  }
  
   public static void setDocumentSubjects(Map<Long,String> docSubjects){
      documentSubjects =  docSubjects;
  }
  //map structure
  //map(map(select value, select string),select label).
  public static void setnumberOfUnitsMap(Map<Map<Long,String>,String> numOfUnits){
    numberOfUnits = numOfUnits;
  }
  
  //getters
  public static Map<Map<Long,String>,Boolean> getDocumentDeadlines(){
    return documentDeadlines;
  }
  
   public static Map<Long,String> getDocumentSubjects(){
    return documentSubjects;
  }
  
  public static Map<Map<Long,String>,String> getNumberOfUnits(){
    return numberOfUnits;
  }
}