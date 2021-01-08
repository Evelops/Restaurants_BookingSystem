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
import java.util.* ;

import booksys.presentation.ReservationDialog;

import java.awt.event.ActionEvent;
import java.awt.event.TextEvent;// ����ó�� ���ַ��� �߰� 
import java.awt.event.TextListener;// ���������� ����ó�� ���ַ��� �߰�



public class BookingSystem
{
  // Attributes:

  Date currentDate ;
  Date today ;
  
  // Associations:
  Restaurant restaurant = null ;
  Vector currentBookings ;
  Booking selectedBooking ;
  Vector phoneNumber;

  // Singleton:
  // �̱��� ������ ����� �κ�. Ŭ�������� �ڽ��� �� �ϳ����� �ν��Ͻ��� �����ϵ��� ����� ���� �ٸ� � Ŭ���������� �ڽ��� �ν��Ͻ��� �߰��� ������
  //���ϵ����ؾ���.
  private static BookingSystem uniqueInstance ;

  public static BookingSystem getInstance()
  {
    if (uniqueInstance == null) {
      uniqueInstance = new BookingSystem() ;
    }
    return uniqueInstance ;
  }

  private BookingSystem()
  {//
    today = new Date(Calendar.getInstance().getTimeInMillis()) ;
    restaurant = new Restaurant() ;
  }

  // Observer: this is `Subject/ConcreteSubject'

  Vector observers = new Vector() ; // vector���� 
//������ ������ ������ �� 
  public void addObserver(BookingObserver o)
  {
    observers.addElement(o) ;// vector�� element()�޼ҵ尡 ���� ���⼭ �߰��� ���� enumeration��ü�� ��ȯ
  }
  
  
  public void notifyObservers()
  {
    Enumeration enums = observers.elements() ;// ������ ����� vector���� element()�޼ҵ��� ��ü�� ����ҵ��� ��ȯ
    while (enums.hasMoreElements()) { // hasMoreElements()�� �ڿ� ���� �����Ͱ� ������ True, ������ False ��ȯ, while���� ���� ����ó���� ����
      BookingObserver bo = (BookingObserver) enums.nextElement() ;// �ڷᱸ���� ���������� ��ȯ�Ͽ�, ���� ��ü ���۷��� ����
      bo.update() ;
    }
  }
// ������ â�� ���� �κ� ! �̰� �Ἥ awtâ ���� �ϸ� �ɲ� ����
  public boolean observerMessage(String message, boolean confirm)
  {
    BookingObserver bo = (BookingObserver) observers.elementAt(0) ;
    return bo.message(message, confirm) ;
  }
  
  // System messages:
//����� ����� �������� �κ� 
  public void display(Date date)
  {
    currentDate = date ;
    currentBookings = restaurant.getBookings(currentDate) ;
    selectedBooking = null ;
    notifyObservers() ;
  }
  // ����ٰ� handling Ŭ���� ����� �߰�����ߵ� 
  public void makeReservation(int covers, Date date, Time time, int tno,
			      String name, int phone)
  { // ���⼭ ! handling(name, phone) �� �߰������ν� ���࿡ ���� ���ට �Է����� �߸� �Է��ϸ� ���� â �ߵ��� ������ �κ� 
    if (!doubleBooked(time, tno, null) && !handling(name, phone) && !overflow(tno, covers)) { 
      Booking b // �� ���⼭ !handling �̴ϱ� handling�� �߻����� ������ �߰� �ǰ� �� �ϴ°��� 
	    = restaurant.makeReservation(covers, date, time, tno, name, phone) ;
      currentBookings.addElement(b) ;
      notifyObservers() ;
    }
  }
 
  public void makeWalkIn(int covers, Date date, Time time, int tno)
  {
    if (!doubleBooked(time, tno, null) && !overflow(tno, covers)) {
      Booking b = restaurant.makeWalkIn(covers, date, time, tno) ;
      currentBookings.addElement(b) ;
      notifyObservers() ;
    }
  }
  
  public void selectBooking(int tno, Time time)
  {
    selectedBooking = null ;
    Enumeration enums = currentBookings.elements() ;
    while (enums.hasMoreElements()) {
      Booking b = (Booking) enums.nextElement() ;
      if (b.getTableNumber() == tno) { //tableNumber�� tno ����
	    if (b.getTime().before(time)
	        && b.getEndTime().after(time)) {
	        selectedBooking = b ;
	    }
      }
    }
    notifyObservers() ;
  }

