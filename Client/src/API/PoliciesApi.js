// import axios from "axios";
import {CONNECTION_ERROR, CATCH, ADD_SIMPLE_CATEGORY_DISCOUNT, ADD_SIMPLE_PRODUCT_DISCOUNT, ADD_SIMPLE_STORE_DISCOUNT, 
    ADD_COMPLEX_DISCOUNT, ADD_COMPLEX_AND_DISCOUNT, ADD_COMPLEX_OR_DISCOUNT, 
    ADD_COMPLEX_MAX_DISCOUNT, ADD_COMPLEX_PLUS_DISCOUNT, 
    ADD_COMPLEX_XOR_DISCOUNT, SEND_PREDDICTS, GET_DISCOUNT_POLICY,
     ADD_SIMPLE_PURCHASE, ADD_AND_SIMPLE_PURCHASE, 
     ADD_OR_SIMPLE_PURCHASE, ADD_PREDICT, GET_PURCHASE_POLICY, 
     REMOVEֹֹֹ_DISCOUNTֹ_RULE, REMOVE_PURCHASE_RULE, REMOVE_PREDICT
    } from "./ApiPaths";
import { Response } from "./Response";
import { Store } from "../ServiceObjects/Store";
import { Product } from "../ServiceObjects/Product";
// const instance = axios.create(
//     {withCredentials : true}
// );
var qs = require('qs');
const instance = require('axios');


export class PoliciesApi {

    //---------------------------------Getters-------------------------------------       

    add_predict(store_id,catgorey,product_id,above,equql,num,price,quantity,age,time,year,month,day,name){
        return instance.get(ADD_PREDICT,
            {
                params:{
                    store_id : store_id,
                    catgorey : catgorey,
                    product_id : product_id,
                    above : above,
                    equql : equql,
                    num : num,
                    price : price,
                    quantity : quantity,
                    age : age,
                    time : time,
                    year : year,
                    month : month,
                    day : day,
                    name : name,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

    get_purchase_policy(store_id) {
        return instance.get(GET_PURCHASE_POLICY,
            {
                params:{
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
    send_predicts(store_id) {
        return instance.get(SEND_PREDDICTS,
            {
                params:{
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
    get_discount_policy(store_id) {
        return instance.get(GET_DISCOUNT_POLICY,
            {
                params:{
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




    //---------------------------------Discount Rules-------------------------------------

    add_complex_discount_rule(store_id, nameOfPredict, nameOfComponent, nameOfRule) {
        return instance.get(ADD_COMPLEX_DISCOUNT,
            {
                params:{
                    store_id : store_id,
                    nameOfPredict : nameOfPredict,
                    nameOfComponent : nameOfComponent,
                    nameOfRule : nameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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
    add_simple_categorey_discount_rule(store_id, nameOfCategory, percent, nameOfRule) {
        return instance.get(ADD_SIMPLE_CATEGORY_DISCOUNT,
            {
                params:{
                    store_id : store_id,
                    nameOfCategory : nameOfCategory,
                    percent : percent,
                    nameOfRule : nameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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
    add_simple_product_discount_rule(store_id, id, percent, nameOfrule) {
        return instance.get(ADD_SIMPLE_PRODUCT_DISCOUNT,
            {
                params: {
                    store_id: store_id,
                    id: id,
                    percent: percent,
                    nameOfrule: nameOfrule, session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

    add_simple_store_discount_rule(store_id, percent, nameOfRule){
    return instance.get(ADD_SIMPLE_STORE_DISCOUNT,
        {
            params:{
                store_id : store_id,
                percent : percent,
                nameOfRule : nameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

    
    add_and_discount_rule(left, right, store_id,  NameOfRule ){
    return instance.get(ADD_COMPLEX_AND_DISCOUNT,
        {
            params:{
                left : left,
                right : right,
                store_id : store_id,
                NameOfRule : NameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

    add_or_discount_rule(left, right, store_id,  NameOfRule ){
    return instance.get(ADD_COMPLEX_OR_DISCOUNT,
        {
            params:{
                left : left,
                right : right,
                store_id : store_id,
                NameOfRule : NameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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


    add_max_discount_rule(left, right, store_id,  NameOfRule ){
    return instance.get(ADD_COMPLEX_MAX_DISCOUNT,
        {
            params:{
                left : left,
                right : right,
                store_id : store_id,
                NameOfRule : NameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

    add_plus_discount_rule(left, right, store_id,  NameOfRule ){
    return instance.get(ADD_COMPLEX_PLUS_DISCOUNT,
        {
            params:{
                left : left,
                right : right,
                store_id : store_id,
                NameOfRule : NameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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


    add_xor_discount_rule(left, right, store_id, NameOfRule) {
        return instance.get(ADD_COMPLEX_XOR_DISCOUNT,
            {
                params:{
                    left : left,
                    right : right,
                    store_id : store_id,
                    NameOfRule : NameOfRule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

      


    //---------------------------------Purchase rules-------------------------------------

    add_simple_purchase_rule(PredictName,  NameOfRule,  store_id){
    return instance.get(ADD_SIMPLE_PURCHASE,
        {
            params:{
                PredictName : PredictName,
                NameOfRule : NameOfRule,
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

    add_and_purchase_rule(left, right, store_id, NameOfrule) {
        return instance.get(ADD_AND_SIMPLE_PURCHASE,
            {
                params:{
                    left : left,
                    right : right,
                    store_id : store_id,
                    NameOfrule : NameOfrule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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
    add_or_purchase_rule( left,  right,  store_id,  NameOfrule){
    return instance.get(ADD_OR_SIMPLE_PURCHASE,
        {
            params:{
                left : left,
                right : right,
                store_id : store_id,
                NameOfrule : NameOfrule,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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
    


        //Remove
    remove_predict(store_id, predict_name){
        return instance.get(REMOVE_PREDICT,
            {
                params:{
                    store_id : store_id,
                    predict_name : predict_name,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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
    remove_discount_rule(store_id, name) {
        return instance.get(REMOVEֹֹֹ_DISCOUNTֹ_RULE,
            {
                params:{
                    store_id : store_id,
                    name : name,session_id:JSON.parse(sessionStorage.getItem("session_id"))
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
    remove_purchase_rule(store_id, name) {
        return instance.get(REMOVE_PURCHASE_RULE,
            {
                params:{
                    store_id : store_id,
                    name : name, session_id:JSON.parse(sessionStorage.getItem("session_id"))
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

