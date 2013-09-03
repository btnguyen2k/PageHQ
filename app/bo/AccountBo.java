package bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.github.ddth.plommon.utils.DPathUtils;

public class AccountBo extends AbstractBo {

    /* virtual db columns */
    public final static String COL_ACCOUNT_EMAIL = "account_email";
    public final static String COL_ACCOUNT_TIMESTAMP = "timestamp_create";

    @JsonProperty
    private String email;

    @JsonProperty
    private Date timestampCreate;

    public String getEmail() {
        return email;
    }

    public AccountBo setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getTimestampCreate() {
        return timestampCreate;
    }

    public AccountBo setTimestampCreate(Date timestampCreate) {
        this.timestampCreate = timestampCreate;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountBo populate(Map<String, Object> data) {
        setEmail(DPathUtils.getValue(data, COL_ACCOUNT_EMAIL, String.class));
        setTimestampCreate(DPathUtils.getValue(data, COL_ACCOUNT_TIMESTAMP, Date.class));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(COL_ACCOUNT_EMAIL, getEmail());
        result.put(COL_ACCOUNT_TIMESTAMP, getTimestampCreate());
        return result;
    }

}
