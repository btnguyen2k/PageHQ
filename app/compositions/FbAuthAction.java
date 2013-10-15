package compositions;

import org.springframework.social.facebook.api.FacebookProfile;

import play.Logger;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.SimpleResult;
import utils.Constants;
import utils.CookieUtils;
import utils.JedisUtils;
import bo.AccountBo;
import bo.MyPagesDao;

import com.github.ddth.plommon.utils.SessionUtils;
import com.github.ddth.springsocialhelper.FacebookHelper;

public class FbAuthAction extends Action.Simple {
    public Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
        /*
         * FB_ACCESS_EXPIRY = get from cookie
         * 
         * FB_ACCESS_TOKEN_TIME = get from session
         * 
         * if ( FB_ACCESS_TOKEN_TIME + FB_ACCESS_TOKEN_EXPIRY <
         * System.currentTimeMillis()/1000 ) then FB_ACCESS_TOKEN has expired!
         */
        int fbAccessTokenTimeExpiry = 0;
        try {
            Cookie cookieExpiry = CookieUtils.getCookie(ctx.request(),
                    Constants.COOKIE_FB_ACCESS_TOKEN_EXPIRY);
            fbAccessTokenTimeExpiry = Integer.parseInt(cookieExpiry.value());
        } catch (Exception e) {
            fbAccessTokenTimeExpiry = 0;
        }
        int fbAccessTokenTime = 0;
        try {
            Object sessionTime = SessionUtils.getSession(Constants.SESSION_FB_ACCESS_TOKEN_TIME,
                    false);
            fbAccessTokenTime = Integer.parseInt(sessionTime.toString());
        } catch (Exception e) {
            fbAccessTokenTime = 0;
        }

        FacebookProfile fbProfile = null;
        if (fbAccessTokenTimeExpiry < 30
                || fbAccessTokenTime + 30 < System.currentTimeMillis() / 1000) {
            // store/update FB_ACCESS_TOKEN_TIME
            int temp = (int) (System.currentTimeMillis() / 1000);
            SessionUtils.setSession(Constants.SESSION_FB_ACCESS_TOKEN_TIME, temp,
                    fbAccessTokenTimeExpiry + 1);

            // recheck FB Access Token
            Cookie cookieFbAccessToken = null;
            try {
                cookieFbAccessToken = CookieUtils.getCookie(ctx.request(),
                        Constants.COOKIE_FB_ACCESS_TOKEN);
                if (cookieFbAccessToken == null) {
                    Logger.debug("No FbAccessToken cookie.");
                }
                fbProfile = cookieFbAccessToken != null ? FacebookHelper
                        .getUserProfile(cookieFbAccessToken.value()) : null;
            } catch (Exception e) {
                fbProfile = null;
            }
            if (fbProfile == null) {
                if (cookieFbAccessToken != null) {
                    Logger.debug("FbAccessToken expires?");
                } else {
                    Logger.debug("Error: cannot get FB profile!");
                }
                Promise<SimpleResult> result = Promise.promise(new Function0<SimpleResult>() {
                    public SimpleResult apply() {
                        return redirect("/");
                    }
                });
                return result;
            } else {
                String email = fbProfile.getEmail();
                AccountBo account = MyPagesDao.getAccount(email);
                if (account == null) {
                    MyPagesDao.createAccount(email);
                }
            }
        }

        if (fbProfile != null) {
            // add account to list of active accounts
            Number accountLastActive = (Number) SessionUtils
                    .getSession(Constants.SESSION_ACC_LAST_ACTIVE);
            if (accountLastActive == null) {
                accountLastActive = Long.valueOf(0);
            }
            if (accountLastActive.longValue() + 60000 < System.currentTimeMillis()) {
                SessionUtils.setSession(Constants.SESSION_ACC_LAST_ACTIVE,
                        System.currentTimeMillis());
                String email = fbProfile.getEmail();
                JedisUtils.setAdd(Constants.REDIS_SET_ACTIVE_ACCOUNTS, JedisUtils.serialize(email));
            }
        }

        return delegate.call(ctx);
    }
}
