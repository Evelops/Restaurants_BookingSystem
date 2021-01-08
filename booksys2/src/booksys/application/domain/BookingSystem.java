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
import java.awt.event.TextEvent;// 예외처리 해주려고 추가 
import java.awt.event.TextListener;// 마찬가지로 예외처리 해주려고 추가



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
  // 싱글턴 패턴을 사용한 부분. 클래스에서 자신의 단 하나뿐인 인스턴스를 관리하도록 만드는 역할 다른 어떤 클래스에서도 자신의 인스턴스를 추가로 만들지
  //못하도록해야함.
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

  Vector observers = new Vector() ; // vector선언 
//옵저버 패턴을 적용한 곳 
  public void addObserver(BookingObserver o)
  {
    observers.addElement(o) ;// vector에 element()메소드가 존재 여기서 추가된 것을 enumeration객체로 반환
  }
  
  
  public void notifyObservers()
  {
    Enumeration enums = observers.elements() ;// 위에서 선언된 vector에서 element()메소드의 객체의 모든요소들을 반환
    while (enums.hasMoreElements()) { // hasMoreElements()는 뒤에 남은 데이터가 있으면 True, 없으면 False 반환, while루프 통한 제어처리시 사용됨
      BookingObserver bo = (BookingObserver) enums.nextElement() ;// 자료구조의 다음데이터 반환하여, 다음 객체 레퍼런스 얻음
      bo.update() ;
    }
  }
// 실제로 창을 띠우는 부분 ! 이거 써서 awt창 띄우게 하면 될꺼 같다
  public boolean observerMessage(String message, boolean confirm)
  {
    BookingObserver bo = (BookingObserver) observers.elementAt(0) ;
    return bo.message(message, confirm) ;
  }
  
  // System messages:
//여기는 예약시 보여지는 부분 
  public void display(Date date)
  {
    currentDate = date ;
    currentBookings = restaurant.getBookings(currentDate) ;
    selectedBooking = null ;
    notifyObservers() ;
  }
  // 여기다가 handling 클래스 만든거 추가해줘야됨 
  public void makeReservation(int covers, Date date, Time time, int tno,
			      String name, int phone)
  { // 여기서 ! handling(name, phone) 문 추가함으로써 만약에 내가 예약때 입력형식 잘못 입력하면 오류 창 뜨도록 나오는 부분 
    if (!doubleBooked(time, tno, null) && !handling(name, phone) && !overflow(tno, covers)) { 
      Booking b // 즉 여기서 !handling 이니까 handling이 발생하지 않으면 추가 되게 끔 하는거임 
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
      if (b.getTableNumber() == tno) { //tableNumber랑 tno 같고
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
  // 테이블 예약된거 이동 시킬 때 사용하는 메소드 
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
  
//여기가 예약 겹칠때 오류 뜨는 부분인거 같다.
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
  
  // 여기서 getcustmoer가져다가 써야됨 
  // 추가적으로 cover 부분 오류 추가하려면 따로 cover 부분 내용 만들어서 추가해야됨;
  //customer 부분에서 오류가 발생하는 부분을 예외처리라고 해야되나 뭐 어째든 그거 해주는 부분  
  // ||Integer.parseInt(c.getName())/1==Integer.parseInt(c.getName())
  // name은 문자형인데 숫자도 짜피 읽히긴 읽혀 근데 내가 하고 싶은건 숫자 입력시 오류 뜨게 하려고 하는거야 그니까 name에 숫자가 입력되면, 1로 나누면 자기 자신이 될꺼 아니냐 
  //그걸 이용해서 name에 숫자가 입력되면 오류 뜨게 하도록.
  private boolean handling(String name, int phone)
  {
	  boolean handling=false;
	  Customer c=restaurant.getCustomer(name, phone);
	  if((c.getPhoneNumber()%1)!=0) { 
		// 1을 자기 자신으로 나누면 무조건 자기 자신이 나와야 되잖아 근데 만약 문자를 입력하면 그렇게 안 뜨니까 이렇게  
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
