import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class TestEqualsMaps {
    public static void main(String[] args) {
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        System.out.println(new EqualsBuilder().append(map1, map2).isEquals());

        map1.put("a", 1);
        map1.put("b", 2);
        map2.put("b", 2);
        map2.put("c", 3);
        System.out.println(new EqualsBuilder().append(map1, map2).isEquals());

        map2.put("a", 1);
        map1.put("c", 3);
        System.out.println(new EqualsBuilder().append(map1, map2).isEquals());
    }
}
