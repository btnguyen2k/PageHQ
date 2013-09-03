package bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.github.ddth.plommon.utils.DPathUtils;

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

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
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

}
