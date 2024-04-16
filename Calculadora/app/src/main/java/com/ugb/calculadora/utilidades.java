package com.ugb.calculadora;

import java.util.Base64;

public class utilidades {
    //static String urlConsulta = "http://192.26.152:5984/database/tienda_online/_design/tienda_online/_view/tienda_online";
    static String urlConsulta = "http://192.168.1.8:5984/tienda_online/_design/tienda_online/_view/tienda_online";
    static String urlMto = "http://192.168.1.8:5984/tienda_online";
    static String user = "admin";
    static String passwd = "31416";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarIdUnico(){
        return java.util.UUID.randomUUID().toString();
    }
}