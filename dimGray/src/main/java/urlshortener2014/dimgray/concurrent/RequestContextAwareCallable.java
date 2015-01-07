package urlshortener2014.dimgray.concurrent;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * Superclase que soluciona el problema del contexto de los ThreadPool en Spring.
 * @author csamuel	
 * @link http://stackoverflow.com/questions/1528444/accessing-scoped-proxy-beans-within-threads-of
 *
 * @param <V> parámetro genérico que devuelve el método call() de la clase.
 */
public abstract class RequestContextAwareCallable<V> implements Callable<V> {

    private final RequestAttributes requestAttributes;
    private Thread thread;

    /**
     * Método constructor de la clase.
     */
    public RequestContextAwareCallable() {
        this.requestAttributes = RequestContextHolder.getRequestAttributes();
        this.thread = Thread.currentThread();
    }

    /**
     * Método que se encarga de solucionar el problema del contexto de Spring.
     */
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

    /**
     * Método para implementar para la clase que extienda esta clase.
     * @return un parámetro genérico V.
     */
    public abstract V onCall();
}
