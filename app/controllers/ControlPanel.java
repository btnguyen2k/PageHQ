package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import compositions.FbAuth;

import com.github.ddth.plommon.utils.*;

public class ControlPanel extends Controller {

    /*
     * Handles /cp/index
     */
    @FbAuth
    public static Result index() {
        return Results.ok(views.html.Cp.index.render());
    }

}
