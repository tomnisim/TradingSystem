// import axios from "axios";
import {CONNECTION_ERROR, CATCH, EMPLOYEE_BASE_REST_API_URL,  FIND_PRODUCT_INFORMATION, FIND_PRODUCTS_BY_NAME, FIND_PRODUCTS_BY_CATEGORY,FIND_PRODUCTS_BY_KEYWORDS 
    ,ADD_PRODUCT_TO_CART, EDIT_PRODUCT_QUANTITY_IN_CART,REMOVE_PRODUCT_FROM_CART, 
    ADD_PRODUCT_REVIEW, RATE_PRODUCT, EDIT_PRODUCT_NAME, EDIT_PRODUCT_PRICE, 
    EDIT_PRODUCT_CATEGORY, EDIT_PRODUCT_KEY_WORDS, LOGIN_PATH, EDIT_PRODUCT_QUANTITY} from "./ApiPaths";
import { Response } from "./Response";
import { Product } from "../ServiceObjects/Product";
// const instance = axios.create(
//     {withCredentials : true}
// );
const instance = require('axios');




export class ProductApi {
   
    find_product_information(product_id, store_id) {
        return instance.get(FIND_PRODUCT_INFORMATION, 
            {
                params:{product_id: product_id,
                    store_id: store_id, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
            })
            .then(res => {
                let response = res.data;
                // let product_info = new Product(response.value);
                // return Response.create(product_info, response.was_exception, response.message);
                return new Response(response);
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
    find_products_by_name(product_name) {
        console.log("product name = "+product_name+"\n\n\n");
        return instance.get(FIND_PRODUCTS_BY_NAME,
            {
                params:{product_name: product_name, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                let response = res.data;
                //traverse the products and create product for each element on the list
                //create response with the list of products
                const arr = [];
                res.data.value.map(p => arr.push(new Product(p)));
                return Response.create(arr, res.data.was_exception, res.data.message);
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

    find_products_by_category(product_category) {
        console.log("product category = " + product_category + "\n\n\n");
        return instance.get(FIND_PRODUCTS_BY_CATEGORY,
            {
                params:{product_category: product_category, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                //traverse the products and create product for each element on the list
                //create response with the list of products
                const arr = [];
                res.data.value.map(p => arr.push(new Product(p)));
                return Response.create(arr, res.data.was_exception, res.data.message);
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
    find_products_by_keywords(product_keywords) {
        console.log("product keywords = " + product_keywords + "\n\n\n");
        return instance.get(FIND_PRODUCTS_BY_KEYWORDS,
            {
                params:{product_keywords: product_keywords, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
            })
            .then(res => {
                //traverse the products and create product for each element on the list
                //create response with the list of products
                const arr = [];
                res.data.value.map(p => arr.push(new Product(p)));
                return Response.create(arr, res.data.was_exception, res.data.message);
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

    add_product_to_cart(store_id, product_id, quantity) {
        console.log("store id: " + store_id)
        console.log("product id:" + product_id)
        return instance.get(ADD_PRODUCT_TO_CART,
            {
                params:{store_id: store_id,
                    product_id: product_id,
                    quantity: quantity, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
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

    edit_product_quantity_in_cart(store_id, product_id, quantity) {
        return instance.get(EDIT_PRODUCT_QUANTITY_IN_CART,
            {
                params:{store_id: store_id,
                    product_id: product_id,
                    quantity: quantity, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
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

    remove_product_from_cart(store_id, product_id) {
        return instance.get(REMOVE_PRODUCT_FROM_CART,
            {
                params:{ store_id: store_id,
                    product_id: product_id, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
               
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
    

    add_product_review(product_id, store_id, review) {
        return instance.get(ADD_PRODUCT_REVIEW,
            {
                params:{  product_id: product_id,
                    store_id: store_id,
                    review : review, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
               
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

    rate_product(product_id, store_id, rate) {
        return instance.get(RATE_PRODUCT,
            {
                params:{ product_id: product_id,
                    store_id: store_id,
                    rate : rate, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
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

    edit_product_name(product_id, store_id, name) {
        return instance.get(EDIT_PRODUCT_NAME,
            {
                params:{product_id: product_id,
                    store_id: store_id,
                    name : name ,session_id:JSON.parse(sessionStorage.getItem("session_id")),}
                
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

    edit_product_price(product_id, store_id, price) {
        return instance.get(EDIT_PRODUCT_PRICE,
            {
                params:{product_id: product_id,
                    store_id: store_id,
                    price : price, session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
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
    edit_product_category(product_id, store_id, category) {
        return instance.get(EDIT_PRODUCT_CATEGORY,
            {
                params:{product_id: product_id,
                    store_id: store_id,
                    category : category,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
                
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
    edit_product_key_words(product_id, store_id, key_words) {
        return instance.get(EDIT_PRODUCT_KEY_WORDS,
            {
                params:{ product_id: product_id,
                    store_id: store_id,
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
    edit_product_quantity(product_id, store_id, quantity) {
        return instance.get(EDIT_PRODUCT_QUANTITY,
            {
                params:{ product_id: product_id,
                    store_id: store_id,
                    quantity : quantity,session_id:JSON.parse(sessionStorage.getItem("session_id"))}
               
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