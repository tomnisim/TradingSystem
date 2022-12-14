// import axios from "axios";
import {CONNECTION_ERROR, CATCH, FIND_STORE_INFORMATION, OPEN_STORE, RATE_STORE, SEND_QUESTION_TO_STORE,
     ADD_PRODUCT_TO_STORE, DELETE_PRODUCT_FROM_CART, SET_STORE_PURCHASE_POLICY,
      SET_STORE_DISCOUNT_POLICY, SET_STORE_PURCHASE_RULES, ADD_OWNER, DELETE_OWNER, 
      ADD_MANAGER, DELETE_MANAGER, CLOSE_STORE_TEMPORARILY, OPEN_CLOSE_STORE,
      VIEW_STORE_MANAGEMENT_INFORMATION, MANAGER_ANSWER_QUESTION, VIEW_STORE_PURCHASES_HISTORY, 
      MANAGER_VIEW_STORE_QUESTIONS, EDIT_MANAGER_PERMISSIONS, 
      GET_PRODUCTS_BY_STORE_ID,GET_ALL_STORES, DELETE_PRODUCT_FROM_STORE,
      GET_PERMISSIONS, ADD_BID, MANAGER_ANSWER_BID, VIEW_BIDS_STATUS,VIEW_APPOINTMENTS_STATUS,MANAGER_ANSWER_APPOINTMENT, GET_ALL_CATEGORIES} from "./ApiPaths";
import { Response } from "./Response";
import { Store } from "../ServiceObjects/Store";
import { Product } from "../ServiceObjects/Product";
// const instance = axios.create(
//     {withCredentials : true}
// );
var qs = require('qs');
const instance = require('axios');
const SESSION_ID=JSON.parse(sessionStorage.getItem("session_id"));

