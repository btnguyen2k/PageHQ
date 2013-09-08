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

import com.github.ddth.plommon.utils.SessionUtils;

public class FbAuthAction extends Action.Simple {
    public Result call(Http.Context ctx) throws Throwable {
        /*
         * FB_ACCESS_TOKEN_TIME = get from session FB_ACCESS_EXPIRY = get from
         * cookie if ( FB_ACCESS_TOKEN_TIME + FB_ACCESS_TOKEN_EXPIRY <
         * System.currentTimeMillis()/1000 ) then FB_ACCESS_TOKEN has expired!
         */
        int fbAccessTokenTimeExpiry = 0;
        Cookie cookieExpiry = null;
        try {
            cookieExpiry = CookieUtils.getCookie(ctx.request(),
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

        if (fbAccessTokenTimeExpiry > 0) {
            int temp = (int) (System.currentTimeMillis() / 1000);
            SessionUtils.setSession(Constants.SESSION_FB_ACCESS_TOKEN_TIME, temp,
                    fbAccessTokenTimeExpiry + 1);
        }

        if (fbAccessTokenTimeExpiry < 30
                || fbAccessTokenTime + fbAccessTokenTimeExpiry < System.currentTimeMillis() / 1000) {
            // recheck FB Access Token
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
