package TradingSystem.server.Domain.UserModule;

import TradingSystem.server.DAL.HibernateUtils;
import TradingSystem.server.Domain.Questions.QuestionController;
import TradingSystem.server.Domain.Statistics.Statistic;
import TradingSystem.server.Domain.Statistics.StatisticsManager;
import TradingSystem.server.Domain.StoreModule.Appointment.Appointment;
import TradingSystem.server.Domain.StoreModule.Basket;
import TradingSystem.server.Domain.StoreModule.Product.Product;
import TradingSystem.server.Domain.StoreModule.Purchase.UserPurchase;
import TradingSystem.server.Domain.StoreModule.Purchase.UserPurchaseHistory;
import TradingSystem.server.Domain.StoreModule.Store.Store;
import TradingSystem.server.Domain.StoreModule.StoreController;
import TradingSystem.server.Domain.Utils.Exception.*;
import TradingSystem.server.Domain.Utils.Logger.MarketLogger;
import TradingSystem.server.Domain.Utils.Logger.SystemLogger;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static TradingSystem.server.Service.MarketSystem.test_flag;

//@Entity
public class UserController {
    //    @Transient
//    // ------------------- fields -------------------------------------
//    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
////    @JoinTable(name = "all_users",
////            joinColumns = {@JoinColumn(name = "controller", referencedColumnName = "id")})
//    @MapKeyColumn(name = "user_id") // the key column
    private Map<String, User> users;              // email,user
    //    @Transient
    private Map<Integer, User> onlineUsers;       // id,user
    private AtomicInteger uc_id;
    private AtomicInteger purchaseID;
    //    @Transient
    private Object usersLock;
    private Object online_users_lock;
    //    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private StatisticsManager statisticsManager;
    //    private static UserController instance = null;
//    @Id
//    @GeneratedValue
    private Long id;
    private Set<String> online_emails;

    public void load(boolean rollback_flag) {
        if (!test_flag || rollback_flag){
            this.uc_id = new AtomicInteger(HibernateUtils.get_uc());
            MarketLogger.getInstance().add_log("--------------uc_id-------------");
            MarketLogger.getInstance().add_log("" + uc_id.get());
            this.purchaseID = new AtomicInteger(HibernateUtils.get_max_purchase());
            MarketLogger.getInstance().add_log("--------------purchase_id---------------");
            MarketLogger.getInstance().add_log("" + purchaseID.get());
            try {
                Map<String, User> all_users = HibernateUtils.users();
                this.users = all_users;
            } catch (Exception e) {
//            throw new MarketException("failed to load users from table");
            }
            MarketLogger.getInstance().add_log("--------------all_users---------------");
            MarketLogger.getInstance().add_log(users.toString());
            //TODO: change to read one
//        this.statisticsManager = HibernateUtils.getEntityManager().find(StatisticsManager.class, new Long(1));
            this.statisticsManager = new StatisticsManager();
            for(Map.Entry<Integer,User> en: onlineUsers.entrySet()){
                try {
                    onlineUsers.put(en.getKey(), users.get(en.getValue().user_email()));
                }
                catch (Exception e){

                }
            }
            SystemLogger.getInstance().add_log("user controller load");
        }
        StoreController.get_instance().load(rollback_flag);

    }

    //    @Transient
    public User get_user_for_tests(int id) {
        return find_online_user(id);
    }

    private static class SingletonHolder {
        private static UserController instance = new UserController();
    }

    public static UserController get_instance() {
        return SingletonHolder.instance;
    }

    // ------------------- constructors --------------------------------
    public UserController() {
        this.uc_id = new AtomicInteger(0);
        this.purchaseID = new AtomicInteger(0);
        this.users = new HashMap<>();        //thread safe
        this.onlineUsers = new ConcurrentHashMap<>();  //thread safe
        this.usersLock = new Object();
        this.statisticsManager = new StatisticsManager();
        this.online_emails = new HashSet<>();
        this.online_users_lock = new Object();
        HibernateUtils.persist(statisticsManager);
    }


    // ------------------ methods --------------------------------------

    /**
     * function that connects new guest to the system.
     *
     * @return guest's online id
     */
    public int guest_login() {
        int cur_guest_id = uc_id.getAndIncrement();    // synchronized
        User newUser = new User();
        onlineUsers.put(cur_guest_id, newUser);
        statisticsManager.inc_connect_system_count();
        return cur_guest_id;
    }

