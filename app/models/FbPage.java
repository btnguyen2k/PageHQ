package models;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import utils.DPathUtils;
import utils.JsonUtils;
import bo.MyPagesDao;
import controllers.routes;

public class FbPage {
    public String id;

    public String name;

    public String category;

    public String signature;

    public FbPage() {
    }

    public FbPage(Map<String, Object> pageData) {
        populate(pageData);
    }

    @SuppressWarnings("unchecked")
    public void populate(Map<String, Object> pageData) {
        id = pageData.get(MyPagesDao.COL_PAGE_ID).toString();
        String strPageSettings = DPathUtils.getValue(pageData, MyPagesDao.COL_PAGE_SETTINGS,
                String.class);
        Map<String, Object> pageSettings = null;
        try {
            pageSettings = JsonUtils.fromJson(strPageSettings, Map.class);
        } catch (Exception e) {
            pageSettings = new HashMap<String, Object>();
        }
        signature = DPathUtils.getValue(pageSettings, MyPagesDao.PAGE_SETTING_SIGNATURE,
                String.class);
    }

    private String url;

    public String getUrl() {
        if (url == null) {
            url = "//facebook.com/pages/" + name + "/" + id;
        }
        return url;
    }

    private String urlEdit;

    public String getUrlEdit() {
        if (urlEdit == null) {
            urlEdit = routes.ControlPanel_Fbpage.edit(id).url();
        }
        return urlEdit;
    }

    private String urlPost;

    public String getUrlPost() {
        if (urlPost == null) {
            urlPost = routes.ControlPanel_Fbpage.post(id).url();
        }
        return urlPost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(id).append(name).append(category);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FbPage)) {
            return false;
        }
        FbPage other = (FbPage) obj;
        return id.equals(other.id);
    }
}
