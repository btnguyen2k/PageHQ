package compositions;

import java.util.List;

import models.FbPage;

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
        List<FbPage> fbPages = null;
        Cookie cookieFbAccessToken = null;
        try {
            cookieFbAccessToken = CookieUtils.getCookie(ctx.request(),
                    Constants.COOKIE_FB_ACCESS_TOKEN);
            if (cookieFbAccessToken == null) {
                Logger.debug("No FbAccessToken cookie.");
            }
            fbPages = cookieFbAccessToken != null ? FacebookUtils.getFbPages(cookieFbAccessToken
                    .value()) : null;
        } catch (Exception e) {
            fbPages = null;
        }
        if (fbPages == null) {
            if (cookieFbAccessToken != null) {
                Logger.debug("FbAccessToken expires?");
            } else {
                Logger.debug("Error: cannot get fanpage list!");
            }
            return redirect("/");
        } else {
            FacebookProfile fbProfile = FacebookUtils.getFbProfile(cookieFbAccessToken.value());
            String email = fbProfile.getEmail();
            AccountBo account = MyPagesDao.getAccount(email);
            if (account == null) {
                MyPagesDao.createAccount(email);
            }
        }
        return delegate.call(ctx);
    }
}