    /**
     * non synchronized function
     * function that check if user email is already registered in the system
     *
     * @param email represnts the user email
     * @return true if the email is registered in the system else false
     */
    private boolean isRegistered(String email) {
        return users.containsKey(email);
    }

//    /**
//     * function that checks if current id exists in online
//     * @param id represent the id of specific online member
//     * @return true if the member is online
//     */
//    private boolean isOnline(int id){
//        return onlineUsers.containsKey(id);
//    }

    /**
     * function that register new user to the system.
     * new user gets an empty cart
     *
     * @param email    new email of the user
     * @param pw       new password of the user
     * @param name     the user's name
     * @param lastName the user's last name
     */
    public User register(int ID, String email, String pw, String name, String lastName, String birth_date) throws
            MarketException {
        User user;
        synchronized (usersLock) {
            if (isRegistered(email))
                throw new RegisterException("user email " + email + " already exists in the system");
            user = find_online_user(ID);
            user.register(email, pw, name, lastName, birth_date);
            add_to_online(email);
            users.put(email, user);
            HibernateUtils.persist(user);
//            HibernateUtils.merge(this);
        }
        statisticsManager.inc_register_count();
        return user;
    }

    /**
     * @param ID       the user's id in the system
     * @param email    the user email to log-in
     * @param password the user password
     * @return the status if log-in succeed
     */
    public User login(int ID, String email, String password) throws MarketException {
        if (isRegistered(email)) {
            User cur_user = find_online_user(ID);
            User user = find_reg_user(email);
            if(!cur_user.getIsGuest().get())
                throw new LoginException("cannot log in from logged in user");
//            if (cur_user.test_isLogged())
//                throw new LoginException("cannot log in from logged in user");
            user.login(password); //verifies if the user is logged and password & changes state.
            if(!add_to_online(email))
                throw new LoginException("user already logged in to the system.");
            onlineUsers.put(ID, user);
            statisticsManager.inc_login_count();
            return user;
        } else
            throw new LoginException("User email does not match the password");
    }


    /**
     * @param ID online user's id to logout
     */
    public User logout(int ID) throws MarketException {
        User user = find_online_user(ID);
        user.logout();
        if(!remove_from_online(user.user_email()))
            throw new LoginException("user is not logged in to the system.");
        onlineUsers.put(ID, new User());
        statisticsManager.inc_logout_count();
        return user;
    }

    /**
     * @param user id
     * @return the user's cart
     */
    public Cart view_user_cart(int user) {
        return find_online_user(user).getCart();
    }


    /**
     * function that gets the basket by store ID
     *
     * @param userID  represents the user id in the online map
     * @param storeID represents the store id
     * @return the store basket from the user's cart
     */
    public Basket getBasketByStoreID(int userID, int storeID) {
        User user = find_online_user(userID);
        return user.getBasketByStoreID(storeID);
    }

    /*    *//**
     * function that add basket to the cart
     *
     * @param userID  the online user ID.
     * @param storeID represents the store id
     * @param basket
     *//*
    public void addBasket(int userID, int storeID, Basket basket) {
        User user = find_online_user()(userID);
        user.addBasket(storeID,basket);
    }*/

    /**
     * function that remove basket in case its empty
     *
     * @param loggedUser
     * @param storeID
     * @param storeBasket
     */
    public void removeBasketIfNeeded(int loggedUser, int storeID, Basket storeBasket) {
        User user = find_online_user(loggedUser);
        user.removeBasketIfNeeded(storeID, storeBasket);
    }


    /**
     * function that gets all the baskets from the user's cart
     *
     * @param loggedUser
     * @return cart's baskets
     */
    public Map<Store, Basket> getBaskets(int loggedUser) {
        User user = find_online_user(loggedUser);
        return user.view_baskets();
    }


    /**
     * function that returns the logged user cart
     *
     * @param loggedUser represents the user ID
     * @return the user's cart
     */
    public Cart getCart(int loggedUser) {
        User user = find_online_user(loggedUser);
        return user.getCart();
    }

