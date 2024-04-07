/**
 * @author Aaron Moser
 */

package vslab1.src.Receiving;

import org.json.JSONException;
import org.json.JSONObject;

public record ReceivedData(String data) {

    public JSONObject interpretAsJSONObject() {
        try {
            JSONObject receivedDataAsJSONObject = null;
            receivedDataAsJSONObject = new JSONObject(data);
            return receivedDataAsJSONObject;
        } catch (JSONException e) {
            System.out.println(data);
            System.err.println("File is a json file, not able to parse it.");
        }
        return null;
    }
}
