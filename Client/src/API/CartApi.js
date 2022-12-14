// import axios from "axios";
import { Cart } from "../ServiceObjects/Cart";
import { CONNECTION_ERROR, CATCH, VIEW_USER_CART, BUY_CART } from "./ApiPaths";
import { Response } from "./Response";
import { UserPurchase } from "../ServiceObjects/UserPurchase"

// const instance = axios.create(
//     {withCredentials : true}
// );
const response_obj = new Response("", "");
const instance = require('axios');

export class CartApi {

    view_user_cart() {
        return instance.get(VIEW_USER_CART,{params:{
            session_id:JSON.parse(sessionStorage.getItem("session_id"))
        }})
            .then(res => {
                let response = res.data;
                console.log(response)
                let cart = new Cart(response.value);
                return Response.create(cart, response.was_exception, response.message);
            })
            .catch(function (res) {
                if (res.message == "Network Error") {
                    console.log(res.message)
                    return Response.create(CATCH, true, CONNECTION_ERROR)

                }
                else {
                    console.log(res.message)
                    return Response.create(CATCH, true, res.message)
                }
            });
    }

    buy_cart(payment_info, supply_info) {
        console.log("in buy cart - cart api");
        return instance.get(BUY_CART,
            {
                params:{ paymentInfo: payment_info,
                    supplyInfo: supply_info,session_id:JSON.parse(sessionStorage.getItem("session_id"))}

            })
            .then(res => {
                const user_purchase = new UserPurchase(res.data.value)
                return Response.create(user_purchase, res.data.was_exception, res.data.message);
            })
            .catch(function (res) {
                if (res.message == "Network Error") {
                    console.log(res.message)
                    return Response.create(CATCH, true, CONNECTION_ERROR)

                }
                else {
                    console.log(res.message)
                    return Response.create(CATCH, true, res.message)
                }
            });
    }



}