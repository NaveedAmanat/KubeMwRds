package com.idev4.rds.dto;

public class BillInquiry {

    public String response_code;

    public String consumer_detail;

    public String bill_status;

    public String due_date;

    public Double amount_within_duedate;

    public Double amount_after_duedate = 0.00;

    public String billing_month = " ";

    public String date_paid = " ";

    public String amount_paid = " ";

    public String tran_auth_id = " ";

    public String reserved;

    public String error;

}
