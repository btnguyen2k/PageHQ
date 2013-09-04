package models;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import bo.PageBo;

import com.github.ddth.plommon.utils.DPathUtils;
import com.github.ddth.plommon.utils.JsonUtils;

public class FbPage {

    @JsonProperty
    public String id, name, category;

    @JsonProperty
    public PageBo page;

    @JsonProperty
    public String signature;

    public FbPage() {
    }

    public FbPage(PageBo page) {
        populate(page);
    }

    @SuppressWarnings("unchecked")
    public FbPage populate(PageBo page) {
        this.page = page;
        String strPageSettings = page.getSettings();
        Map<String, Object> pageSettings = null;
        try {
            pageSettings = JsonUtils.fromJsonString(strPageSettings, Map.class);
        } catch (Exception e) {
            pageSettings = new HashMap<String, Object>();
        }
        signature = DPathUtils.getValue(pageSettings, PageBo.PAGE_SETTING_SIGNATURE, String.class);
        return this;
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
            urlEdit = controllers.routes.ControlPanel_Fbpage.edit(id).url();
        }
        return urlEdit;
    }

    private String urlPost;

    public String getUrlPost() {
        if (urlPost == null) {
            urlPost = controllers.routes.ControlPanel_Fbpage.post(id).url();
        }
        return urlPost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(page).append(name).append(category);
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
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(page, other.page).append(id, other.id).append(name, other.name)
                .append(category, other.category);
        return eb.isEquals();
    }
}
