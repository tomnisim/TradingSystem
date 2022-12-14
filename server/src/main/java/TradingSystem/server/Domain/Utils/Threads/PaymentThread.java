package TradingSystem.server.Domain.Utils.Threads;

import TradingSystem.server.Domain.ExternalSystems.PaymentAdapter;
import TradingSystem.server.Domain.ExternalSystems.PaymentInfo;

public class PaymentThread implements Runnable {

    private int value;
    private double total_price;
    private PaymentInfo paymentInfo;
    private PaymentAdapter paymentAdapter;

    public PaymentThread(PaymentAdapter paymentAdapter, PaymentInfo paymentInfo, double total_price){
        this.paymentInfo = paymentInfo;
        this.total_price = total_price;
        this.paymentAdapter = paymentAdapter;
        this.value = -1;

    }
    @Override
    public void run(){
        value = this.paymentAdapter.payment(paymentInfo, total_price);
    }

    /**
     * Requirement 6
     * @return the answer we got from the external service.
     */
    public int get_value(){
        return this.value;
    }
}
