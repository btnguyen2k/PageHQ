package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.FbPage;
import models.FbPostText;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.FacebookProfile;

import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Results;
import utils.Constants;
import utils.CookieUtils;
import utils.FacebookUtils;
import bo.FeedBo;
import bo.MyPagesDao;
import bo.PageBo;

import com.github.ddth.plommon.utils.DPathUtils;
import com.github.ddth.plommon.utils.JsonUtils;
import compositions.FbAuth;

public class ControlPanel_Fbpage extends Controller {

    public final static String URL_PARAM_PAGE_ID = "pid";

    /*
     * Handles /cp/listFbPages
     */
    @FbAuth
    public static Result list() {
        Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                Constants.COOKIE_FB_ACCESS_TOKEN);
        List<FbPage> fbPages = cookieFbAccessToken != null ? FacebookUtils
                .getFbPages(cookieFbAccessToken.value()) : null;
        FacebookProfile fbProfile = cookieFbAccessToken != null ? FacebookUtils
                .getFbProfile(cookieFbAccessToken.value()) : null;
        if (fbPages != null) {
            String email = fbProfile.getEmail();
            for (FbPage fbPage : fbPages) {
                PageBo page = MyPagesDao.getPage(fbPage.id, email);
                if (page == null) {
                    MyPagesDao.createPage(fbPage.id, email);
                } else {
                    fbPage.signature = page.getSetting(PageBo.PAGE_SETTING_SIGNATURE, String.class);
                    MyPagesDao.updatePageLastActive(page);
                }
            }
        }
        String message = flash("msg");
        return Results.ok(views.html.Cp.fbpage_list.render(message, fbPages));
    }

    private final static Form<FbPage> formFbPage = Form.form(FbPage.class);

    /*
     * Handles GET:/cp/editFbPage?pid=<page_id>
     */
    @FbAuth
    public static Result edit(String pageId) {
        Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                Constants.COOKIE_FB_ACCESS_TOKEN);
        FacebookProfile fbProfile = cookieFbAccessToken != null ? FacebookUtils
                .getFbProfile(cookieFbAccessToken.value()) : null;
        String email = fbProfile != null ? fbProfile.getEmail() : null;
        PageBo page = email != null ? MyPagesDao.getPage(pageId, email) : null;
        if (page == null) {
            return Results.badRequest(views.html.Cp.fbpage_edit.render(null));
        }
        FbPage fbPage = FacebookUtils.getFbPage(cookieFbAccessToken.value(), pageId);
        fbPage.populate(page);
        return Results.ok(views.html.Cp.fbpage_edit.render(formFbPage.fill(fbPage)));
    }

    /*
     * Handles POST:/cp/editFbPage?pid=<page_id>
     */
    @FbAuth
    public static Result editSubmit(String pageId) {
        Form<FbPage> filledForm = formFbPage.bindFromRequest();

        Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                Constants.COOKIE_FB_ACCESS_TOKEN);
        FacebookProfile fbProfile = cookieFbAccessToken != null ? FacebookUtils
                .getFbProfile(cookieFbAccessToken.value()) : null;
        String email = fbProfile != null ? fbProfile.getEmail() : null;
        PageBo page = email != null ? MyPagesDao.getPage(pageId, email) : null;
        if (page == null) {
            return Results.badRequest(views.html.Cp.fbpage_edit.render(null));
        }

        if (filledForm.hasErrors()) {
            return Results.badRequest(views.html.Cp.fbpage_edit.render(filledForm));
        } else {
            String signature = filledForm.get().signature;
            signature = signature != null ? signature.trim() : "";
            MyPagesDao.updatePageSignature(pageId, email, signature);
            Controller.flash("msg", Messages.get("msg.fbpage.edit.done"));
            return Results.redirect(controllers.routes.ControlPanel_Fbpage.list());
        }
    }

    private final static Form<FbPostText> formFbPostText = Form.form(FbPostText.class);

    /*
     * Handles GET:/cp/postFbPage?pid=<page_id>
     */
    @FbAuth
    public static Result post(String pageId) {
        Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                Constants.COOKIE_FB_ACCESS_TOKEN);
        FacebookProfile fbProfile = cookieFbAccessToken != null ? FacebookUtils
                .getFbProfile(cookieFbAccessToken.value()) : null;
        String email = fbProfile != null ? fbProfile.getEmail() : null;
        if (StringUtils.isBlank(email)) {
            return Results.badRequest(views.html.Cp.fbpage_post.render(null, null, null));
        }
        FbPage fbPage = null;
        if (!StringUtils.isBlank(pageId)) {
            PageBo page = email != null ? MyPagesDao.getPage(pageId, email) : null;
            if (page == null) {
                return Results.badRequest(views.html.Cp.fbpage_post.render(null, null, null));
            }
            fbPage = FacebookUtils.getFbPage(cookieFbAccessToken.value(), pageId);
            fbPage.populate(page);
        }
        FbPostText fbPostText = new FbPostText();
        fbPostText.pages = new ArrayList<String>();
        if (fbPage != null) {
            fbPostText.pages.add(fbPage.id);
        }
        List<FbPage> fbPages = FacebookUtils.getFbPages(cookieFbAccessToken.value());
        return Results.ok(views.html.Cp.fbpage_post.render(formFbPostText.fill(fbPostText),
                fbPages, null));
    }

    /*
     * Handles POST:/cp/ajax_postPhoto
     */
    @BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength = 5 * 1024 * 1024)
    public static Result postPhoto() {
        try {
            MultipartFormData body = Controller.request().body().asMultipartFormData();
            FilePart file = body.getFile("photo");
            if (file == null) {
                return jsonResponse(400, Messages.get("error.fbpost.nofile"));
            }
            if (!StringUtils.startsWithIgnoreCase(file.getContentType(), "image/")) {
                return jsonResponse(400, Messages.get("error.fbpost.invalid_photo"));
            }
            File physicalFile = file.getFile();
            if (physicalFile.length() < 1) {
                return jsonResponse(400, Messages.get("error.fbpost.invalid_photo"));
            }

            Map<String, String[]> formData = body.asFormUrlEncoded();
            String[] temp = formData.get("pages");
            String[] pageIds = temp != null && temp.length > 0 ? temp[0].split("[\\s+,]+") : null;
            if (pageIds != null && pageIds.length > 0) {
                temp = formData.get("caption");
                String caption = temp != null && temp.length > 0 ? temp[0].trim() : "";

                Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                        Constants.COOKIE_FB_ACCESS_TOKEN);
                String fbAccessToken = cookieFbAccessToken != null ? cookieFbAccessToken.value()
                        : null;

                Facebook facebook = FacebookUtils.getFacebook(fbAccessToken);
                FacebookProfile fbProfile = fbAccessToken != null ? facebook.userOperations()
                        .getUserProfile() : null;
                String email = fbProfile != null ? fbProfile.getEmail() : null;

                if (facebook != null && email != null) {
                    for (String pageId : pageIds) {
                        FbPage fbPage = FacebookUtils.getFbPage(fbAccessToken, pageId);
                        if (fbPage != null) {
                            PageBo page = MyPagesDao.getPage(pageId, email);
                            if (page != null) {
                                fbPage.populate(page);
                            }
                            String signature = fbPage.signature;
                            if (!StringUtils.isBlank(signature)) {
                                signature = "\r\n" + signature;
                            } else {
                                signature = "";
                            }

                            Resource facebookPhoto = new FileSystemResource(physicalFile);
                            String feedId = facebook.pageOperations().postPhoto(pageId, pageId,
                                    facebookPhoto, caption + signature);
                            if (!StringUtils.isBlank(feedId)) {
                                Map<String, Object> metaInfo = new HashMap<String, Object>();
                                metaInfo.put(FeedBo.METAINFO_CAPTION, caption);
                                MyPagesDao.createFeed(feedId, FeedBo.FEED_TYPE_PHOTO, metaInfo,
                                        email, pageId);
                            }
                        }
                    }
                }
            }
            return jsonResponse(200, "");
        } catch (Exception e) {
            return jsonResponse(500, e.getClass() + "/" + e.getMessage());
        }
    }

    /*
     * Handles POST:/cp/ajax_postLink
     */
    @SuppressWarnings("unchecked")
    public static Result postLink() {
        try {
            JsonNode jsonNode = Controller.request().body().asJson();
            Map<String, Object> postData = null;
            try {
                postData = JsonUtils.fromJsonString(jsonNode.toString(), Map.class);
            } catch (Exception e) {
                postData = new HashMap<String, Object>();
            }
            if (!(postData instanceof Map)) {
                return jsonResponse(400, "Expect input to be a map!");
            }
            String url = DPathUtils.getValue(postData, "url", String.class);
            if (!StringUtils.isBlank(url)) {

            }
            String urlCaption = DPathUtils.getValue(postData, "url_caption", String.class);
            String urlDesc = DPathUtils.getValue(postData, "url_description", String.class);
            String pageId = DPathUtils.getValue(postData, "page", String.class);
            Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                    Constants.COOKIE_FB_ACCESS_TOKEN);
            String fbAccessToken = cookieFbAccessToken != null ? cookieFbAccessToken.value() : null;
            FacebookProfile fbProfile = fbAccessToken != null ? FacebookUtils
                    .getFbProfile(cookieFbAccessToken.value()) : null;
            String email = fbProfile != null ? fbProfile.getEmail() : null;
            FbPage fbPage = fbAccessToken != null ? FacebookUtils.getFbPage(
                    cookieFbAccessToken.value(), pageId) : null;
            if (fbPage == null) {
                return jsonResponse(400, "Invalid page or your Facebook session has expired!");
            }
            PageBo page = MyPagesDao.getPage(pageId, email);
            if (page != null) {
                fbPage.populate(page);
            }
            String signature = fbPage.signature;
            if (!StringUtils.isBlank(signature)) {
                signature = "\r\n" + signature;
            } else {
                signature = "";
            }
            if (StringUtils.isBlank(urlDesc)) {
                urlDesc = "";
            }
            FacebookLink facebookLink = new FacebookLink(url.trim(), null,
                    urlCaption != null ? urlCaption.trim() : null, null);
            Facebook facebook = FacebookUtils.getFacebook(fbAccessToken);
            String feedId = facebook.pageOperations().post(pageId, urlDesc.trim() + signature,
                    facebookLink);
            if (!StringUtils.isBlank(feedId)) {
                Map<String, Object> metaInfo = new HashMap<String, Object>();
                metaInfo.put(FeedBo.METAINFO_URL, url);
                MyPagesDao.createFeed(feedId, FeedBo.FEED_TYPE_LINK, metaInfo, email, pageId);
                return jsonResponse(200, feedId);
            } else {
                return jsonResponse(500, "Unknow error, can not post to page!");
            }
        } catch (Exception e) {
            return jsonResponse(500, e.getClass() + "/" + e.getMessage());
        }
    }

    /*
     * Handles POST:/cp/ajax_postText
     */
    @SuppressWarnings("unchecked")
    public static Result postText() {
        try {
            JsonNode jsonNode = Controller.request().body().asJson();
            Map<String, Object> postData = null;
            try {
                postData = JsonUtils.fromJsonString(jsonNode.toString(), Map.class);
            } catch (Exception e) {
                postData = new HashMap<String, Object>();
            }
            if (!(postData instanceof Map)) {
                return jsonResponse(400, "Expect input to be a map!");
            }
            String text = DPathUtils.getValue(postData, "text", String.class);
            String pageId = DPathUtils.getValue(postData, "page", String.class);
            if (StringUtils.isBlank(text)) {
                return jsonResponse(400, "No text to post!");
            }
            Cookie cookieFbAccessToken = CookieUtils.getCookie(request(),
                    Constants.COOKIE_FB_ACCESS_TOKEN);
            String fbAccessToken = cookieFbAccessToken != null ? cookieFbAccessToken.value() : null;
            FacebookProfile fbProfile = fbAccessToken != null ? FacebookUtils
                    .getFbProfile(cookieFbAccessToken.value()) : null;
            String email = fbProfile != null ? fbProfile.getEmail() : null;
            FbPage fbPage = fbAccessToken != null ? FacebookUtils.getFbPage(
                    cookieFbAccessToken.value(), pageId) : null;
            if (fbPage == null) {
                return jsonResponse(400, "Invalid page or your Facebook session has expired!");
            }
            PageBo page = MyPagesDao.getPage(pageId, email);
            if (page != null) {
                fbPage.populate(page);
            }
            String signature = fbPage.signature;
            if (!StringUtils.isBlank(signature)) {
                signature = "\r\n" + signature;
            } else {
                signature = "";
            }
            Facebook facebook = FacebookUtils.getFacebook(fbAccessToken);
            String feedId = facebook.pageOperations().post(pageId, text + signature);
            if (!StringUtils.isBlank(feedId)) {
                Map<String, Object> metaInfo = new HashMap<String, Object>();
                metaInfo.put(FeedBo.METAINFO_TEXT, text);
                MyPagesDao.createFeed(feedId, FeedBo.FEED_TYPE_TEXT, metaInfo, email, pageId);
                return jsonResponse(200, feedId);
            } else {
                return jsonResponse(500, "Unknow error, can not post to page!");
            }
        } catch (Exception e) {
            return jsonResponse(500, e.getClass() + "/" + e.getMessage());
        }
    }

    private static Result jsonResponse(int status, Object message) {
        ObjectNode result = Json.newObject();
        result.put("status", status);
        result.put("message", Json.toJson(message));
        return Controller.ok(result);
    }
}
