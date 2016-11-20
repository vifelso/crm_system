package controllers;

import java.util.List;

import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	redirect("/orders");
    }

}