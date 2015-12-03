package mum.mpp.views;

import java.util.Date;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontBuilder;
import javafx.scene.text.Text;
import mum.mpp.tay.backendinterface.LibrarianInterface;
import mum.mpp.tay.backendinterface.ServiceException;
import mum.mpp.tay.entity.Book;
import mum.mpp.tay.entity.BookCopy;
import mum.mpp.tay.entity.CheckoutRecord;
import mum.mpp.tay.entity.Member;
import mum.mpp.tay.vo.BookVo;

public class LibrarianOprationDetailController {
	private LibrarianInterface user;

	public void setUser(LibrarianInterface user) {
		this.user = user;
	}
	private ObservableList<BookVo> checkoutBooks = FXCollections.observableArrayList();
	private ObservableList<BookCopy> bookcopys = FXCollections.observableArrayList();
	private BookVo bookDetail;

	@FXML
	private TableView<BookVo> checkoutBooksTable;
	@FXML
	private TableColumn<BookVo, String> firstColumn;
	@FXML
	private TableColumn<BookVo, Number> secondColumn;
	@FXML
	private TableColumn<BookVo, Date> thirdColumn;
	@FXML
    private Label isbnNumberLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label keepDaysLabel;
    @FXML
    private Label copyNumberLabel;
    @FXML
    private Label checkoutDate;
    @FXML
    private Label dueDate;
    @FXML
    private Label checkinDate;
//    book.getiSBNNumber(),
//	book.getTitle(), book.getMaximumCheckoutDurationInDays(), checkoutRecord.getBook().getCopyNumber(),
//	checkoutRecord.getCheckoutDate(), checkoutRecord.getDueDate(), checkoutRecord.getCheckinDate());
    @FXML
    private TextField memberIdField;
    @FXML
    private TextField iSBNField;
    
    @FXML
    private Text memberErrMsg;
    @FXML
    private Text ISBNErrMsg;
	@FXML
    private Button checkoutBtn;
	@FXML
	private Button checkinBtn;
    
    private boolean memberIdValid = false;
    private boolean isbnNoValid = false;
    private int selectedIndex = -1;
    private BookVo selectedRow = null;
    
	public void searchMemberInfo() {
		
		initChkoutRecordEnv();
		
		//validate
		String memberId = memberIdField.getText();
		if(null == memberId || "".equals(memberId)){
			memberErrMsg.setText("Empty imput");
			memberErrMsg.setFill(Color.RED);
			return;
		}else{
			try{

				Long id = Long.valueOf(memberId);
				
				//TODO remove mock
				Member libMember = user.getMemberById(id);
				
				if(null == libMember){
					memberErrMsg.setText("Can not find by that ID");
					memberErrMsg.setFill(Color.RED);
					return;
				}else{
//					System.out.println("btnLogin_click2");
//					checkoutBooks.add(new BookVo("1", "1", 1));
//					checkoutBooks.add(new BookVo("2", "2", 2));
					memberErrMsg.setText("Valid");
					memberErrMsg.setFill(Color.GREEN);
					memberIdValid = true;
					if(isbnNoValid)
						checkoutBtn.setDisable(false);
					
					List<CheckoutRecord> memberRecord = user.getMemberRecord(id);
					for(CheckoutRecord checkoutRecord: memberRecord){
						Book book = checkoutRecord.getBook().getBook();
						BookVo bookVo = new BookVo(checkoutRecord.getId(), book.getiSBNNumber(),
								book.getTitle(), book.getMaximumCheckoutDurationInDays(), checkoutRecord.getBook().getCopyNumber(),
								checkoutRecord.getCheckoutDate(), checkoutRecord.getDueDate(), checkoutRecord.getCheckinDate());
						
						checkoutBooks.add(bookVo);
						
					}

			        // Add observable list data to the table
					checkoutBooksTable.setItems(checkoutBooks);
					
			        firstColumn.setCellValueFactory(cellData -> cellData.getValue().getiSBNNumber());
			        secondColumn.setCellValueFactory(cellData -> cellData.getValue().getCopyNumber());
			        thirdColumn.setCellValueFactory(cellData -> cellData.getValue().getDueDate());
				}
			} catch (ServiceException e) {
				memberErrMsg.setText(e.getMessage());
				memberErrMsg.setFill(Color.RED);
				return;
			} catch(Exception e){
				e.printStackTrace();
				memberErrMsg.setText("Pls input a number!");
				memberErrMsg.setFill(Color.RED);
				return;
			}
			
		}
		
		
	}

