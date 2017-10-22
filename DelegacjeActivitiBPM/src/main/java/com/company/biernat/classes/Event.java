package com.company.biernat.classes;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

  private String step;
  private String user;
  private Date date;
  
  public Event(String step, String user, Date date) {
    super();
    this.step = step;
    this.user = user;
    this.date = date;
  }
  
  public String getStep() {
    return step;
  }
  
  public void setStep(String step) {
    this.step = step;
  }
  
  public String getUser() {
    return user;
  }
  
  public void setUser(String user) {
    this.user = user;
  }
  
  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  @Override
  public String toString() {
    return "Event [step=" + step + ", user=" + user + ", date=" + date + "]";
  }

}
