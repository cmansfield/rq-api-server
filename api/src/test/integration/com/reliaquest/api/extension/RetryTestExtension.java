package com.reliaquest.api.extension;

import com.reliaquest.api.annotation.RetryTest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

@Slf4j
public class RetryTestExtension implements TestTemplateInvocationContextProvider, TestExecutionExceptionHandler {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return context.getTestMethod().isPresent()
                && context.getTestMethod().get().isAnnotationPresent(RetryTest.class);
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        int retries =
                context.getTestMethod().get().getAnnotation(RetryTest.class).value();
        return Stream.generate(() -> new RetryTestInvocationContext(retries));
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        RetryTest retryTest = context.getTestMethod().get().getAnnotation(RetryTest.class);
        int retries = retryTest.value();
        var delay = retryTest.delay();
        var current = 1;

        while (current < retries) {
            try {
                if (context.getTestMethod().isEmpty()) {
                    return;
                }

                log.info(
                        "Retrying test method: {}{}. Retry {} out of {}",
                        (delay > 0L) ? "waiting " + delay + "ms before " : "",
                        context.getTestMethod().get().getName(),
                        current,
                        retries);
                if (delay > 0L) {
                    Thread.sleep(delay);
                }
                context.getExecutableInvoker().invoke(context.getTestMethod().get(), context.getRequiredTestInstance());
                return;
            } catch (Throwable e) {
                var store = context.getStore(ExtensionContext.Namespace.GLOBAL);
                store.put(context.getUniqueId(), current + 1);
                current = store.get(context.getUniqueId(), Integer.class);
            }
        }
        throw throwable;
    }

    private record RetryTestInvocationContext(int retries) implements TestTemplateInvocationContext {
        @Override
        public List<Extension> getAdditionalExtensions() {
            return Collections.singletonList(new RetryTestExtension());
        }
    }
}
