package TradingSystem.server.Domain.UserModule;

import TradingSystem.server.DAL.HibernateUtils;
import TradingSystem.server.Domain.StoreModule.Basket;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.DiscountPolicy;
import TradingSystem.server.Domain.StoreModule.Product.Product;
import TradingSystem.server.Domain.StoreModule.Purchase.Purchase;
import TradingSystem.server.Domain.StoreModule.Store.Store;
import TradingSystem.server.Domain.StoreModule.Store.StoreInformation;
import TradingSystem.server.Domain.StoreModule.StoreController;
import TradingSystem.server.Domain.Utils.Exception.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MapKeyClass(value = Store.class)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "cart_baskets",
            joinColumns = {@JoinColumn(name = "cart", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "basket", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "store")
    private Map<Store, Basket> baskets;                // storeID,Basket

    public Cart() {
        baskets = new HashMap<>();
    }

    // ------------------------------ setters ------------------------------
    public void setBaskets(Map<Store, Basket> baskets) {
        this.baskets = baskets;
    }

    // ------------------------------ methods ------------------------------
    public Basket getBasket(int storeID, String email) {
        if (!baskets.containsKey(storeID))
            return new Basket(storeID, email);
        return baskets.get(storeID);
    }

    public void addBasket(Store store, Basket basket) {
        this.baskets.put(store, basket);
    }

    public void removeBasketIfNeeded(int storeID, Basket storeBasket) {
        if (storeBasket.isEmpty())
            baskets.remove(storeID);
//        HibernateUtils.remove(storeBasket);
    }

    public Map<Store, Basket> getBaskets() {
        return baskets;
    }

    public void clear() {
        for (Map.Entry<Store, Basket> entry : baskets.entrySet()) {
            entry.getValue().clear();
            baskets.remove(entry.getKey(), entry.getValue());
            HibernateUtils.remove(entry.getValue());
        }
    }

    public void remove_product_from_cart(Store store, Product p) throws MarketException {
        if (!this.baskets.containsKey(store))
            throw new BasketException("user dont have item's from specified store");
        Basket basket = baskets.get(store);
        basket.removeProduct(p);
        if (basket.isEmpty()) {
            baskets.remove(store);
            HibernateUtils.remove(basket);
//            HibernateUtils.remove(basket.getBasket_id());
        }
    }

    /**
     * this method served both regular add product to cart & add product after confirm bid offer.
     *
     * @param store
     * @param p
     * @param quantity
     * @param email
     * @param price_per_unit -
     * @throws MarketException
     */
    public void add_product_to_cart(Store store, Product p, int quantity, String email, double price_per_unit) throws MarketException {
        Basket basket = baskets.getOrDefault(store, new Basket(store.getStore_id(), email));
//        if (!email.equals("guest")) {
//            HibernateUtils.persist(basket);
//            HibernateUtils.commit();
//            HibernateUtils.beginTransaction();
//        }
        basket.addProduct(p, quantity, price_per_unit);
        this.baskets.put(store, basket);
    }

    public void edit_product_quantity_in_cart(Store store, Product p, int quantity) throws MarketException {
        if (!baskets.containsKey(store))
            throw new NoUserRegisterdException("user haven't bought product from this store.");
        baskets.get(store).changeQuantity(p, quantity);
//        HibernateUtils.merge(this);
    }

    public double check_cart_available_products_and_calc_price(int user_age) throws MarketException {
        double cart_price = 0;
        for (Map.Entry<Store, Basket> entry : baskets.entrySet()) {
            Store store = entry.getKey();
//            store = HibernateUtils.merge(store);
            Basket basket = entry.getValue();
            if (!store.is_active()) throw new PurchaseException("store " + store.getStore_id() + " is not active");
            double basket_price = store.check_available_products_and_calc_price(user_age, basket); // throw if not available
            cart_price += basket_price;
        }
        return cart_price;
    }

    public Map<Integer, Purchase> update_stores_inventory(int purchase_id) throws MarketException {
        Map<Integer, Purchase> store_id_purchase = new HashMap<>();
        for (Map.Entry<Store, Basket> entry : baskets.entrySet()) {
            Store store = entry.getKey();
            Basket basket = entry.getValue();
            Purchase purchase = store.remove_basket_products_from_store(basket, purchase_id);
            store_id_purchase.put(store.getStore_id(), purchase);
        }
        return store_id_purchase;
    }

    public void verify_not_empty() throws BasketException {
        if (this.baskets.size() == 0)
            throw new BasketException("user try to buy empty cart");
    }

    public CartInformation cartInformation() {
        HashMap<StoreInformation, Basket> answer = new HashMap<>();
        DiscountPolicy discountPolicy;
        double discount = 0;
        for (Map.Entry<Store, Basket> entry : this.baskets.entrySet()) {
            discountPolicy = entry.getKey().getDiscountPolicy();
            discount += discountPolicy.calculateDiscount(entry.getValue());
            StoreInformation temp = new StoreInformation(entry.getKey());
            answer.put(temp, entry.getValue());
        }
        return new CartInformation(answer, discount);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


//    public void merge(){
//        Cart load = HibernateUtils.getEntityManager().find(this.getClass(),id);
//        HibernateUtils.getEntityManager().merge(load);
//    }
}
