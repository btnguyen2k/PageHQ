package compositions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

@With(FbAuthAction.class)
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FbAuth {
}
