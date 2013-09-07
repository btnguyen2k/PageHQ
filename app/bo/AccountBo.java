package bo;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.ddth.plommon.bo.BaseBo;

public class AccountBo extends BaseBo {

    /* virtual db columns */
    public final static String COL_ACCOUNT_EMAIL = "account_email";
    public final static String COL_ACCOUNT_TIMESTAMP = "timestamp_create";

    public String getEmail() {
        return getAttribute(COL_ACCOUNT_EMAIL, String.class);
    }

    public AccountBo setEmail(String email) {
        return (AccountBo) setAttribute(COL_ACCOUNT_EMAIL, email);
    }

    public Date getTimestampCreate() {
        return getAttribute(COL_ACCOUNT_TIMESTAMP, Date.class);
    }

    public AccountBo setTimestampCreate(Date timestampCreate) {
        return (AccountBo) setAttribute(COL_ACCOUNT_TIMESTAMP, timestampCreate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(getEmail());
        return hcb.hashCode();
    }
}
