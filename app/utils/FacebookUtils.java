package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.FbPage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;

import play.Logger;
import play.mvc.Controller;

import com.github.ddth.plommon.utils.JsonUtils;
import com.github.ddth.plommon.utils.SessionUtils;
import com.github.ddth.springsocialhelper.FacebookHelper;

/**
 * Facebook-related utilities.
 * 
 * @author Thanh.Nguyen <btnguyen2k@gmail.com>
 */
public class FacebookUtils {

    private static boolean validateFbAccessToken(String fbAccessToken) {
        String cachedFbAccessToken = Controller.session(Constants.SESSION_FB_ACCESS_TOKEN);
        if (StringUtils.equals(fbAccessToken, cachedFbAccessToken)) {
            return true;
        }
        Controller.session(Constants.SESSION_FB_ACCESS_TOKEN, fbAccessToken);
        return false;
    }

    /**
     * Gets a Facebook fanpage.
     * 
     * @param fbAccessToken
     * @param pageId
     * @return
     */
    public static FbPage getFbPage(String fbAccessToken, String pageId) {
        List<FbPage> fbPages = getFbPages(fbAccessToken);
        if (fbPages != null) {
            for (FbPage fbPage : fbPages) {
                if (fbPage.id.equals(pageId)) {
                    return fbPage;
                }
            }
        }
        return null;
    }

    /**
     * Gets list of Facebook fanpages.
     * 
     * @param fbAccessToken
     * @return
     */
    public static List<FbPage> getFbPages(String fbAccessToken) {
        List<FbPage> result = null;
        if (validateFbAccessToken(fbAccessToken)) {
            List<?> cachedData = (List<?>) SessionUtils.getSession(Constants.SESSION_FB_PAGES);
            if (cachedData != null) {
                result = new ArrayList<FbPage>();
                for (Object obj : cachedData) {
                    FbPage fbPage = JsonUtils.fromJsonString(obj.toString(), FbPage.class);
                    result.add(fbPage);
                }
            }
        }
        if (result == null) {
            Facebook facebook = FacebookHelper.getFacebook(fbAccessToken);
            if (facebook == null) {
                return null;
            }
            result = new ArrayList<FbPage>();
            PagedList<Account> accounts = facebook.pageOperations().getAccounts();
            for (Account account : accounts) {
                FbPage fbPage = new FbPage();
                fbPage.category = account.getCategory();
                fbPage.id = account.getId();
                fbPage.name = account.getName();
                result.add(fbPage);
            }
            Collections.sort(result, new Comparator<FbPage>() {
                @Override
                public int compare(FbPage fbPage1, FbPage fbPage2) {
                    int result = fbPage1.name.compareToIgnoreCase(fbPage2.name);
                    return result != 0 ? result : fbPage1.id.compareToIgnoreCase(fbPage2.id);
                }
            });
            Logger.debug(result != null ? result.toString() : null);
            List<String> cachedData = new ArrayList<String>();
            for (FbPage fbPage : result) {
                cachedData.add(JsonUtils.toJsonString(fbPage));
            }
            SessionUtils.setSession(Constants.SESSION_FB_PAGES, cachedData, 900);
        }
        return result;
    }
}