  public void cancel()
  {
    if (selectedBooking != null) {
      if (observerMessage("Are you sure?", true)) {
	currentBookings.remove(selectedBooking) ;
	restaurant.removeBooking(selectedBooking) ;
	selectedBooking = null ;
	notifyObservers() ;
      }
    }
  }
  
  
  public void recordArrival(Time time)
  {
    if (selectedBooking != null) {
      if (selectedBooking.getArrivalTime() != null) {
	observerMessage("Arrival already recorded", false) ;
      }
      else {
	selectedBooking.setArrivalTime(time) ;
	restaurant.updateBooking(selectedBooking) ;
	notifyObservers() ;
      }
    }
  }
  // ���̺� ����Ȱ� �̵� ��ų �� ����ϴ� �޼ҵ� 
  public void transfer(Time time, int tno)
  {
    if (selectedBooking != null) {
      if (selectedBooking.getTableNumber() != tno) {
	if (!doubleBooked(selectedBooking.getTime(), tno, selectedBooking)
	    && !overflow(tno, selectedBooking.getCovers())) {
	  selectedBooking.setTable(restaurant.getTable(tno)) ;
	  restaurant.updateBooking(selectedBooking) ;
	}
      }
      notifyObservers() ;
    }
  }
  
//���Ⱑ ���� ��ĥ�� ���� �ߴ� �κ��ΰ� ����.
  private boolean doubleBooked(Time startTime, int tno, Booking ignore)
  {
    boolean doubleBooked = false ;

    Time endTime = (Time) startTime.clone() ;
    endTime.setHours(endTime.getHours() + 2) ;
    
    Enumeration enums = currentBookings.elements() ;
    while (!doubleBooked && enums.hasMoreElements()) {
      Booking b = (Booking) enums.nextElement() ;
      if (b != ignore && b.getTableNumber() == tno
	  && startTime.before(b.getEndTime())
	  && endTime.after(b.getTime())) {
	doubleBooked = true ;
	observerMessage("Double booking!", false) ;
      }
    }
    return doubleBooked ;
  }
  
  // ���⼭ getcustmoer�����ٰ� ��ߵ� 
  // �߰������� cover �κ� ���� �߰��Ϸ��� ���� cover �κ� ���� ���� �߰��ؾߵ�;
  //customer �κп��� ������ �߻��ϴ� �κ��� ����ó����� �ؾߵǳ� �� ��°�� �װ� ���ִ� �κ�  
  // ||Integer.parseInt(c.getName())/1==Integer.parseInt(c.getName())
  // name�� �������ε� ���ڵ� ¥�� ������ ���� �ٵ� ���� �ϰ� ������ ���� �Է½� ���� �߰� �Ϸ��� �ϴ°ž� �״ϱ� name�� ���ڰ� �ԷµǸ�, 1�� ������ �ڱ� �ڽ��� �ɲ� �ƴϳ� 
  //�װ� �̿��ؼ� name�� ���ڰ� �ԷµǸ� ���� �߰� �ϵ���.
  private boolean handling(String name, int phone)
  {
	  boolean handling=false;
	  Customer c=restaurant.getCustomer(name, phone);
	  if((c.getPhoneNumber()%1)!=0) { 
		// 1�� �ڱ� �ڽ����� ������ ������ �ڱ� �ڽ��� ���;� ���ݾ� �ٵ� ���� ���ڸ� �Է��ϸ� �׷��� �� �ߴϱ� �̷���  
		  handling=observerMessage("The input format was incorrect!",true);
	  }
	  return handling;
  }
  private boolean overflow(int tno, int covers)
  {
    boolean overflow = false ;
    Table t = restaurant.getTable(tno) ;
      
    if (t.getPlaces() < covers) {
      overflow = !observerMessage("Ok to overfill table?", true) ;
    }
    
    return overflow ;
  }
  
  // Other Operations:

  public Date getCurrentDate()
  {
    return currentDate ;
  }
  
  public Enumeration getBookings()
  {
    return currentBookings.elements() ;
  }

  public Booking getSelectedBooking()
  {
    return selectedBooking ;
  }

  public static Vector getTableNumbers()
  {
    return Restaurant.getTableNumbers() ;
  }
}
