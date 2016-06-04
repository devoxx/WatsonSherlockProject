package com.devoxx.watson.controller;

import org.junit.Test;

/**
 * @author Stephan Janssen
 */
public class AlchemyAPIServiceTest {

    @Test
    public void process() {
        final AlchemyAPIService alchemyAPIService = new AlchemyAPIService();
        alchemyAPIService.setApikey("446dc444d593ed09eece2c66476c87c269c37896");
        alchemyAPIService.process("https://www.voxxed.com/blog/2016/01/microservices-versus-soa-practice/");
    }

}
