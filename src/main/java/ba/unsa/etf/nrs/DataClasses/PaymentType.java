package ba.unsa.etf.nrs.DataClasses;

public class PaymentType {
    private int id;
    private String paymentTypeProvider;
    private String description;

    public PaymentType() {
    }

    public PaymentType(int id, String paymentTypeProvider, String description) {
        this.id = id;
        this.paymentTypeProvider = paymentTypeProvider;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentTypeProvider() {
        return paymentTypeProvider;
    }

    public void setPaymentTypeProvider(String paymentTypeProvider) {
        this.paymentTypeProvider = paymentTypeProvider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
