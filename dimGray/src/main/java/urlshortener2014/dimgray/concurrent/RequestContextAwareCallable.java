package urlshortener2014.dimgray.concurrent;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public abstract class RequestContextAwareCallable<V> implements Callable<V> {

    private final RequestAttributes requestAttributes;
    private Thread thread;

    public RequestContextAwareCallable() {
        this.requestAttributes = RequestContextHolder.getRequestAttributes();
        this.thread = Thread.currentThread();
    }

    public V call() {
        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            return onCall();
        } finally {
            if (Thread.currentThread() != thread) {
                RequestContextHolder.resetRequestAttributes();
            }
            thread = null;
        }
    }

    public abstract V onCall();
}