export class StoreApi {
    find_store_information(store_id) {
        console.log("in find store information -> dtore id = "+store_id);
        return instance.get(FIND_STORE_INFORMATION,
            {
                params:{ store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                let response = res.data;
                let store_info = new Store(response.value);
                return Response.create(store_info, false, response.message);
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
    open_store(store_name) {
        return instance.get(OPEN_STORE,
            {
                params:{store_name : store_name,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                return new Response(res.data)
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
    
    rate_store(store_id, rate) {
        return instance.get(RATE_STORE,
            {
                params:{store_id: store_id,
                    rate : rate,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                return new Response(res.data)
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

    send_question_to_store(store_id, question) {
        return instance.get(SEND_QUESTION_TO_STORE,
            {
                params:{store_id: store_id,
                    question : question,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                return new Response(res.data)
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
    
    add_product_to_store(store_id, quantity,name, price, category, key_words) {
        return instance.get(ADD_PRODUCT_TO_STORE,
            {
                params:{store_id : store_id,
                    quantity : quantity,
                    name : name,
                    price : price,
                    category : category,
                    key_words : key_words,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                return new Response(res.data)
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
    delete_product_from_store(product_id, store_id) {
        return instance.get(DELETE_PRODUCT_FROM_STORE,
            {
                params:{product_id : product_id,
                    store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
                
            })
            .then(res => {
                return new Response(res.data)
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

    set_store_purchase_policy(store_id, policy) {
        return instance.get(SET_STORE_PURCHASE_POLICY,
            {
                params:{store_id : store_id,
                    policy : policy,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
                
            })
            .then(res => {
                return new Response(res.data)
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

    set_store_discount_policy(store_id, policy) {
        return instance.get(SET_STORE_DISCOUNT_POLICY,
            {
                params:{store_id : store_id,
                    policy : policy,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
                
            })
            .then(res => {
                return new Response(res.data)
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
    set_store_purchase_rules(store_id, rule) {
        return instance.get(SET_STORE_PURCHASE_RULES,
            {
                params:{store_id : store_id,
                    rule : rule,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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

    add_owner(user_email_to_appoint, store_id)  {
        return instance.get(ADD_OWNER,
            {
                params:{user_email_to_appoint : user_email_to_appoint,
                    store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
   
    delete_owner(user_email_to_delete_appointment, store_id)  {
        return instance.get(DELETE_OWNER,
            {
                params:{user_email_to_delete_appointment : user_email_to_delete_appointment,
                    store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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

       
    add_manager(user_email_to_appoint, store_id)  {
        return instance.get(ADD_MANAGER,
            {
                params:{user_email_to_appoint : user_email_to_appoint,
                    store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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


    delete_manager(user_email_to_delete_appointment, store_id)  {
            return instance.get(DELETE_MANAGER,
                {
                    params:{user_email_to_delete_appointment : user_email_to_delete_appointment,
                        store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                })
                .then(res => {
                    return new Response(res.data)
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
    close_store_temporarily(store_id){
        return instance.get(CLOSE_STORE_TEMPORARILY,
            {
                params:{store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    open_close_store(store_id) {
        return instance.get(OPEN_CLOSE_STORE,
            {
                params:{store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    view_store_management_information(store_id) {
        return instance.get(VIEW_STORE_MANAGEMENT_INFORMATION,
            {
                params:{store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data) //value is string answer
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
    manager_view_store_questions(store_id){ // value is list of strings
        return instance.get(MANAGER_VIEW_STORE_QUESTIONS,
            {
                params:{store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    manager_answer_question(store_id, question_id, answer){
        return instance.get(MANAGER_ANSWER_QUESTION,
            {
                params:{store_id : store_id,
                    question_id : question_id,
                    managerAnswer : answer,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    view_store_purchases_history(store_id){ // value is string of the purchases history
        return instance.get(VIEW_STORE_PURCHASES_HISTORY,
            {
                params:{store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    edit_manager_permissions(manager_email, store_id, permissions){
        console.log("permissions");
        console.log(permissions);
        console.log("permissions");
        
        
        return instance.get(EDIT_MANAGER_PERMISSIONS,
            {
                params:{manager_email : manager_email,
                    store_id : store_id,
                    permissions : permissions,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    get_products_by_store_id(store_id){
        return instance.get(GET_PRODUCTS_BY_STORE_ID,
            {
                params:{store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                let response = res.data;
                //traverse the products and create product for each element on the list
                //create response with the list of products
                const arr = [];
                res.data.value.map(p => arr.push(new Product(p)));
                return Response.create(arr, res.data.wasException, res.data.message);
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
    get_all_stores(){
        return instance.get(GET_ALL_STORES,
            {
                params:{session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                let response = res.data;
                //traverse the products and create product for each element on the list
                //create response with the list of products
                const arr = [];
                response.value.map(s => arr.push(new Store(s)));
                return Response.create(arr, response.wasException, response.message);
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
    get_permissions(manager_email, store_id ){
        return instance.get(GET_PERMISSIONS,
            {
                params:{
                    manager_email : manager_email,
                    store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                return new Response(res.data)
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
    add_bid(storeID, productID, quantity, offer_price) {
        return instance.get(ADD_BID,
            {
                params: {
                    storeID: storeID,
                    productID: productID,
                    quantity: quantity,
                    offer_price: offer_price,session_id:JSON.parse(sessionStorage.getItem("session_id"))
                }
            })
            .then(res => {
                return new Response(res.data)
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
    manager_answer_bid(storeID, bidID, manager_answer, negotiation_price ){
        return instance.get(MANAGER_ANSWER_BID,
            {
                params:{
                    storeID : storeID,
                    bidID : bidID,
                    manager_answer : manager_answer,
                    negotiation_price : negotiation_price,session_id:JSON.parse(sessionStorage.getItem("session_id"))
                    }
            })
            .then(res => {
                return new Response(res.data)
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

    view_appointments_status(storeID ){
        return instance.get(VIEW_APPOINTMENTS_STATUS,
            {
                params:{
                    storeID : storeID,session_id:JSON.parse(sessionStorage.getItem("session_id"))
                 }
            })
            .then(res => {
                return new Response(res.data)
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

    manager_answer_appointment(storeID, manager_answer, candidate_email){
        return instance.get(MANAGER_ANSWER_APPOINTMENT,
            {
                params:{
                    storeID : storeID,
                    manager_answer : manager_answer,
                    candidate_email : candidate_email,session_id:JSON.parse(sessionStorage.getItem("session_id"))
                    }
            })
            .then(res => {
                return new Response(res.data)
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

    view_bids_status(storeID) {
        return instance.get(VIEW_BIDS_STATUS,
            {
                params: {
                    storeID : storeID,session_id:JSON.parse(sessionStorage.getItem("session_id"))
                }
            })
            .then(res => {
                return new Response(res.data)
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
    get_all_categories(store_id) {
        return instance.get(GET_ALL_CATEGORIES,
            {
                params: {
                    store_id : store_id,session_id:JSON.parse(sessionStorage.getItem("session_id"))
                }
            })
            .then(res => {
                return new Response(res.data)
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