	private void initChkoutRecordEnv() {
		//clear err msg
		memberErrMsg.setText("");
		memberErrMsg.setFill(Color.BLACK);
		//clear Data
		checkoutBooks.clear();
		//reset state
		memberIdValid = false;
	}
	
	private void bindISBNFieldEvent(Boolean newValue) {
		if(!newValue){
			initISBNEnv();
			
			//validate
			String iSBNNo = iSBNField.getText();
			if(null == iSBNNo || "".equals(iSBNNo)){
				ISBNErrMsg.setText("Empty imput");
				ISBNErrMsg.setFill(Color.RED);
				return;
			} else{
				try {
					boolean bookAvailable = user.isBookAvailable(iSBNNo);
					if(bookAvailable){
						isbnNoValid = true;
						if(memberIdValid)
							checkoutBtn.setDisable(false);
						ISBNErrMsg.setText("Available");
						ISBNErrMsg.setFill(Color.GREEN);
					}else{
						ISBNErrMsg.setText("Not available");
						ISBNErrMsg.setFill(Color.YELLOW);
					}
					return;
				} catch (ServiceException e) {
					ISBNErrMsg.setText(e.getMessage());
					ISBNErrMsg.setFill(Color.RED);
					return;
				}
			}
		}

//		return null;
	}
	
	private void initISBNEnv() {
		//clear err msg
		ISBNErrMsg.setText("");
		ISBNErrMsg.setFill(Color.BLACK);
//		//clear Data
//		checkoutBooks.clear();
		//reset state
		isbnNoValid = false;

	}	
	
    private void showChkOutRecordsDetails(BookVo book) {
    	int newIndex = checkoutBooksTable.getSelectionModel().getSelectedIndex();
    	if(newIndex != -1)
    		selectedIndex = newIndex;
    	if(null != checkoutBooksTable.getSelectionModel().getSelectedItem())
    		selectedRow = checkoutBooksTable.getSelectionModel().getSelectedItem();
        if (book != null) {
            // Fill the labels with info from the book object.

        	isbnNumberLabel.setText(book.getiSBNNumber().get());
        	titleLabel.setText(book.getTitle().get());
        	keepDaysLabel.setText(String.valueOf(book.getMaximumCheckoutDurationInDays().get()));
        	copyNumberLabel.setText(String.valueOf(book.getCopyNumber().get()));
        	checkoutDate.setText(book.getCheckoutDate().get().toString());
        	dueDate.setText(book.getDueDate().get().toString());
        	Object object = book.getCheckinDate().get();
        	if(null == object){
        		checkinBtn.setVisible(true);
        		checkinDate.setText("");
        	}else{
        		checkinDate.setText(object.toString());
        	}
			

        } else {
            // Person is null, remove all the text.
        	isbnNumberLabel.setText("");
        	titleLabel.setText("");
        	keepDaysLabel.setText("");
        	copyNumberLabel.setText("");
        	checkoutDate.setText("");
        	dueDate.setText("");
        	checkinDate.setText("");
        }
    }
    

    @FXML
    private void initialize() {
        // Listen for selection changes and show the person details when changed.
    	checkoutBooksTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showChkOutRecordsDetails(newValue));
    	
    	//onblur for member id
        memberIdField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
//                {
//                    System.out.println("Textfield on focus");
//                }
//                else
                {
                	searchMemberInfo();
                }
            }
        });
        
        iSBNField.focusedProperty().addListener((observable, oldValue, newValue) -> bindISBNFieldEvent(newValue));
        
//        checkoutBtn.disableProperty().addListener(arg0);
        checkoutBtn.setDisable(true);
    }



	@FXML
	public void btnCheckout_click() {
		if(memberIdValid && isbnNoValid){
			try {
				user.checkout(iSBNField.getText(), Long.valueOf(memberIdField.getText()));
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		searchMemberInfo();
		
		iSBNField.setText("");
		checkoutBtn.setDisable(true);
		initISBNEnv();
	}
	
	@FXML
	public void btnCheckin_click() {
		System.out.println("btnCheckin_click");
		if(null == checkinDate.getText() || "".equalsIgnoreCase(checkinDate.getText())){
			try {
//				checkoutBooksTable.getSelectionModel().getSelectedItem()
				user.checkIn(selectedRow.getId());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		searchMemberInfo();
//		bindISBNFieldEvent(false);
		
		checkoutBooksTable.getSelectionModel().select(selectedIndex);
	}

}
