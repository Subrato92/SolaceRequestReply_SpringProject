package com.subrato.packages.solace.RequestReplyDemo.controller;

import com.subrato.packages.solace.RequestReplyDemo.config.SolaceReplier;
import com.subrato.packages.solace.RequestReplyDemo.config.SolaceRequestor;
import com.subrato.packages.solace.RequestReplyDemo.pojo.RequestorReplierPayload;
import com.subrato.packages.solace.RequestReplyDemo.pojo.StringResponse;
import com.subrato.packages.solace.RequestReplyDemo.pojo.TransmitterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@RequestMapping(value = "/solace/request_reply_demo")
@EnableSwagger2
public class RestEndPoints {

    private SolaceRequestor requestor = null;
    private SolaceReplier replier = null;
    Logger log = LoggerFactory.getLogger(RestEndPoints.class);

    @PostMapping(value = "/initialize-requestor",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE,
        headers = "Content-Type=application/json" )
    public void initializeRequestor(@RequestBody RequestorReplierPayload payload){
        requestor = new SolaceRequestor(payload);
    }

    @PostMapping(
            value = "/initialize-replier",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = "Content-Type=application/json" )
    public void initializeReplier(@RequestBody RequestorReplierPayload payload){
        replier = new SolaceReplier(payload);
    }

    @PostMapping(
            value = "/initialize-requestor-replier",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = "Content-Type=application/json" )
    public void initialize(@RequestBody RequestorReplierPayload payload){
        requestor = new SolaceRequestor(payload);
        replier = new SolaceReplier(payload);
    }

    @PostMapping(
           value = "/send",
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE,
           headers = "Content-Type=application/json"
    )
    public @ResponseBody TransmitterResponse sendMessage(@RequestBody String message){

        if( requestor == null ){
            return new TransmitterResponse(false, "Instances are not initialized", null);
        }

        TransmitterResponse response = requestor.transmitter(message);
        log.info("ReplyText:{}",response.getReplyText());
        log.info(response.getReplyBody().toString());

        return response;
    }

    @GetMapping(value = "/reset",
    produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody StringResponse reset(){
        if(requestor == null && replier == null){
            return new StringResponse("No Instances Found.", false);
        }

        if(requestor != null){
            requestor.reset();
            requestor = null;
        }

        if(replier != null){
            replier.reset();
            replier = null;
        }

        return new StringResponse("Instance Killed...", true);
    }

}
