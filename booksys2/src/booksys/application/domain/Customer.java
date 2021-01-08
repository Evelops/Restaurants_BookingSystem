/*
 * Restaurant Booking System: example code to accompany
 *
 * "Practical Object-oriented Design with UML"
 * Mark Priestley
 * McGraw-Hill (2004)
 */

package booksys.application.domain ;

//예약할때 입력해야 되는 고객정보 중 고객 이름이랑 phone number 
public class Customer
{
  private String name ;
  private int phoneNumber ;

  public Customer(String n, int p)
  {
    name = n ;
    phoneNumber = p ;
  }

  public String getName()
  {
    return name ;
  }

  public int getPhoneNumber()
  {
    return phoneNumber ;
  }
}