    /**
     * function that makes a new purchase and add it to the history & clears the cart
     *
     * @param loggedUser
     */
    public UserPurchase buyCart(int loggedUser) throws MarketException {
        User user = find_online_user(loggedUser);
        UserPurchase userPurchase = user.buyCart(purchaseID.getAndIncrement());
        statisticsManager.inc_buy_cart_count();
        return userPurchase;
    }


    public void check_if_user_buy_from_this_store(int loggedUser, int store_id) throws MarketException {
        User user = find_online_user(loggedUser);
        user.check_if_user_buy_from_this_store(store_id);
    }

    public void check_if_user_buy_this_product(int loggedUser, int productID, int storeID) throws MarketException {
        User user = find_online_user(loggedUser);
        user.check_if_user_buy_this_product(storeID, productID);
    }

    public UserPurchaseHistory view_user_purchase_history(int loggedUser) throws MarketException { //admin
        User user = find_online_user(loggedUser);
        return user.view_user_purchase_history();
    }

    public String get_user_name(int loggedUser) throws MarketException {
        User user = find_online_user(loggedUser);
        return user.user_name();
    }

    public String get_user_last_name(int loggedUser) throws MarketException {
        User user = find_online_user(loggedUser);
        return user.user_last_name();
    }

    public String get_email(int loggedUser) throws MarketException {
        User user = find_online_user(loggedUser);
        return user.user_email();
    }

    public void check_admin_permission(int loggedUser) throws MarketException {
        User user = find_online_user(loggedUser);
        user.check_admin_permission();
    }

    public UserPurchaseHistory admin_view_user_purchase_history(int loggedUser, String email) throws MarketException { //admin
        check_admin_permission(loggedUser);
        if (!isRegistered(email))
            throw new NoUserRegisterdException("user " + email + "is not registered to the system.");
        User user = find_reg_user(email);
        return user.view_user_purchase_history();
    }

    private void remove_email_from_online_users(String email) { //if exists
        for (Map.Entry<Integer, User> entry : onlineUsers.entrySet()) {
            try {
                if (entry.getValue().user_email().equals(email)) {
                    onlineUsers.remove(entry.getKey());
                    return;
                }
            } catch (Exception e) {
                //TODO why the catch is in here
            }
        }
    }

    public void remove_user(int ID, String email) throws MarketException {
        check_admin_permission(ID);
        if (!isRegistered(email))
            throw new NoUserRegisterdException("failed to remove due to the reason " + email + " is not registered in the system.");


        Map<Store, Appointment> founder_map = this.get_user_by_email(email).state_if_assigned().getFounder();
        for (Store store : founder_map.keySet()){
            store.close_store_permanently();
        }

        if (email.equals(get_email(ID)))
            throw new AdminException("failed to remove admin from the system.");
        remove_email_from_online_users(email);
        synchronized (usersLock) {
            users.remove(email);
        }
    }

    public String unregister(int ID, String password) throws MarketException {
        String email = get_email(ID);
        User user = find_online_user(ID);
        for (Store store : user.state_if_assigned().getFounder().keySet()){
            store.close_store_temporarily(user.state_if_assigned());
        }

        user.unregister(password);
        synchronized (usersLock) {
            users.remove(email);
        }
        onlineUsers.put(ID, new User());
        return email;
    }

