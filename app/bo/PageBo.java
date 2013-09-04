package bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import com.github.ddth.plommon.utils.DPathUtils;
import com.github.ddth.plommon.utils.JsonUtils;

public class PageBo extends AbstractBo {

    /* virtual db columns */
    public final static String COL_PAGE_ID = "page_id";
    public final static String COL_PAGE_ADMIN = "admin_email";
    public final static String COL_PAGE_TIMESTAMP_CREATE = "timestamp_create";
    public final static String COL_PAGE_TIMESTAMP_ACTIVE = "timestamp_lastactive";
    public final static String COL_PAGE_STATUS = "page_status";
    public final static String COL_PAGE_SETTINGS = "page_settings";

    /* setting attributes */
    public final static String PAGE_SETTING_SIGNATURE = "signature";

    @JsonProperty
    private String id, adminEmail;

    @JsonProperty
    private Date timestampCreate, timestampLastActive;

    @JsonProperty
    private Integer status;

    @JsonProperty
    private String settings;

    private Map<String, Object> objSettings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public Date getTimestampCreate() {
        return timestampCreate;
    }

    public void setTimestampCreate(Date timestampCreate) {
        this.timestampCreate = timestampCreate;
    }

    public Date getTimestampLastActive() {
        return timestampLastActive;
    }

    public void setTimestampLastActive(Date timestampLastActive) {
        this.timestampLastActive = timestampLastActive;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getSetting(String key) {
        return DPathUtils.getValue(objSettings, key);
    }

    public <T> T getSetting(String key, Class<T> clazz) {
        return DPathUtils.getValue(objSettings, key, clazz);
    }

    public String getSettings() {
        return settings;
    }

    @SuppressWarnings("unchecked")
    public void setSettings(String settings) {
        this.settings = settings;
        try {
            this.objSettings = JsonUtils.fromJsonString(settings, Map.class);
        } catch (Exception e) {
            this.objSettings = new HashMap<String, Object>();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageBo populate(Map<String, Object> data) {
        setId(DPathUtils.getValue(data, COL_PAGE_ID, String.class));
        setAdminEmail(DPathUtils.getValue(data, COL_PAGE_ADMIN, String.class));
        setTimestampCreate(DPathUtils.getValue(data, COL_PAGE_TIMESTAMP_CREATE, Date.class));
        setTimestampLastActive(DPathUtils.getValue(data, COL_PAGE_TIMESTAMP_ACTIVE, Date.class));
        setStatus(DPathUtils.getValue(data, COL_PAGE_STATUS, Integer.class));
        setSettings(DPathUtils.getValue(data, COL_PAGE_SETTINGS, String.class));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(COL_PAGE_ID, getId());
        result.put(COL_PAGE_ADMIN, getAdminEmail());
        result.put(COL_PAGE_TIMESTAMP_CREATE, getTimestampCreate());
        result.put(COL_PAGE_TIMESTAMP_ACTIVE, getTimestampLastActive());
        result.put(COL_PAGE_STATUS, getStatus());
        result.put(COL_PAGE_SETTINGS, getSettings());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(id).append(adminEmail);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PageBo)) {
            return false;
        }
        PageBo other = (PageBo) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(id, other.id).append(adminEmail, other.adminEmail)
                .append(timestampCreate, other.timestampCreate)
                .append(timestampLastActive, other.timestampLastActive)
                .append(status, other.status).append(settings, other.settings);
        return eb.isEquals();
    }
}
