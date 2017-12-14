package com.common.pool;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 *
 * @author Bruce
 * @date 2017/12/9
 */
public class PooledClientFactory {

    private final static PooledClientFactory instance = new PooledClientFactory();
    private final GenericObjectPool clientPool = new GenericObjectPool();

    public PooledClientFactory() {
        clientPool.setFactory(new PoolableObjectFactory() {
            @Override
            public boolean validateObject(Object arg0) {
                return false;
            }

            @Override
            public void passivateObject(Object arg0) throws Exception {
            }

            @Override
            public Object makeObject() throws Exception {
                System.out.println("为线程 [ " + Thread.currentThread().getName() + " ] 创建新的WebClient实例!");
                WebClient client = new WebClient(BrowserVersion.FIREFOX_3_6);
                client.setCssEnabled(false);
                client.setJavaScriptEnabled(false);
                return client;
            }

            @Override
            public void destroyObject(Object arg0) throws Exception {
                WebClient client = (WebClient) arg0;
                client.closeAllWindows();
                client = null;
            }

            @Override
            public void activateObject(Object arg0) throws Exception {
            }
        });
    }

    public static PooledClientFactory getInstance() {
        return instance;
    }

    public WebClient getClient() throws Exception {
        WebClient webClient = new WebClient();
        return (WebClient) this.clientPool.borrowObject();
    }

    public void returnClient(WebClient client) throws Exception {
        this.clientPool.returnObject(client);
    }
}