    public String edit_name(int loggedUser, String new_name) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_name(new_name);
        return get_email(loggedUser);
    }

    public String edit_password(int loggedUser, String old_password, String password) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_password(old_password, password);
        return get_email(loggedUser);
    }

    public String edit_last_name(int loggedUser, String new_last_name) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_last_name(new_last_name);
        return get_email(loggedUser);
    }

    public Statistic get_statistics(int logged_user) throws MarketException {
        check_admin_permission(logged_user);
        return statisticsManager.get_system_statistics(users, onlineUsers);
    }


    public void send_question_to_admin(int loggedUser, String question) throws NoUserRegisterdException {
        User user = find_online_user(loggedUser);
        AssignUser assignUser = user.state_if_assigned();
        QuestionController.getInstance().add_user_question(question, assignUser);
        List<Admin> adminsList = this.get_admins();
        for (Admin admin : adminsList) {
            admin.add_notification("user : " + assignUser.get_user_email() + " send new question");
        }
    }

    public void answer_user_question(int loggedUser, int question_id, String answer) throws MarketException {
        check_admin_permission(loggedUser);
        QuestionController.getInstance().answer_user_question(question_id, answer);
    }

    public List<String> view_users_questions(int loggedUser) throws MarketException {
        check_admin_permission(loggedUser);
        return QuestionController.getInstance().view_users_to_admin_questions();
    }

    public User add_admin(String email, String pw, String name, String lastName) throws MarketException {
        User admin = new User();
        admin.set_admin(email, pw, name, lastName);
        synchronized (usersLock) {
            users.put(email, admin);
        }
        HibernateUtils.persist(admin);
        return admin;
    }

    public String get_user_security_question(int loggedUser) throws MarketException {
        User user = find_online_user(loggedUser);
        return user.user_security_question();
    }

    public String edit_name_premium(int loggedUser, String new_name, String answer) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_name_premium(new_name, answer);
        return get_email(loggedUser);
    }

    public String edit_last_name_premium(int loggedUser, String new_last_name, String answer) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_last_name_premium(new_last_name, answer);
        return get_email(loggedUser);
    }

    public String edit_password_premium(int loggedUser, String old_password, String new_password, String answer) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_password_premium(old_password, new_password, answer);
        return get_email(loggedUser);
    }

    public String improve_security(int loggedUser, String password, String question, String answer) throws MarketException {
        User user = find_online_user(loggedUser);
        user.improve_security(password, question, answer);
        return get_email(loggedUser);
    }

    //TODO: new functions
    public void remove_product_from_cart(int loggedUser, Store store, Product p) throws MarketException {
        User user = find_online_user(loggedUser);
        user.remove_product_from_cart(store, p);
    }


    public void add_product_to_cart(int loggedUser, Store store, Product p, int quantity) throws MarketException {
        User user = find_online_user(loggedUser);
        user.add_product_to_cart(store, p, quantity);
    }

    public void edit_product_quantity_in_cart(int loggedUser, Store store, Product p, int quantity) throws MarketException {
        User user = find_online_user(loggedUser);
        user.edit_product_quantity_in_cart(store, p, quantity);
    }

    public User get_user(int loggedUser) {
        return find_online_user(loggedUser);
    }

    public User get_user_by_email(String email) throws MarketException {
        if (!users.containsKey(email))
            throw new ObjectDoesntExsitException("user does not exists in the system.");
        return find_reg_user(email);
    }

    public List<Admin> get_admins() {
        List<Admin> admins = new ArrayList<>();
        for (User user : users.values()) {
            Admin user_state = null;
            if (user_state != null)
                admins.add(user_state);
        }
        return admins;
    }

    // TODO: added functions for testing :
    public boolean contains_user_email(String email) {
        return this.users.containsKey(email);
    }

    public void clear() {
        this.uc_id = new AtomicInteger(0);
        this.purchaseID = new AtomicInteger(0);
        this.users = new HashMap<>();        //thread safe
        this.onlineUsers = new ConcurrentHashMap<>();  //thread safe
        this.usersLock = new Object();
        this.statisticsManager = new StatisticsManager();
        this.usersLock = new Object();
        this.online_emails = new HashSet<>();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    private User find_online_user(int id) {
        User u = onlineUsers.get(id);
//        u = u.merge();
//        HibernateUtils.commit();
//        HibernateUtils.beginTransaction();
        onlineUsers.put(id, u);
        return u;
    }

    private User find_reg_user(String email) {
        User u = users.get(email);
//        u = u.merge();
//        HibernateUtils.commit();
//        HibernateUtils.beginTransaction();
        users.put(email, u);
        return u;
    }

    public void remove_product_from_all_carts(Product product, Store store) throws MarketException {
        for (User u : users.values()) {
            try {
                u.remove_product_from_cart(store, product);
            } catch (Exception e) {
                continue;
            }
        }
        for (User u : onlineUsers.values()) {
            try {
                u.remove_product_from_cart(store, product);
            } catch (Exception e) {
                continue;
            }
        }
    }

    private boolean add_to_online(String email){
        boolean res = false;
        synchronized (online_users_lock){
            if(!online_emails.contains(email)){
                online_emails.add(email);
                res = true;
            }
        }
        return res;
    }

    private boolean remove_from_online(String email){
        boolean res = false;
        synchronized (online_users_lock){
            if(online_emails.contains(email)){
                online_emails.remove(email);
                res = true;
            }
        }
        return res;
    }
}
