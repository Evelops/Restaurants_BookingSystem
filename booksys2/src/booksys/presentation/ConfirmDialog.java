/*
 * Restaurant Booking System: example code to accompany
 *
 * "Practical Object-oriented Design with UML"
 * Mark Priestley
 * McGraw-Hill (2004)
 */

package booksys.presentation ;

import java.awt.* ;
import java.awt.event.* ;

class ConfirmDialog extends Dialog
{
  protected Label   messageLabel ;
  protected Label   blankLabel ;
  protected boolean confirmed ;
  protected Button  confirm ;
  protected Button  cancel ;
  
  ConfirmDialog(Frame owner, String message, boolean choice)
  {
    super(owner, "Warning!", true) ;
    
    addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  confirmed = false ;
	  ConfirmDialog.this.hide() ;
	}
      }) ;

    messageLabel = new Label(message, Label.RIGHT) ;
    blankLabel = new Label("", Label.RIGHT) ;
// Booking 에서 new Reservation에 가서 예약할때 ok 버튼 눌렀을 때 동작하는 부분
    confirm = new Button("Ok") ;
    confirm.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  confirmed = true ;
	  ConfirmDialog.this.hide() ;
	}
      }) ;
// Booking 에서 new Reservation에 가서 예약할때 cancel 버튼 눌렀을 때 동작하는 부분 
    cancel = new Button("Cancel") ;
    cancel.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  confirmed = false ;
	  ConfirmDialog.this.hide() ;
	}
      }) ;

    setLayout( new GridLayout(0, 2) ) ;
    
    add(messageLabel) ;
    add(blankLabel) ;
    
    add(confirm) ;
    if (choice) {
      add(cancel) ;
    }

    pack() ;
  }

  boolean isConfirmed()
  {
    return confirmed ;
  }
}
