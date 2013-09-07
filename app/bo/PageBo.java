package bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.ddth.plommon.bo.BaseBo;
import com.github.ddth.plommon.utils.DPathUtils;
import com.github.ddth.plommon.utils.JsonUtils;

public class PageBo extends BaseBo {

    /* virtual db columns */
    public final static String COL_PAGE_ID = "page_id";
    public final static String COL_PAGE_ADMIN = "admin_email";
    public final static String COL_PAGE_TIMESTAMP_CREATE = "timestamp_create";
    public final static String COL_PAGE_TIMESTAMP_ACTIVE = "timestamp_lastactive";
    public final static String COL_PAGE_STATUS = "page_status";
    public final static String COL_PAGE_SETTINGS = "page_settings";

    /* setting attributes */
    public final static String PAGE_SETTING_SIGNATURE = "signature";

    private Map<String, Object> objSettings;

    public String getId() {
        return getAttribute(COL_PAGE_ID, String.class);
    }

    public PageBo setId(String id) {
        return (PageBo) setAttribute(COL_PAGE_ID, id);
    }

    public String getAdminEmail() {
        return getAttribute(COL_PAGE_ADMIN, String.class);
    }

    public PageBo setAdminEmail(String adminEmail) {
        return (PageBo) setAttribute(COL_PAGE_ADMIN, adminEmail);
    }

    public Date getTimestampCreate() {
        return getAttribute(COL_PAGE_TIMESTAMP_CREATE, Date.class);
    }

    public PageBo setTimestampCreate(Date timestampCreate) {
        return (PageBo) setAttribute(COL_PAGE_TIMESTAMP_CREATE, timestampCreate);
    }

    public Date getTimestampLastActive() {
        return getAttribute(COL_PAGE_TIMESTAMP_ACTIVE, Date.class);
    }

    public PageBo setTimestampLastActive(Date timestampLastActive) {
        return (PageBo) setAttribute(COL_PAGE_TIMESTAMP_ACTIVE, timestampLastActive);
    }

    public Integer getStatus() {
        return getAttribute(COL_PAGE_STATUS, Integer.class);
    }

    public PageBo setStatus(Integer status) {
        return (PageBo) setAttribute(COL_PAGE_STATUS, status);
    }

    public Object getSetting(String key) {
        return DPathUtils.getValue(objSettings, key);
    }

    public <T> T getSetting(String key, Class<T> clazz) {
        return DPathUtils.getValue(objSettings, key, clazz);
    }

    public String getSettings() {
        return getAttribute(COL_PAGE_SETTINGS, String.class);
    }

    @SuppressWarnings("unchecked")
    public PageBo setSettings(String settings) {
        setAttribute(COL_PAGE_SETTINGS, settings);
        try {
            this.objSettings = JsonUtils.fromJsonString(settings, Map.class);
        } catch (Exception e) {
            this.objSettings = new HashMap<String, Object>();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(getId()).append(getAdminEmail());
        return hcb.hashCode();
    }
}
