package com.knot.uol.mediators;

import javax.ejb.Stateless;


@Stateless
public class QueryProperties {

public String sayHello(String name) {
	return "Hello ," + name+" .!";
	
}


}
