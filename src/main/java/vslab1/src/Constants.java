package vslab1.src;

public class Constants {
    public static final String PEERCONFIGFILENAME = ".peers.json";
    public static final String PEERCONFIGFILEPATH = ".filesharing_data"; 

    public static final String PEERCONFIGFILEINITIALCONTENT = 
        "{\"peer\":{\"ipAddress\":\"\",\"port\":0,\"files\":[]}," + 
         "\"peers\":[" + 
            "{\"ipAddress\":\"127.0.0.1\",\"port\":5001,\"files\":[],\"onlineState\":\"offline\"}" + 
            ",{\"ipAddress\":\"127.0.0.1\",\"port\":5002,\"files\":[],\"onlineState\":\"offline\"}" +
         "]}";
}
