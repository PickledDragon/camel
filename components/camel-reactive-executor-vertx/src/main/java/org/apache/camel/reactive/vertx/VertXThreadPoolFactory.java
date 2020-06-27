package org.apache.camel.reactive.vertx;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.vertx.core.Vertx;
import org.apache.camel.Experimental;
import org.apache.camel.spi.ThreadPoolFactory;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.support.DefaultThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Experimental
public class VertXThreadPoolFactory extends DefaultThreadPoolFactory implements ThreadPoolFactory {

    private static final Logger LOG = LoggerFactory.getLogger(VertXReactiveExecutor.class);

    private final ExecutorService executorService = new VertXExecutorService();
    private Vertx vertx;

    public Vertx getVertx() {
        return vertx;
    }

    /**
     * To use an existing instance of {@link Vertx} instead of creating a default instance.
     */
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public ExecutorService newThreadPool(ThreadPoolProfile profile, ThreadFactory threadFactory) {
        return executorService;
    }

    @Override
    public ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return executorService;
    }

    private class VertXExecutorService implements ExecutorService {

        @Override
        public void shutdown() {
            // noop
        }

        @Override
        public List<Runnable> shutdownNow() {
            // noop
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable task) {
            LOG.trace("submit: {}", task);
            final CompletableFuture<?> answer = new CompletableFuture<>();
            // used by vertx
            vertx.executeBlocking(future -> {
                task.run();
                future.complete();
            }, res -> { answer.complete(null);} );
            return answer;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public void execute(Runnable command) {
            // noop
        }
    }

}
