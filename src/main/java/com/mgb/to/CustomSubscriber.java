package com.mgb.to;

import com.utiljar.bo.Subscriber;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class CustomSubscriber
  extends Subscriber
{
  @DateTimeFormat(pattern="dd/MM/yyyy")
  private Date subscribedDate;
  
  public Date getSubscribedDate()
  {
    return this.subscribedDate;
  }
  
  public void setSubscribedDate(Date subscribedDate)
  {
    this.subscribedDate = subscribedDate;
  }
}
