package TradingSystem.server.Domain.StoreModule.Store;

import TradingSystem.server.Domain.Questions.QuestionController;
import TradingSystem.server.Domain.StoreModule.*;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.ComplexDiscountComponent;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.DiscountComponent;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.DiscountPolicy;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.logicCompnent.OrCompositePredict;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.logicCompnent.xorDiscountComponent;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.logicCompnent.andCompsoitePredict;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.numric.maxDiscountComponent;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.numric.plusDiscountComponent;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.simple.simpleDiscountComponentByCategory;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.simple.simpleDiscountComponentByProduct;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.simple.simpleDiscountComponentByStore;
import TradingSystem.server.Domain.StoreModule.Policy.Discount.simple.simpleDiscountComponent;
import TradingSystem.server.Domain.StoreModule.Policy.Ipredict;
import TradingSystem.server.Domain.StoreModule.Policy.Predict;
import TradingSystem.server.Domain.StoreModule.Policy.Purchase.*;
import TradingSystem.server.Domain.StoreModule.Product.Product;
import TradingSystem.server.Domain.StoreModule.Purchase.Purchase;
import TradingSystem.server.Domain.StoreModule.Purchase.StorePurchase;
import TradingSystem.server.Domain.StoreModule.Purchase.StorePurchaseHistory;
import TradingSystem.server.Domain.UserModule.AssignUser;
import TradingSystem.server.Domain.Utils.Exception.*;
import TradingSystem.server.Domain.Utils.Utils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class Store {

    //TODO: change all methods belongs to stuff_emails to point user.
    //TODO: reviews - should contain users / users_email ? if user changes his email ?

    // -- fields
    private int store_id;
    private AssignUser founder;
    private Map<AssignUser, Appointment> stuffs_and_appointments;
    private String name;
    public String foundation_date;
    private Map<Product, Integer> inventory; // product & quantity
    private boolean active;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;
    private StorePurchaseHistory purchases_history;
    private StoreReview storeReview;
    private AtomicInteger product_ids_counter;
    private Object owners_lock;
    private Object managers_lock;
    private HashMap<String, Ipredict> predictList;

    // -- constructors
    public Store(int store_id, String name, AssignUser founder,AtomicInteger ai) {
        this.discountPolicy = new DiscountPolicy();
        this.purchasePolicy = new PurchasePolicy();
        this.store_id = store_id;
        this.founder = founder;
        this.name = name;
        this.product_ids_counter = ai;
        this.active = true;
        this.foundation_date = LocalDate.now().toString();
        this.storeReview = new StoreReview();
        this.purchases_history = new StorePurchaseHistory(this.name);
        this.inventory = new ConcurrentHashMap<>();
        this.stuffs_and_appointments = new ConcurrentHashMap<>();
        this.owners_lock = new Object();
        this.managers_lock = new Object();
        this.predictList = new HashMap<>();
    }

    public Store() {
    }

    // ------------------------------ getters ------------------------------

    public Map<AssignUser, Appointment> getStuffs_and_appointments() {
        return stuffs_and_appointments;
    }

    public boolean isActive() {
        return active;
    }

    public StorePurchaseHistory getPurchases_history() {
        return purchases_history;
    }

    public AtomicInteger getProduct_ids_counter() {
        return product_ids_counter;
    }

    public Object getOwners_lock() {
        return owners_lock;
    }

    public Object getManagers_lock() {
        return managers_lock;
    }

    // ------------------------------ setters ------------------------------

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setFounder(AssignUser founder) {
        this.founder = founder;
    }

    public void setStuffs_and_appointments(Map<AssignUser, Appointment> stuffs_and_appointments) {
        this.stuffs_and_appointments = stuffs_and_appointments;
    }

    public void setFoundation_date(String foundation_date) {
        this.foundation_date = foundation_date;
    }

    public void setInventory(Map<Product, Integer> inventory) {
        this.inventory = inventory;
    }

    public void setPurchases_history(StorePurchaseHistory purchases_history) {
        this.purchases_history = purchases_history;
    }

    public void setStoreReview(StoreReview storeReview) {
        this.storeReview = storeReview;
    }

    public void setProduct_ids_counter(AtomicInteger product_ids_counter) {
        this.product_ids_counter = product_ids_counter;
    }

    public void setOwners_lock(Object owners_lock) {
        this.owners_lock = owners_lock;
    }

    public void setManagers_lock(Object managers_lock) {
        this.managers_lock = managers_lock;
    }


    // -- public methods

    public AssignUser getFounder() {
        return founder;
    }

    public int get_store_rating() {
        return this.storeReview.getAvgRating();
    }

    public void add_product_review(int product_id, String user_email, String review) throws ObjectDoesntExsitException {
        Product p = this.getProduct_by_product_id(product_id); //throws
        p.add_review(user_email, review);
    }

    public AndporchaseRule add_and_purchase_rule(String nameOfRule,String left, String right) throws WrongPermterException {
        porchaseRule purchaseright = purchasePolicy.getPolicy(left);
        porchaseRule purchaseleft = purchasePolicy.getPolicy(right);
        AndporchaseRule and = new AndporchaseRule(purchaseleft, purchaseright);
        purchasePolicy.addRule(nameOfRule,and);
        return and;
    }

    public OrporchaseRule add_or_purchase_rule(String name, String left, String right) throws WrongPermterException {
        porchaseRule purchaseright = purchasePolicy.getPolicy(left);
        porchaseRule purchaseleft = purchasePolicy.getPolicy(right);
        OrporchaseRule or = new OrporchaseRule(purchaseleft, purchaseright);
        purchasePolicy.addRule(name, or);
        return or;
    }

    public void add_store_rating(AssignUser user, int rating) throws MarketException {
        if (this.stuffs_and_appointments.containsKey(user))
            throw new NoPremssionException("store members can't rate their store");
        this.storeReview.add_rating(user.get_user_email(), rating);
    }

    public Predict addPredict(String catgorey, Product product, boolean above, boolean equql, int num, boolean price, boolean quantity, boolean age, boolean time, int year, int month, int day, String name) throws WrongPermterException {
        Predict predict = new Predict(catgorey, product, above, equql, num, price, quantity, age, time, year, month, day);
        if (predictList.keySet().contains(name))
            throw new WrongPermterException("there is alreay a predict with the same name");
        predictList.put(name, predict);
        return predict;
    }

    //start of discount policy
    public void remove_discount_rule(String name) {
        discountPolicy.removeRule(name);
    }

    private void checkUniqName(String name, HashMap map) throws WrongPermterException {
        if (map.keySet().contains(name))
            throw new WrongPermterException("there is a predict with this name in the store,please choose another name");
    }


    public List<String> getDiscountPolicyNames() {
        List<String> list = new LinkedList<>();
        for (String s : discountPolicy.getPolicyNames())
            list.add(s);
        return list;
    }

    public List<String> getPurchasePolicyNames() {
        List<String> list = new LinkedList<>();
        for (String s : purchasePolicy.getPolicyNames())
            list.add(s);
        return list;

    }

    public List<String> getPredicts() {
        List<String> list = new LinkedList<>();
        for (String s : predictList.keySet())
            list.add(s);
        return list;

    }

    public List<String> getSimplePredicts() {
        List<String> list = new LinkedList<>();
        for (String s : predictList.keySet())
            if (predictList.get(s) instanceof Predict)
                list.add(s);
        return list;

    }


    public andCompsoitePredict CreateAndDisocuntCompnent(String name, String left, String right) throws WrongPermterException {
        Ipredict leftPredict = predictList.get(left);
        Ipredict rightPredict = predictList.get(right);
        andCompsoitePredict toreturn = new andCompsoitePredict(leftPredict, rightPredict);
        checkUniqName(name, predictList);
        this.predictList.put(name, toreturn);
        return toreturn;
    }

    public OrCompositePredict CreateOrDisocuntCompnent(String name, String left, String right, String complex) throws WrongPermterException {
        Ipredict leftPredict = predictList.get(left);
        Ipredict rightPredict = predictList.get(right);
        OrCompositePredict toreturn = new OrCompositePredict(leftPredict, rightPredict);
        checkUniqName(name, predictList);
        this.predictList.put(name, toreturn);
        return toreturn;
    }

    public xorDiscountComponent CreateXorDisocuntCompnent(String name, String left, String right) throws WrongPermterException {
        DiscountComponent leftdiscount = discountPolicy.getDiscountCompnentByName(left);
        DiscountComponent rifhtdiscount = discountPolicy.getDiscountCompnentByName(right);
        xorDiscountComponent toreturn = new xorDiscountComponent(leftdiscount, rifhtdiscount);
        this.discountPolicy.addRule(name, toreturn);
        return toreturn;
    }

    public maxDiscountComponent CreateMaxDisocuntCompnent(String name, String left, String right) throws WrongPermterException {
        DiscountComponent leftdiscount = discountPolicy.getDiscountCompnentByName(left);
        DiscountComponent rifhtdiscount = discountPolicy.getDiscountCompnentByName(right);
        maxDiscountComponent toreturn = new maxDiscountComponent(leftdiscount, rifhtdiscount);
        this.discountPolicy.addRule(name, toreturn);
        return toreturn;
    }

    public plusDiscountComponent CreateplusDisocuntCompnent(String name, String left, String right) throws WrongPermterException {
        DiscountComponent leftdiscount = discountPolicy.getDiscountCompnentByName(left);
        DiscountComponent rifhtdiscount = discountPolicy.getDiscountCompnentByName(right);
        plusDiscountComponent toreturn = new plusDiscountComponent(leftdiscount, rifhtdiscount);
        this.discountPolicy.addRule(name, toreturn);
        return toreturn;
    }


    public simpleDiscountComponent add_simple_product_discount(String name, int id, double precent) throws WrongPermterException, ObjectDoesntExsitException {
        simpleDiscountComponent simpleDiscountComponent;
        Product p = getProduct_by_product_id(id);
        simpleDiscountComponent = new simpleDiscountComponentByProduct(p, precent);
        this.discountPolicy.addRule(name, simpleDiscountComponent);
        return simpleDiscountComponent;
    }


    public simpleDiscountComponent add_simple_discount(String name, String type, String nameOfCategorey, double precent) throws WrongPermterException {
        simpleDiscountComponent simpleDiscountComponent;
        if (type == "c")
            simpleDiscountComponent = new simpleDiscountComponentByCategory(nameOfCategorey, precent);
        else
            simpleDiscountComponent = new simpleDiscountComponentByStore(precent);
        this.discountPolicy.addRule(name, simpleDiscountComponent);
        return simpleDiscountComponent;
    }

    public ComplexDiscountComponent add_complex_discount(String name, String nameOFPredict, String nameOfPolicy) throws WrongPermterException {
        Ipredict predict = predictList.get(nameOFPredict);
        DiscountComponent simpleDiscountComponent = discountPolicy.getDiscountCompnentByName(nameOfPolicy);
        if (!(simpleDiscountComponent instanceof simpleDiscountComponent))
            throw new WrongPermterException("this polciy is not of type simple");
        ComplexDiscountComponent toreturn = new ComplexDiscountComponent(simpleDiscountComponent, predict);
        this.discountPolicy.addRule(name, toreturn);
        return toreturn;

    }


    //end of discount policy

    //purchase policy

    private Predict getSimplePredictsByName(String name) throws WrongPermterException {
        Ipredict p = predictList.get(name);
        if (!(p instanceof Predict))
            throw new WrongPermterException("the name of the predict is wrong");
        return (Predict) p;
    }

    public SimpleporchaseRule addsimplePorchaseRule(String nameOfRule, String nameForule, String name) throws WrongPermterException {
        Predict p = getSimplePredictsByName(name);
        SimpleporchaseRule Toreturn = new SimpleporchaseRule(p);
        this.purchasePolicy.addRule(nameForule, Toreturn);
        return Toreturn;
    }


    //TODO add purchase or and
    //end of purchase policy

    public void add_product_rating(String user_email, int product_id, int rate) throws MarketException {
        Product p = this.getProduct_by_product_id(product_id);//throws
        p.add_rating(user_email, rate);
    }

    public Appointment appoint_founder() throws MarketException {
        Appointment appointment = new Appointment(this.founder, this.founder, this, StoreManagerType.store_founder);
        this.stuffs_and_appointments.put(founder, appointment);
        this.founder.add_founder(this, appointment);
        return appointment;
    }

    public void close_store_permanently() throws MarketException {
        this.active = false;
        String message = "Store was closed permanently at " + LocalDate.now().toString();
        this.send_message_to_the_store_stuff(message);
        for (AssignUser user : stuffs_and_appointments.keySet()) {
            user.remove_appointment(this);
        }
        this.stuffs_and_appointments = null;
    }


    public void close_store_temporarily(AssignUser user) throws MarketException {
        this.check_permission(user, StorePermission.close_store_temporarily);
        this.active = false;
        String message = "Store was closed close_store_temporarily at " + LocalDate.now().toString();
        this.send_message_to_the_store_stuff(message);
    }


    public void open_close_store(AssignUser user) throws MarketException {
        this.check_permission(user, StorePermission.open_close_store);
        if (this.is_active())
            throw new StoreMethodException("The store is already open");
        this.active = true;
        String message = "Store was re-open at " + LocalDate.now().toString();
        this.send_message_to_the_store_stuff(message);
    }

    public StoreManagersInfo view_store_management_information(AssignUser user) throws MarketException {
        this.check_permission(user, StorePermission.view_permissions);
        //TODO: added parse to emails - appointment map
        List<AppointmentInformation> answer = new LinkedList<>();
        for (Appointment appointment : stuffs_and_appointments.values()) {
            AppointmentInformation temp = new AppointmentInformation(appointment.getMember().get_user_email(), appointment.getAppointer().get_user_email(),
                    appointment.getType().toString());
            answer.add(temp);
        }
        //end
        return new StoreManagersInfo(this.name, answer);
    }

    public boolean is_active() {
        return this.active;
    }


    public void set_permissions(AssignUser user_who_set_permission, AssignUser manager, List<StorePermission> permissions) throws MarketException {
        // check that the manager appointed by the user
        this.check_permission(user_who_set_permission, StorePermission.edit_permissions); //TODO: verify
        if (!this.get_appointer(manager).equals(user_who_set_permission))
            throw new AppointmentException("The manager is not appointed by user");
        // check that the user is not trying to change his permissions
        if (manager.equals(user_who_set_permission))
            throw new NoPremssionException("User cant change himself permissions");

        Appointment manager_permission = this.stuffs_and_appointments.get(manager);
        manager_permission.set_permissions(permissions);

    }

    public List<String> view_store_questions(AssignUser user) throws MarketException {
        this.check_permission(user, StorePermission.view_users_questions);
        return QuestionController.getInstance().view_buyers_to_store_questions(store_id);
    }

    public void add_question(AssignUser sender, String question_message) {
        QuestionController.getInstance().add_buyer_question(question_message, sender, store_id);
        this.send_message_to_the_store_stuff("new user question from :" + sender.get_user_email() + " in store " + name);
    }

    public void answer_question(AssignUser user, int question_id, String answer) throws MarketException {
        this.check_permission(user, StorePermission.view_users_questions);
        QuestionController.getInstance().answer_buyer_question(question_id, answer);
    }

    public StorePurchaseHistory view_store_purchases_history(AssignUser user) throws MarketException {
        this.check_permission(user, StorePermission.view_purchases_history);
        return this.purchases_history;
    }

    public StorePurchaseHistory admin_view_store_purchases_history() {
        return this.purchases_history;
    }

    // -- find product by ----------------------------------------------------------------------------------

    public List<Product> find_products_by_name(String product_name) {
        List<Product> products = new ArrayList<>();
        for (Product p : inventory.keySet()) {
            if (p.getName().equals(product_name)) {
                products.add(p);
            }
        }
        return products;

    }

    public List<Product> find_products_by_category(String category) {
        List<Product> products = new ArrayList<>();
        for (Product p : inventory.keySet()) {
            if (p.getCategory().equals(category)) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Product> find_products_by_key_words(String key_words) {
        List<Product> products = new ArrayList<>();
        for (Product p : inventory.keySet()) {
            if (p.getKey_words().contains(key_words)) {
                products.add(p);
            }
        }
        return products;
    }
    // -----------------------------------------------------------------------------------------------------


    public Map<Product, Integer> add_product(AssignUser user, String name, double price, String category, List<String> key_words, int quantity) throws MarketException {
        this.check_permission(user, StorePermission.add_item);
        if (price <= 0)
            throw new ProductAddingException("price must be more then zero");
        if (quantity < 1)
            throw new ProductAddingException("quantity must be more then zero");
        Utils.nameValidCheck(name);
        Utils.nameValidCheck(category);
        for (Product p : inventory.keySet()) {
            if (p.getName().equals(name))
                throw new ProductAddingException("product already exists in the store");
        }
        int product_id = this.product_ids_counter.getAndIncrement();
        Product product = new Product(name, product_id, price, category, key_words, store_id);
        inventory.put(product, quantity);
        return inventory;
    }

    public Map<Product, Integer> delete_product(int product_id, AssignUser user) throws MarketException {
        Product product_to_remove = this.getProduct_by_product_id(product_id);
        this.check_permission(user, StorePermission.remove_item);
        inventory.remove(product_to_remove);
        return inventory;
    }

    // -- edit product - Start ----------------------------------------------------------------------------------

    public void edit_product_name(AssignUser user, int product_id, String name) throws MarketException {
        Product to_edit = this.getProduct_by_product_id(product_id);
        this.check_permission(user, StorePermission.edit_item_name);
        to_edit.setName(name);
    }

    public void edit_product_price(AssignUser user, int product_id, double price) throws MarketException {
        Product to_edit = this.getProduct_by_product_id(product_id);
        this.check_permission(user, StorePermission.edit_item_price);
        to_edit.setPrice(price);
    }

    public void edit_product_category(AssignUser user, int product_id, String category) throws MarketException {
        Product to_edit = this.getProduct_by_product_id(product_id);
        this.check_permission(user, StorePermission.edit_item_category);
        to_edit.setCategory(category);
    }

    public void edit_product_key_words(AssignUser user, int product_id, List<String> key_words) throws MarketException {
        Product to_edit = this.getProduct_by_product_id(product_id);
        this.check_permission(user, StorePermission.edit_item_keywords);
        to_edit.setKey_words(key_words);
    }

    // -----------------------------------------------------------------------------------------------------


    public synchronized double check_available_products_and_calc_price(int user_age, Basket basket) throws MarketException {
        Map<Product, Integer> products_and_quantities = basket.getProducts_and_quantities();
        purchasePolicy.checkPolicy(user_age, basket);
        for (Product p : products_and_quantities.keySet()) {
            this.checkAvailablityAndGet(p.getProduct_id(), products_and_quantities.get(p));
        }
        double discount = discountPolicy.calculateDiscount(basket);
        return basket.getTotal_price() - discount;
    }


    //TODO: policies
    // check product is available - throws if no.
    public synchronized Product checkAvailablityAndGet(int product_id, int quantity) throws MarketException {
        Product p = this.getProduct_by_product_id(product_id);
        if (p == null) {
            throw new ProductAddingException("checkAvailablityAndGet: Product is not exist");
            //not suppose to happen
            //add to logger
        }
        int product_quantity = this.inventory.get(p);
        if (quantity <= product_quantity) {
            return p;
        }
        throw new ProductAddingException("Store.checkAvailablityAndGet: Product is not available");
    }

    /**
     * @param basket      we call this method with all the basket of a single cart
     * @param purchase_id the index from store controller
     * @return
     */
    public synchronized Purchase remove_basket_products_from_store(Basket basket, int purchase_id) throws MarketException {
        Map<Product, Integer> products_and_quantities = basket.getProducts_and_quantities();

        for (Product p : products_and_quantities.keySet()) {
            int first_quantity = this.inventory.get(p);
            int quantity_to_remove = products_and_quantities.get(p);
            if (first_quantity - quantity_to_remove < 0)
                throw new StoreMethodException("Store.remove_basket_products_from_store: product quantity :" + quantity_to_remove + "" +
                        " is more then available for product id :" + p.getProduct_id());
        }
        for (Product p : products_and_quantities.keySet()) {
            int first_quantity = this.inventory.get(p);
            int quantity_to_remove = products_and_quantities.get(p);
            if (first_quantity - quantity_to_remove == 0)
                this.inventory.remove(p);
            else
                this.inventory.put(p, first_quantity - quantity_to_remove);
        }
        String buyer_email = basket.get_buyer_email();
        Map<Integer, Integer> p_ids_quantity = basket.get_productsIds_and_quantity();
        Map<Integer, Double> p_ids_price = this.get_product_ids_and_total_price(basket);
        Map<Integer, String> p_ids_name = basket.getProducts_and_names();

        Purchase purchase = new Purchase(p_ids_quantity, p_ids_price, p_ids_name);
        StorePurchase purchase_to_add = new StorePurchase(purchase, buyer_email, purchase_id);
        this.purchases_history.insert(purchase_to_add);
        this.send_message_to_the_store_stuff("new purchase, with id : " + purchase_id);
        return purchase;
    }

    public void add_owner(AssignUser appointer, AssignUser new_owner) throws MarketException {
        this.check_permission(appointer, StorePermission.add_owner);
        synchronized (owners_lock) {
            Appointment appointment = this.stuffs_and_appointments.get(new_owner);
            if (appointment != null) {
                throw new AppointmentException("User to appoint is already store member");
            }

            Appointment appointment_to_add = new Appointment(new_owner, appointer, this, StoreManagerType.store_owner);
            this.stuffs_and_appointments.put(new_owner, appointment_to_add);
            new_owner.add_owner(this, appointment_to_add);
        }
    }

    public void add_manager(AssignUser appointer, AssignUser new_manager) throws MarketException {
        this.check_permission(appointer, StorePermission.add_manager);
        synchronized (managers_lock) {
            Appointment appointment = this.stuffs_and_appointments.get(new_manager);
            if (appointment != null)
                throw new AppointmentException("User to appoint is already store member");
            Appointment appointment_to_add = new Appointment(new_manager, appointer, this, StoreManagerType.store_manager);
            this.stuffs_and_appointments.put(new_manager, appointment_to_add);
            new_manager.add_manager(this, appointment_to_add);
        }
    }

    public void remove_manager(AssignUser remover, AssignUser user_to_delete_appointment) throws MarketException {
        this.check_permission(remover, StorePermission.add_manager);
        synchronized (managers_lock) {
            Appointment appointment = this.stuffs_and_appointments.get(user_to_delete_appointment);
            if (appointment == null) {
                throw new AppointmentException("User to be removed is not stuff member of this store");
            }
            if (!appointment.is_manager()) {
                throw new AppointmentException("User to be removed is not owner/founder");
            }
            if (!appointment.getAppointer().equals(remover)) {
                throw new AppointmentException("User can not remove stuff member that is not appoint by him");
            }
            this.stuffs_and_appointments.remove(user_to_delete_appointment);
            user_to_delete_appointment.remove_appointment(this);
        }
    }

    private void remove_all_appointments_by_user(AssignUser user_to_delete_appointment) throws MarketException {
        for (Appointment appointment1 : this.stuffs_and_appointments.values()) {
            if (appointment1.getAppointer().equals(user_to_delete_appointment)) {
                this.remove_owner(user_to_delete_appointment, appointment1.getMember());
                user_to_delete_appointment.remove_appointment(this);
            }
        }
    }

    public void remove_owner(AssignUser remover, AssignUser user_to_delete_appointment) throws MarketException {
        this.check_permission(remover, StorePermission.add_manager);
        synchronized (owners_lock) {
            Appointment appointment = this.stuffs_and_appointments.get(user_to_delete_appointment);
            if (appointment == null) {
                throw new AppointmentException("User to be removed is not stuff member of this store");
            }

            if (!appointment.is_owner()) {
                throw new AppointmentException("User to be removed is not owner");
            }

            if (!appointment.getAppointer().equals(remover)) {
                throw new AppointmentException("User can not remove stuff member that is not appoint by him");
            }

            remove_all_appointments_by_user(user_to_delete_appointment);
            this.stuffs_and_appointments.remove(user_to_delete_appointment);
            user_to_delete_appointment.remove_appointment(this);
        }
    }


    public Product getProduct_by_product_id(int product_id) throws ObjectDoesntExsitException {
        for (Product product : this.inventory.keySet()) {
            if (product.getProduct_id() == product_id)
                return product;
        }
        throw new ObjectDoesntExsitException("Store: Product is not exist - product id: " + product_id);
    }


    // -- Private Methods

    // -- Getters ------------------------------------------------------------------------

    public String getFoundation_date() {
        return foundation_date;
    }

    public StoreReview getStoreReview() {
        return storeReview;
    }

    public Map<Product, Integer> getInventory() {
        return this.inventory;
    }

    public StorePurchaseHistory getPurchase_history() {
        return purchases_history;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getName() {
        return name;
    }


//---------------------------------------------------------------------- Setters - Start ------------------------------------------------------------------------------------


    public void setName(String name) {
        this.name = name;
    }

    public void setInventory(HashMap<Product, Integer> inventory) {
        this.inventory = inventory;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPurchasePolicy(AssignUser user, PurchasePolicy purchasePolicy) throws NoPremssionException {
        check_permission(user, StorePermission.edit_purchase_policy);
        this.purchasePolicy = purchasePolicy;
    }

    public void setDiscountPolicy(AssignUser user, DiscountPolicy discountPolicy) throws NoPremssionException {
        check_permission(user, StorePermission.edit_discount_policy);
        this.discountPolicy = discountPolicy;
    }


    public void check_permission(AssignUser user, StorePermission permission) throws NoPremssionException {
        if (!this.stuffs_and_appointments.containsKey(user))
            throw new NoPremssionException("user is no a store member");
        boolean flag = this.stuffs_and_appointments.get(user).has_permission(permission);
        if (!flag)
            throw new NoPremssionException("User has no permissions!");
    }

    public Map<Integer, Double> get_product_ids_and_total_price(Basket basket) {
        Map<Integer, Double> productsIds_and_totalPrice = new HashMap<>();
        Map<Product, Integer> products_and_quantities = basket.getProducts_and_quantities();
        for (Product p : products_and_quantities.keySet()) {
            int quantity = products_and_quantities.get(p);
            productsIds_and_totalPrice.put(p.getProduct_id(), this.calc_product_price(p, quantity));
        }
        return productsIds_and_totalPrice;
    }

    public AssignUser get_appointer(AssignUser manager) {
        Appointment appointment = this.stuffs_and_appointments.get(manager);
        if (appointment == null)
            throw new IllegalArgumentException("is not a manager");
        return this.stuffs_and_appointments.get(manager).getAppointer();
    }

    //TODO: pass
    public Double calc_product_price(Product product, int quantity) {
        //TODO :discount policy - version 2
        return product.getPrice() * quantity;
    }

    public void send_message_to_the_store_stuff(String message) {
        for (AssignUser stuff_member : this.stuffs_and_appointments.keySet()) {
            stuff_member.add_notification(message);
        }
    }

    //TODO: testing method
    public boolean has_appointment(AssignUser founder) {
        return stuffs_and_appointments.containsKey(founder);
    }

    public void edit_product_quantity(AssignUser assignUser, int product_id, int quantity) throws MarketException {
        Product to_edit = this.getProduct_by_product_id(product_id);
        this.check_permission(assignUser, StorePermission.edit_item_quantity);
        if (quantity < 1)
        {
            throw new WrongPermterException("quantity must be positive number");
        }
        this.inventory.put(to_edit, quantity);
    }

}