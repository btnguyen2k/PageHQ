package compositions;

import org.springframework.social.facebook.api.FacebookProfile;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import utils.Constants;
import utils.CookieUtils;
import utils.FacebookUtils;
import bo.AccountBo;
import bo.MyPagesDao;

public class FbAuthAction extends Action.Simple {
    public Result call(Http.Context ctx) throws Throwable {
        Cookie cookieExpiry = CookieUtils.getCookie(ctx.request(),
                Constants.COOKIE_FB_ACCESS_TOKEN_EXPIRY);
        String strExpiry = cookieExpiry != null ? cookieExpiry.value() : null;
        int expiry = 0;
        try {
            expiry = Integer.parseInt(strExpiry);
        } catch (Exception e) {
            expiry = 0;
        }
        if (expiry < 30) {
            FacebookProfile fbProfile = null;
            Cookie cookieFbAccessToken = null;
            try {
                cookieFbAccessToken = CookieUtils.getCookie(ctx.request(),
                        Constants.COOKIE_FB_ACCESS_TOKEN);
                if (cookieFbAccessToken == null) {
                    Logger.debug("No FbAccessToken cookie.");
                }
                fbProfile = cookieFbAccessToken != null ? FacebookUtils
                        .getFbProfile(cookieFbAccessToken.value()) : null;
            } catch (Exception e) {
                fbProfile = null;
            }
            if (fbProfile == null) {
                if (cookieFbAccessToken != null) {
                    Logger.debug("FbAccessToken expires?");
                } else {
                    Logger.debug("Error: cannot get FB profile!");
                }
                return redirect("/");
            } else {
                String email = fbProfile.getEmail();
                AccountBo account = MyPagesDao.getAccount(email);
                if (account == null) {
                    MyPagesDao.createAccount(email);
                }
            }
        }
        return delegate.call(ctx);
    }
}
