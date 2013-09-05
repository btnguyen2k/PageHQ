package bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import com.github.ddth.plommon.utils.DPathUtils;

public class FeedBo extends AbstractBo {

    public final static int FEED_TYPE_TEXT = 0;
    public final static int FEED_TYPE_LINK = 1;
    public final static int FEED_TYPE_PHOTO = 2;

    /* virtual db columns */
    public final static String COL_FEED_ID = "feed_id";
    public final static String COL_FEED_TYPE = "feed_type";
    public final static String COL_USER_EMAIL = "user_email";
    public final static String COL_PAGE_ID = "page_id";
    public final static String COL_TIMESTAMP_CREATE = "timestamp_create";
    public final static String COL_NUM_LIKES = "num_likes";
    public final static String COL_NUM_COMMENTS = "num_comments";
    public final static String COL_NUM_SHARES = "num_shares";

    @JsonProperty
    private String feedId, feedType, userEmail, pageId;

    @JsonProperty
    private Date timestampCreate;

    @JsonProperty
    private Integer numLikes, numShares, numComments;

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Date getTimestampCreate() {
        return timestampCreate;
    }

    public void setTimestampCreate(Date timestampCreate) {
        this.timestampCreate = timestampCreate;
    }

    public Integer getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Integer numLikes) {
        this.numLikes = numLikes;
    }

    public Integer getNumShares() {
        return numShares;
    }

    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    public Integer getNumComments() {
        return numComments;
    }

    public void setNumComments(Integer numComments) {
        this.numComments = numComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeedBo populate(Map<String, Object> data) {
        setFeedId(DPathUtils.getValue(data, COL_FEED_ID, String.class));
        setFeedType(DPathUtils.getValue(data, COL_FEED_TYPE, String.class));
        setUserEmail(DPathUtils.getValue(data, COL_USER_EMAIL, String.class));
        setPageId(DPathUtils.getValue(data, COL_PAGE_ID, String.class));
        setTimestampCreate(DPathUtils.getValue(data, COL_TIMESTAMP_CREATE, Date.class));
        setNumLikes(DPathUtils.getValue(data, COL_NUM_LIKES, Integer.class));
        setNumShares(DPathUtils.getValue(data, COL_NUM_SHARES, Integer.class));
        setNumComments(DPathUtils.getValue(data, COL_NUM_COMMENTS, Integer.class));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(COL_FEED_ID, getFeedId());
        result.put(COL_FEED_TYPE, getFeedType());
        result.put(COL_USER_EMAIL, getUserEmail());
        result.put(COL_PAGE_ID, getPageId());
        result.put(COL_TIMESTAMP_CREATE, getTimestampCreate());
        result.put(COL_NUM_LIKES, getNumLikes());
        result.put(COL_NUM_SHARES, getNumShares());
        result.put(COL_NUM_COMMENTS, getNumComments());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(feedId).append(userEmail).append(pageId).append(timestampCreate);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FeedBo)) {
            return false;
        }
        FeedBo other = (FeedBo) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(feedId, other.feedId).append(userEmail, other.userEmail)
                .append(pageId, other.pageId).append(timestampCreate, other.timestampCreate);
        return eb.isEquals();
    }
}
