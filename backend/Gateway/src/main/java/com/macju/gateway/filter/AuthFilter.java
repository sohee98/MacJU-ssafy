package com.macju.gateway.filter;


import com.macju.gateway.exeption.UnAuthorizedException;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;


@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    private static final Logger logger = LogManager.getLogger(AuthFilter.class);
    private static final String reqURL = "http://i6c107.p.ssafy.io:8752/oauth/access/check";
    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
//        System.out.println("Member In After Return");
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = request.getHeaders();
            String token = headers.getFirst("AccessToken");
//            headers.forEach((k,v)->{
//                logger.info(k + " : " + v);
//            });
//
//            MultiValueMap<String, String> mvm = request.getQueryParams();
//            logger.info(mvm.getFirst("nickName"));
            logger.info("AccessToken"+token);


            String json = "{\"accessToken\": \""+token+"\"}";

            logger.info(json);
            if(!httpPostBodyConnection(reqURL,json)){
                return handleUnAuth(exchange);

//                throw new UnAuthorizedException();
            }


//            System.out.println("Member Filter In");
            logger.info("AuthFilter baseMessage>>>>>>" + config.getBaseMessage());
            if (config.isPreLogger()) {
                logger.info("AuthFilter Start>>>>>>" + exchange.getRequest());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if (config.isPostLogger()) {
                    logger.info("AuthFilter End>>>>>>" + exchange.getResponse());
                }
            }));
        });
    }

    private Mono<Void> handleUnAuth(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();

    }

    public static boolean httpPostBodyConnection(String UrlData, String ParamData) {

        //http ?????? ??? ????????? url ????????? ?????? ??????
        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        //http ????????? ???????????? ?????? ?????? ??????
        URL url = null;
        HttpURLConnection conn = null;

        //http ?????? ?????? ??? ?????? ?????? ???????????? ?????? ?????? ??????
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;
        String result = "";
        //????????? ?????? ???????????? ???????????? ?????? ??????
        String returnData = "";
        String responseCode = "";
        try {
            //??????????????? ????????? url??? ????????? connection ??????
            url = new URL(totalUrl);
            conn = (HttpURLConnection) url.openConnection();

            //http ????????? ????????? ?????? ?????? ??????
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8"); //post body json?????? ????????? ??????
//            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); //OutputStream??? ???????????? post body ????????? ??????
            try (OutputStream os = conn.getOutputStream()){
                byte request_data[] = ParamData.getBytes("utf-8");
                os.write(request_data);
                os.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            //http ?????? ??????
            conn.connect();

            //http ?????? ??? ?????? ?????? ???????????? ????????? ?????????
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer??? ???????????? ????????? ??????????????? ?????? ??????
            }

            //????????? ?????? ?????? ??? ???????????? ????????? ?????? ????????? ?????? ??????
            returnData = sb.toString();

            //http ?????? ?????? ?????? ?????? ??????
            responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http ?????? ?????? : "+responseCode);
            System.out.println("http ?????? ????????? : "+returnData);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(returnData);

            result = element.getAsJsonObject().get("result").getAsString();


        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //http ?????? ??? ?????? ?????? ??? BufferedReader??? ???????????????
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.equals("success")? true : false;
        }

    }
    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

}
