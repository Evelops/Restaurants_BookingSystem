/*
 * Restaurant Booking System: example code to accompany
 *
 * "Practical Object-oriented Design with UML"
 * Mark Priestley
 * McGraw-Hill (2004)
 */

package booksys.application.domain ;

import java.sql.Date ;
import java.sql.Time ;

public class Reservation extends BookingImp
{
  private Customer customer ;
  private Time     arrivalTime ;
  
  public Reservation(int c, Date d, Time t, Table tab, Customer cust, Time arr)
  {
    super(c, d, t, tab) ;
    customer    = cust ;
    arrivalTime = arr ;
  }

  public java.sql.Time getArrivalTime() {
    return arrivalTime ;
  }

  public Customer getCustomer() {
    return customer ;
  }

  public String getDetails()
  {
    StringBuffer details = new StringBuffer(64) ; // StringBuffer 클래스 사용시 new 이용하여 새로운 객체 생성
    details.append(customer.getName()) ;// append 메소드는 기존 문자열의 뒤쪽에 문자열을 추가하는 기능을 가지고 있음
    details.append(" ") ;
    details.append(customer.getPhoneNumber()) ;
    details.append(" (") ;
    details.append(covers) ;
    details.append(")") ;
    if (arrivalTime != null) {
      details.append(" [") ;
      details.append(arrivalTime) ;
      details.append("]") ;
    }
    return details.toString() ;
  }

  public void setArrivalTime(Time t) {
    arrivalTime = t ;
  }

  public void setCustomer(Customer c) {
    customer = c ;
  }
}
