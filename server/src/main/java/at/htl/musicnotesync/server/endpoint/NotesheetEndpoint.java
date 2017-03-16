package at.htl.musicnotesync.server.endpoint;

import at.htl.musicnotesync.server.facade.NotesheetFacade;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Created by michael on 12/7/16.
 */
@Stateless
@Path("notesheet")
public class NotesheetEndpoint {
    private static final int MEGABYTE = 1024000;

    @Inject
    NotesheetFacade notesheetFacade;

    @POST
    @Consumes({MediaType.APPLICATION_OCTET_STREAM})
    public Response upload(@HeaderParam("filename")String uuid,
                           InputStream inputStream){

        File file = new File(uuid);

        if(file.exists()){
            file.delete();
        }

        try {
            byte[] buffer = new byte[MEGABYTE];
            int read = -1;
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            while((read = inputStream.read(buffer)) > -1){
                fileOutputStream.write(buffer, 0, read);
            }

            fileOutputStream.close();
            inputStream.close();

            if(file.exists()){
                return Response.status(Response.Status.OK).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
//        String uuid = null;
//        long resultCode = -1;
//        Map<String, List<InputPart>> inputParts = input.getFormDataMap();
//        List<InputPart> inputPartList = inputParts.get("file");
//        inputPartList.addAll(inputParts.get("uuid"));
//
//        if(inputPartList != null && inputPartList.size() > 1){
//            InputPart filePart = inputPartList.get(0);
//            InputPart uuidPart = inputPartList.get(1);
//            MultivaluedMap<String, String> header = filePart.getHeaders();
//            String filename = header.get("Content-Disposition").get(0);
//            InputStream inputStream = null;
//
//            try {
//                 inputStream = filePart.getBody(InputStream.class, null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            filename =
////                    filename
////                    .substring(filename.indexOf("filename"));
////            filename =
////                    filename.substring(filename.indexOf("\"")+1);
////            filename =
////                    filename.substring(0, filename.indexOf("\""));
//            try {
//                uuid = uuidPart.getBody(String.class, null);
//                resultCode = notesheetFacade.save(uuid, inputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        Response resultResponse = null;
//
//        if(resultCode > 0) {
//            resultResponse = Response.status(Response.Status.OK).entity(uuid).build();
//        }else{
//            if (resultCode == -1){
//                resultResponse = Response.status(Response.Status.EXPECTATION_FAILED).build();
//            }
//            else if(resultCode == -2){
//                resultResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//            }
//            else if(resultCode == -3){
//                resultResponse = Response.status(Response.Status.BAD_REQUEST).build();
//            }
//            else{
//                resultResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//
//
//        return resultResponse;
    }

    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("uuid") String uuid){
        final File file = new File(uuid);
        Response result = null;

        if(file.exists() == false){
            result = Response.status(Response.Status.BAD_REQUEST).build();
            return result;
        }
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[(int) file.length()];

        try {
            stream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = Response
                .ok(buffer, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Length", file.length())
                .build();


        return result;
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
