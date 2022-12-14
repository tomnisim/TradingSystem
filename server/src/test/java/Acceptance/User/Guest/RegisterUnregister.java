package Acceptance.User.Guest;

import TradingSystem.server.Domain.ExternalSystems.*;
import TradingSystem.server.Domain.Facade.MarketFacade;
import TradingSystem.server.Domain.StoreModule.Store.StoreInformation;
import TradingSystem.server.Domain.StoreModule.StoreController;
import TradingSystem.server.Domain.UserModule.UserController;
import TradingSystem.server.Domain.Utils.Response;
import TradingSystem.server.Service.MarketSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import static TradingSystem.server.Service.MarketSystem.tests_config_file_path;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RegisterUnregister {
    private MarketFacade facade1;
    private MarketFacade facade2;
    private MarketFacade facade3;
    private MarketFacade facade4;
    private UserController uc;
    private PaymentAdapter pa;
    private SupplyAdapter sa;
    private String email;
    private String password;
    private String birth_date;
    private final int num_of_threads = 100;
    private String user_premium_security_email;
    private String user_password;
    private String user_founder_email;
    private String user_buyer_email;
    private String user_regular_email_1;
    private String user_regular_email_2;
    private String user_admin_email;
    private SupplyInfo supplyInfo = new SupplyInfo("1","2","3","4","5");
    private PaymentInfo payment_info = new PaymentInfo("123","456","789","245","123","455");
    private PaymentAdapter paymentAdapter;
    private SupplyAdapter supplyAdapter;
    private String prodname = "";

    public RegisterUnregister(){
        try{
            MarketSystem marketSystem = new MarketSystem(tests_config_file_path, "");
            this.paymentAdapter = marketSystem.getPayment_adapter();
            this.supplyAdapter = marketSystem.getSupply_adapter();

            this.facade1 = new MarketFacade(paymentAdapter, supplyAdapter);
            this.facade2 = new MarketFacade(paymentAdapter, supplyAdapter);
            this.facade3 = new MarketFacade(paymentAdapter, supplyAdapter);
            this.facade4 = new MarketFacade(paymentAdapter, supplyAdapter);

            uc = UserController.get_instance();
            pa = new PaymentAdapterImpl();
            sa = new SupplyAdapterImpl();

            // users information
            user_buyer_email = "buyer@email.com";
            user_founder_email = "founder@email.com";
            user_regular_email_1 = "regular1@email.com";
            user_regular_email_2 = "regular2@email.com";
            user_admin_email = "admin@gmail.com";
            user_premium_security_email = "premiumSecurity@email.com";
            user_password = "pass3Chec";
            birth_date =  LocalDate.now().minusYears(30).toString();
            String first_name = "name";
            String last_name = "last";
            email = "somthing@gmail.com";
            password = "aA12345";

            facade1.register(user_founder_email, user_password, first_name, last_name,birth_date);
            facade2.register(user_buyer_email, user_password, first_name, last_name,birth_date);
            facade3.register(user_regular_email_1, user_password, first_name, last_name,birth_date);
            facade4.register(user_regular_email_2, user_password, first_name, last_name,birth_date);

            int id = open_store_get_id("Checker Store") ;
            add_prod_make_purchase_get_id(id);

            facade1.logout();
            facade2.logout();
            facade3.logout();
            facade4.logout();

            // register user with premium security
            facade1.register(user_premium_security_email, user_password, first_name, last_name,birth_date);
            facade1.improve_security(user_password, "What was your mother's maiden name?", "Sasson");
            facade1.logout();

            // add admin to  the system
            uc.add_admin(user_admin_email, user_password, "Barak", "Bahar");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private int open_store_get_id(String name){
        facade1.open_store(name);
        return num_of_stores();
    }
    private int num_of_stores(){
        Response res = facade1.get_all_stores();
        int stores_count = 0;
        if(res.getValue().getClass() == (new ArrayList<StoreInformation>()).getClass()) {
            stores_count = ((ArrayList<StoreInformation>) res.getValue()).size();
        }
        return stores_count;
    }
    private int add_prod_make_purchase_get_id(int sore_id){
        ArrayList<String> arraylist = new ArrayList<>();
        arraylist.add("\n\ncheck_check\n\n");
        prodname += "l";
        int prod_id = StoreController.get_instance().getProduct_ids_counter();
        facade1.add_product_to_store(sore_id, 100, prodname, 10.0, "checker", new ArrayList<>());

        facade2.add_product_to_cart(sore_id, prod_id, 1);
        facade2.buy_cart(payment_info, supplyInfo);

        return prod_id;
    }
    private boolean check_was_exception(Response response) {
        return response.WasException();
    }
    private String make_assert_exception_message(String test, String test_case, boolean suppose_to_be_exception){
        String test_part = "Test: " + test + "\n";
        String case_part = "In test case: " + test_case + " ";
        if(suppose_to_be_exception)
            case_part = "No exception thrown " + case_part;
        else
            case_part = "Exception thrown " + case_part;

        return test_part + case_part;
    }

    //------------------------------- User registration --------------------------------------------------------------------------

    static Stream<Arguments> user_info_provider1() {
        return Stream.of(
                arguments("check1@email.com", "pass3Chec", "name", "last"),
                arguments("check2@email.com", "pass1Chec", "name", "last"),
                arguments("check3@email.com", "Ch3ckPsw0rd", "checker", "checkcheck")
        );
    }

    /**
     * Cases checked:
     * 1. regular register
     * 2. register with registered user from different facade
     * 3. register with registered user from same facade
     * 4. register with registered user from same facade while logged in
     */
    @ParameterizedTest
    @MethodSource("user_info_provider1")
    void register(String email, String pw, String name, String lastName) {
        Response res;
        boolean suppose_to_throw = true;
        String test_name = "register";
        String message;
        Response<String> user_email_res;

        // case 1
        message = make_assert_exception_message(test_name, "regular register", !suppose_to_throw);
        res = facade1.register(email, pw, name, lastName, birth_date);
        boolean was_exception = check_was_exception(res); // regular register
        assertFalse(was_exception, message);
        user_email_res = facade1.get_user_email();
        assertEquals(user_email_res.getValue(),email,"case 1 - failed to add user to system , got different user"); // todo

        //case 2
        message = make_assert_exception_message(test_name, "register with registered user from different facade", suppose_to_throw);
        was_exception = check_was_exception(facade2.register(email, pw, name, lastName, birth_date)); // register with registered user from different facade
        assertTrue(was_exception, message);
        facade1.logout();

        //case 3
        message = make_assert_exception_message(test_name, "register with registered user from same facade", suppose_to_throw);
        was_exception = check_was_exception(facade1.register("check1@email.com", user_password, "name", "last", birth_date)); // register with registered user from same facade
        assertTrue(was_exception, message);

        //case 4
        message = make_assert_exception_message(test_name, "register with registered user from same facade while logged in", suppose_to_throw);
        facade1.register("check12@email.com", "pass123Chec", "name", "last", birth_date);
        was_exception = check_was_exception(facade1.register("check12@email.com", "pass123Chec", "name", "last", birth_date)); // register with registered user from same facade while logged in
        assertTrue(was_exception, message);

        facade1.logout();
    }




    /*
     * Cases checked:
     * 1. unregister guest user
     * 2. unregister assigned user
     * 3. unregister guest user after assigned user unregistered
     */
    @Test
    void unregister() {
        boolean result;
        boolean suppose_to_throw = true;
        String test_name = "unregister";
        String message;

        message = make_assert_exception_message(test_name, "unregister guest user", suppose_to_throw);
        result = check_was_exception(facade1.unregister(user_password)); // unregister guest user
        assertTrue(result, message);

        facade1.login(user_regular_email_1, user_password);

        message = make_assert_exception_message(test_name, "unregister assigned user", !suppose_to_throw);
        result = check_was_exception(facade1.unregister(user_password)); // unregister assigned user
        assertFalse(result, message);

        message = make_assert_exception_message(test_name, "unregister guest user after assigned user unregistered", suppose_to_throw);
        result = check_was_exception(facade1.unregister(user_password)); // unregister guest user after assigned user unregistered
        assertTrue(result, message);

        facade1.register(user_regular_email_1, user_password, "name", "last", birth_date);
        facade1.logout();
    }
}