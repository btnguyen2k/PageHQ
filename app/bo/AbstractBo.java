package bo;

import java.util.Map;

/**
 * Base class for application BOs.
 * 
 * @author Thanh.Nguyen <btnguyen2k@gmail.com>
 */
public abstract class AbstractBo {
    /**
     * Populates the model with data from a map.
     * 
     * @param data
     * @return
     */
    public abstract AbstractBo populate(Map<String, Object> data);

    /**
     * Serializes the model object to a Java map.
     * 
     * @return
     */
    public abstract Map<String, Object> toMap();
}
