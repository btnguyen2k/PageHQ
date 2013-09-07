package bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.ddth.plommon.bo.BaseBo;
import com.github.ddth.plommon.utils.DPathUtils;
import com.github.ddth.plommon.utils.JsonUtils;

public class FeedBo extends BaseBo {

    public final static int FEED_TYPE_TEXT = 0;
    public final static int FEED_TYPE_LINK = 1;
    public final static int FEED_TYPE_PHOTO = 2;

    public final static String METAINFO_TEXT = "text";
    public final static String METAINFO_URL = "url";
    public final static String METAINFO_CAPTION = "caption";

    /* virtual db columns */
    public final static String COL_ID = "id";
    public final static String COL_FEED_ID = "feed_id";
    public final static String COL_FEED_TYPE = "feed_type";
    public final static String COL_FEED_METAINFO = "feed_metainfo";
    public final static String COL_USER_EMAIL = "user_email";
    public final static String COL_PAGE_ID = "page_id";
    public final static String COL_TIMESTAMP_CREATE = "timestamp_create";
    public final static String COL_NUM_LIKES = "num_likes";
    public final static String COL_NUM_COMMENTS = "num_comments";
    public final static String COL_NUM_SHARES = "num_shares";

    private Map<String, Object> metainfo;

    public String getId() {
        return getAttribute(COL_ID, String.class);
    }

    public FeedBo setId(String id) {
        return (FeedBo) setAttribute(COL_ID, id);
    }

    public String getFeedId() {
        return getAttribute(COL_FEED_ID, String.class);
    }

    public FeedBo setFeedId(String feedId) {
        return (FeedBo) setAttribute(COL_FEED_ID, feedId);
    }

    public Integer getFeedType() {
        return getAttribute(COL_FEED_TYPE, Integer.class);
    }

    public FeedBo setFeedType(Integer feedType) {
        return (FeedBo) setAttribute(COL_FEED_TYPE, feedType);
    }

    public String getMetaInfo() {
        return getAttribute(COL_FEED_METAINFO, String.class);
    }

    public Object getMetaInfo(String key) {
        return DPathUtils.getValue(metainfo, key);
    }

    public <T> T getMetaInfo(String key, Class<T> clazz) {
        return DPathUtils.getValue(metainfo, key, clazz);
    }

    @SuppressWarnings("unchecked")
    public FeedBo setMetaInfo(String metaInfo) {
        setAttribute(COL_FEED_METAINFO, metaInfo);
        try {
            this.metainfo = JsonUtils.fromJsonString(metaInfo, Map.class);
        } catch (Exception e) {
            this.metainfo = new HashMap<String, Object>();
        }
        return this;
    }

    public String getUserEmail() {
        return getAttribute(COL_USER_EMAIL, String.class);
    }

    public FeedBo setUserEmail(String userEmail) {
        return (FeedBo) setAttribute(COL_USER_EMAIL, userEmail);
    }

    public String getPageId() {
        return getAttribute(COL_PAGE_ID, String.class);
    }

    public FeedBo setPageId(String pageId) {
        return (FeedBo) setAttribute(COL_PAGE_ID, pageId);
    }

    public Date getTimestampCreate() {
        return getAttribute(COL_TIMESTAMP_CREATE, Date.class);
    }

    public FeedBo setTimestampCreate(Date timestampCreate) {
        return (FeedBo) setAttribute(COL_TIMESTAMP_CREATE, timestampCreate);
    }

    public Integer getNumLikes() {
        return getAttribute(COL_NUM_LIKES, Integer.class);
    }

    public FeedBo setNumLikes(Integer numLikes) {
        return (FeedBo) setAttribute(COL_NUM_LIKES, numLikes);
    }

    public Integer getNumShares() {
        return getAttribute(COL_NUM_SHARES, Integer.class);
    }

    public FeedBo setNumShares(Integer numShares) {
        return (FeedBo) setAttribute(COL_NUM_SHARES, numShares);
    }

    public Integer getNumComments() {
        return getAttribute(COL_NUM_COMMENTS, Integer.class);
    }

    public FeedBo setNumComments(Integer numComments) {
        return (FeedBo) setAttribute(COL_NUM_COMMENTS, numComments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(getId()).append(getFeedId()).append(getFeedType()).append(getUserEmail())
                .append(getPageId()).append(getTimestampCreate());
        return hcb.hashCode();
    }
}
