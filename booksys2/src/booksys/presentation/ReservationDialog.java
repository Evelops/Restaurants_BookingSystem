/*
 * Restaurant Booking System: example code to accompany

 *
 * "Practical Object-oriented Design with UML"
 * Mark Priestley
 * McGraw-Hill (2004)
 */
// 여기서 수정해보쟈 
package booksys.presentation ;

import booksys.application.domain.Reservation ;

import java.awt.* ;
import java.awt.event.* ;

public class ReservationDialog extends BookingDialog
{
  protected TextField name ;
  protected static TextField phone ;
  protected Label     nameLabel ;
  protected Label     phoneLabel ;
  
  ReservationDialog(Frame owner, String title)
  {
    this(owner, title, null) ;
  }

  // This constructor initializes fields with data from an existing booking.
  // This is useful for completing Exercise 7.6.
  
  ReservationDialog(Frame owner, String title, Reservation r)
  {
    super(owner, title, r) ;

    nameLabel = new Label("Name:", Label.RIGHT) ;
    name = new TextField(30) ;// textField를 통해서 값 입력 
    if (r != null) {
      name.setText(r.getCustomer().getName()) ;
    }

    phoneLabel = new Label("Phone no:", Label.RIGHT) ;
    phone = new TextField(15) ;
    if (r != null) {
      phone.setText(Integer.toString(r.getCustomer().getPhoneNumber())) ; // 수정부분 
    }
  
    // Lay out components in dialog  생성된 component들을  레이아웃에 포함 
    
    setLayout( new GridLayout(0, 2) ) ;

    add(timeLabel) ;
    add(time) ;

    add(nameLabel) ;
    add(name) ;

    add(phoneLabel) ;
    add(phone) ;
    
    add(coversLabel) ;
    add(covers) ;

    add(tableNumberLabel) ;
    add(tableNumber) ;
    
    add(ok) ;
    add(cancel) ;
    
    pack() ;
  }
// 여기가 textfield 값 받아서 반환하는 부분 이거든 ??? 이걸 좀 건드리면 될꺼 같음 
 public String getCustomerName()
  {
    return name.getText() ;
  }

 public static int getPhoneNumber()
  {
    return Integer.parseInt(phone.getText());// phonenumber 부분을 위에서 integer형을 string형으로 저장 
    //해놨는데 이 부분을 다시 integer 형으로 형변환 함.
  }
}
