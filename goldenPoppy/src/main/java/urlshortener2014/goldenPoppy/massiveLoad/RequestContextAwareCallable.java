package urlshortener2014.goldenPoppy.massiveLoad;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 
 * @author csamuel
 * @link http://stackoverflow.com/questions/1528444/accessing-scoped-proxy-beans-within-threads-of
 * 
 */
public abstract class RequestContextAwareCallable<V> implements Callable<V> {

    private final RequestAttributes requestAttributes;
    private Thread thread;

    public RequestContextAwareCallable() {
        this.requestAttributes = RequestContextHolder.getRequestAttributes();
        this.thread = Thread.currentThread();
    }

    public V call() throws Exception {
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

    public abstract V onCall() throws Exception;
}