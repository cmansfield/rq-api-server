package com.reliaquest.api.annotation;

import com.reliaquest.api.extension.RetryTestExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * {@code @RetryTest} is used to signal that the annotated test method should
 * be {@linkplain #value retried a specified number of times} if it fails.
 * Similar to {@link org.junit.jupiter.api.RepeatedTest}'s <em>failureThreshold</em>,
 * but will retry the test until it passes, or until it reaches the
 * {@linkplain #value specified value}.
 *
 * <p>Optional {@linkplain #delay delay} can be specified to wait between retries
 * in milliseconds.
 *
 *<p>Good for tests the rely on external services that may be flaky.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RetryTestExtension.class)
public @interface RetryTest {
    int value() default 1;

    long delay() default 0;
}
