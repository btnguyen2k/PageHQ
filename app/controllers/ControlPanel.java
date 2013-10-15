package controllers;

import org.springframework.social.facebook.api.FacebookProfile;

import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import play.mvc.Results;
import utils.Constants;
import utils.CookieUtils;
import bo.MyPagesDao;

import com.github.ddth.springsocialhelper.FacebookHelper;
import compositions.FbAuth;

public class ControlPanel extends Controller {

    /*
     * Handles /cp/index
     */
    @FbAuth
    public static Result index() {
        Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                Constants.COOKIE_FB_ACCESS_TOKEN);
        FacebookProfile fbProfile = cookieFbAccessToken != null ? FacebookHelper
                .getUserProfile(cookieFbAccessToken.value()) : null;
        String email = fbProfile != null ? fbProfile.getEmail() : null;
        if (email != null) {
            MyPagesDao.createAccount(email);
        }
        return Results.ok(views.html.Cp.index.render());
    }

}
