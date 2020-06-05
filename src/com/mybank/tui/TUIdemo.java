package com.mybank.tui;

import com.mybank.data.DataSource;
import com.mybank.domain.*;
import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        // Load bank data from file
        DataSource dataSource = new DataSource("src\\com\\mybank\\data\\test.dat");
        dataSource.loadData();

        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        // custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        // end of 'File' menu

        addWindowMenu();
        
        // custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        // end of 'Help' menu

        setFocusFollowsMouse(true);
        // Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About",
                    "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich")
                            .show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 45, 15, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());

                    Customer customer = Bank.getCustomer(custNum);

                    details.setText(getCustomerDetails(customer));
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }

    private String getCustomerDetails(Customer customer){
        StringBuilder details = new StringBuilder();
        details.append("Owner name: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");

        for(int i = 0; i < customer.getNumberOfAccounts(); i++){
            Account account = customer.getAccount(i);
            if(account != null){

                if (account instanceof SavingsAccount) {
                    details.append("\nAccount Type: 'Savings'")
                    .append("\nBalance: ").append(account.getBalance()).append("\n");
                } 
                else if (account instanceof CheckingAccount) {
                    details.append("\nAccount Type: 'Checking' ")
                    .append("\nBalance: ").append(account.getBalance()).append("\n");
                }
            }
        }
        return details.toString();
    }
}
