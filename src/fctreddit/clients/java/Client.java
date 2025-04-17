package fctreddit.clients.java;


import java.util.logging.Logger;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Invocation.Builder;


public abstract class Client{

    private static Logger Log = Logger.getLogger(Client.class.getName());

    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 5000;

    protected Response executeOperationsPost(Builder req, Entity<?> e){

        for(int i = 0; i < MAX_RETRIES; i++){
            try{
                return req.post(e);
            }catch (ProcessingException x){
                Log.info(x.getMessage());

                try{
                    Thread.sleep(RETRY_SLEEP);
                }catch (InterruptedException ex){

                }
            }catch (Exception x){
                x.printStackTrace();
            }
        }
        return null;
    }


    protected Response executeOperationsGet(Builder req){
        for(int i = 0; i < MAX_RETRIES; i++){
            try{
                return req.get();
            }catch (ProcessingException x){
                Log.info(x.getMessage());

                try{
                    Thread.sleep(RETRY_SLEEP);
                }catch (InterruptedException ex){

                }
            }catch (Exception x){
                x.printStackTrace();
            }
        }
        return null;
    }

    protected Response executeOperationsDelete(Builder req){

        for(int i = 0; i < MAX_RETRIES; i++){
            try{
                return req.delete();
            }catch (ProcessingException x){
                Log.info(x.getMessage());

                try{
                    Thread.sleep(RETRY_SLEEP);
                }catch (InterruptedException ex){

                }
            }catch (Exception x){
                x.printStackTrace();
            }
        }
        return null;
    }

    protected Response executeOperationsPut(Builder req, Entity<?> e){

        for(int i = 0; i < MAX_RETRIES; i++){
            try{
                return req.put(e);
            }catch (ProcessingException x){
                Log.info(x.getMessage());

                try{
                    Thread.sleep(RETRY_SLEEP);
                }catch (InterruptedException ex){

                }
            }catch (Exception x){
                x.printStackTrace();
            }
        }
        return null;
    }



}
