package at.htl.musicnotesync.server.endpoint;

import at.htl.musicnotesync.server.facade.NotesheetFacade;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 12/7/16.
 */
@Stateless
@Path("notesheet")
public class NotesheetEndpoint {
    @Inject
    NotesheetFacade notesheetFacade;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(MultipartFormDataInput input){
        long id = -1;
        Map<String, List<InputPart>> inputParts = input.getFormDataMap();
        List<InputPart> inputPartList = inputParts.get("file");

        if(inputPartList != null && inputPartList.size() > 0){
            InputPart part = inputPartList.get(0);
            MultivaluedMap<String, String> header = part.getHeaders();
            String filename = header.get("Content-Disposition").get(0);
            InputStream inputStream = null;

            try {
                 inputStream = part.getBody(InputStream.class, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            filename =
                    filename
                    .substring(filename.indexOf("filename"));
            filename =
                    filename.substring(filename.indexOf("\"")+1);
            filename =
                    filename.substring(0, filename.indexOf("\""));


            id = notesheetFacade.save(filename, inputStream);
        }

        Response resultResponse = null;

        if(id > 0) {
            resultResponse = Response.status(Response.Status.OK).entity(id).build();
        }else{
            if (id == -1){
                resultResponse = Response.status(Response.Status.EXPECTATION_FAILED).build();
            }
            else if(id == -2){
                resultResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            else if(id == -3){
                resultResponse = Response.status(Response.Status.BAD_REQUEST).build();
            }
        }


        return resultResponse;
    }

    @GET
    public String usage(){
        StringBuilder usageBuilder = new StringBuilder();
        usageBuilder.append("Usage:")
                .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;")
                .append("REQUEST: POST request with multipart data<br>&nbsp;&nbsp;&nbsp;&nbsp;")
                .append("RESPONSE:")
                .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                .append("On " + Response.Status.OK.getStatusCode())
                .append(": Success with id of new object")
                .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                .append("On " + Response.Status.EXPECTATION_FAILED.getStatusCode())
                .append(": Could not create file on server")
                .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                .append("On " + Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .append(": Could not find created file on server")
                .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                .append("On " + Response.Status.BAD_REQUEST.getStatusCode())
                .append(": Could not read data");
        return usageBuilder.toString();
    }
}
