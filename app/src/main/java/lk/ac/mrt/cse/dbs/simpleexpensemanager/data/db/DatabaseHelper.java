package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHelper extends SQLiteOpenHelper {

    public  static final String DB_NAME = "170612T";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table account (accountNo TEXT(50) PRIMARY KEY,bankName TEXT(50),accountHolderName TEXT(50),balance REAL) ");
        sqLiteDatabase.execSQL(" create table transactions (accountNo TEXT(50) ,date date, expenseType TEXT(20),amount REAL,FOREIGN KEY (accountNo) REFERENCES account(accountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(sqLiteDatabase);
    }

    public boolean insertAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bankName",account.getBankName());
        contentValues.put("accountHolderName",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        long result = db.insert("account",null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bankName",account.getBankName());
        contentValues.put("accountHolderName",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        long result = db.update("account",contentValues,"accountNo = ?",new String[]{account.getAccountNo()});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }


    public Account getAccount(String accNo){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor response = db.rawQuery("SELECT * FROM account WHERE accountNo = ?",new String[]{accNo});
        Account account = null;
        if(response.getCount() == 0){
            return account;
        }else{
            while(response.moveToNext()){
                String accountNo = response.getString(0);
                String bankName = response.getString(1);
                String accountHolderName = response.getString(2);
                double balance = response.getDouble(3);
                account = new Account(accountNo,bankName,accountHolderName,balance);
            }
            return account;
        }
    }

    public ArrayList<Account> getAllAccounts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor response = db.rawQuery("SELECT * FROM account",null);
        ArrayList<Account> accountList=new ArrayList<>();
        if(response.getCount()==0){
            return accountList;
        }else{

            while(response.moveToNext()){
                String accountNo = response.getString(0);
                String bankName = response.getString(1);
                String accountHolderName = response.getString(2);
                double balance = response.getDouble(3);
                accountList.add(new Account(accountNo,bankName,accountHolderName,balance));
            }
            return accountList;
        }
    }

    public boolean deleteAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("account","accountNo = "+accountNo,null) > 0;

    }

    public boolean logTransaction(Transaction transaction){

        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",transaction.getAccountNo());
        contentValues.put("date",format.format(transaction.getDate()));
        contentValues.put("expenseType",transaction.getExpenseType().toString());
        contentValues.put("amount",transaction.getAmount());


        long response = db.insert("transactions",null,contentValues);
        if(response == -1){
            return false;
        }else{
            return true;
        }



    }

    public ArrayList<Transaction> getTransactions(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor response = db.rawQuery("SELECT * FROM transactions",null);
        return populateTransactions(response);
    }

    public ArrayList<Transaction> getTransactions(int limit){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor response = db.rawQuery("SELECT * FROM transactions LIMIT "+limit,null);
        return populateTransactions(response);
    }



    private ArrayList<Transaction> populateTransactions(Cursor response){

        ArrayList<Transaction> transactionList=new ArrayList<>();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        if(response.getCount()==0){
            return transactionList;
        }else{

            while(response.moveToNext()){
                String accountNo = response.getString(0);
                Date date = new Date();
                ExpenseType expenseType = ExpenseType.INCOME;
                try {
                    date =  format.parse(response.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(response.getString(2) == "INCOME"){
                    expenseType = ExpenseType.INCOME;
                }else if(response.getString(2) == "EXPENSE"){
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = response.getDouble(3);
                transactionList.add(new Transaction(date,accountNo,expenseType,amount));
            }
            return transactionList;
        }
    }
}
