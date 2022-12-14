package TradingSystem.server.Domain.StoreModule.Policy;

import TradingSystem.server.Domain.StoreModule.Basket;
import TradingSystem.server.Domain.StoreModule.Product.Product;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Entity
@DiscriminatorValue("Predict")
public class Predict extends Ipredict {
    //on what
    private String category;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Product product;

    //what type < > =
    private boolean above;//true=above false=below
    private boolean equql;//true=to regerd false=to not regerd

    //value
    private int num;

    //field
    private boolean price_constraint;
    private boolean quantity_constraint;
    private boolean age_constraint;
    private boolean time_constraint;
    private boolean category_constraint;
    private boolean product_constraint;

    //time
    private int year;
    private int month;
    private int day;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Predict(String category, Product product, boolean above, boolean equql,
                   int num, boolean price, boolean quantity, boolean age,
                   boolean time, int year, int month, int day) {
        this.category = category;
        this.product = product;
        this.above = above;
        this.equql = equql;
        this.category_constraint = category != "";
        this.product_constraint = product != null;
        this.num = num;
        this.price_constraint = price;
        this.quantity_constraint = quantity;
        this.age_constraint = age;
        this.time_constraint = time;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Predict() {

    }


    private boolean CanApply(int age, Product product, int quantity, double price) {
        String product_category = product.getCategory();
        boolean quantityCheck = this.check_valid_quantity(quantity);
        boolean CategoryCheck = this.check_valid_category(product_category);
        boolean TimeCheck = this.check_valid_time();
        boolean AgeCheck = this.check_valid_age(age);
        boolean PriceCheck = this.check_valid_price(price * quantity);
        boolean ProductCheck = this.checkProduct(product);
        return quantityCheck || CategoryCheck || TimeCheck || AgeCheck || PriceCheck || ProductCheck;
    }

    public boolean CanApply(int age, Basket b) {
        Map<Product, Integer> map = b.getProducts_and_quantities();
        for (Map.Entry<Product, Integer> entry : map.entrySet())
            if (CanApply(age, entry.getKey(), entry.getValue(), entry.getKey().getOriginal_price()))
                return true;
        return false;
    }

    public boolean CanApply(Basket b) {
        Map<Product, Integer> map = b.getProducts_and_quantities();
        for (Map.Entry<Product, Integer> entry : map.entrySet())
            if (CanApply(entry.getKey(), entry.getValue(), entry.getKey().getOriginal_price()))
                return true;
        return false;
    }

    private boolean CanApply(Product product, int quantity, double price) {
        String product_category = product.getCategory();
        boolean quantityCheck = this.check_valid_quantity(quantity);
        boolean CategoryCheck = this.check_valid_category(product_category);
        boolean TimeCheck = this.check_valid_time();
        boolean PriceCheck = this.check_valid_price(price * quantity);
        boolean ProductCheck = this.checkProduct(product);
        return quantityCheck || CategoryCheck || TimeCheck || PriceCheck || ProductCheck;
    }

    private boolean checkProduct(Product product) {
        if (product_constraint)
            if (equql)
                return product.getName().equals(this.product.getName());
            else
                return !product.getName().equals(this.product.getName());
        return false;
    }

    private boolean checkField(double numTocheck) {
        if (equql)
            return num == numTocheck;
        if (above)
            return numTocheck > num;
        else
            return numTocheck < num;
    }

    private boolean check_valid_age(int age) {
        if (age_constraint)
            return checkField(age);
        return false;
    }

    //TODO improve time formats allowed
    private boolean check_valid_time() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        if (time_constraint) {
            if (equql)
                return year == this.year && month == this.month && this.day == day;
            else
                return year != this.year || month != this.month || this.day != day;
        }
        return false;
    }

    private boolean check_valid_category(String product_category) {
        if (category_constraint)
            if (equql)
                return product_category.equals(this.category);
            else
                return !product_category.equals(this.category);
        return false;
    }

    private boolean check_valid_quantity(int quantity) {
        if (quantity_constraint)
            return checkField(quantity);
        return false;
    }

    public boolean check_valid_price(double price) {
        if (price_constraint)
            return checkField(price);
        return false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isAbove() {
        return above;
    }

    public void setAbove(boolean above) {
        this.above = above;
    }

    public boolean isEquql() {
        return equql;
    }

    public void setEquql(boolean equql) {
        this.equql = equql;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isPrice_constraint() {
        return price_constraint;
    }

    public void setPrice_constraint(boolean price_constraint) {
        this.price_constraint = price_constraint;
    }

    public boolean isQuantity_constraint() {
        return quantity_constraint;
    }

    public void setQuantity_constraint(boolean quantity_constraint) {
        this.quantity_constraint = quantity_constraint;
    }

    public boolean isAge_constraint() {
        return age_constraint;
    }

    public void setAge_constraint(boolean age_constraint) {
        this.age_constraint = age_constraint;
    }

    public boolean isTime_constraint() {
        return time_constraint;
    }

    public void setTime_constraint(boolean time_constraint) {
        this.time_constraint = time_constraint;
    }

    public boolean isCategory_constraint() {
        return category_constraint;
    }

    public void setCategory_constraint(boolean category_constraint) {
        this.category_constraint = category_constraint;
    }

    public boolean isProduct_constraint() {
        return product_constraint;
    }

    public void setProduct_constraint(boolean product_constraint) {
        this.product_constraint = product_constraint;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
