package se.lth.cs.hajenner.demo;

import org.json.JSONArray;
import org.json.JSONObject;
import se.lth.cs.hajenner.Candidate;
import se.lth.cs.hajenner.Mention;
import se.lth.cs.hajenner.Mentions;

/**
 * Created by antonsodergren on 3/4/16.
 * BratTranslator creates a JSONObject that brat can read from the data structure
 */
public class BratTranslator {
    private Mentions mentions;
    public BratTranslator(Mentions mentions) {
        this.mentions = mentions;
    }

    public String getDocData() {
        JSONObject jsonObject = makeDocData();
        if(jsonObject!=null) {
            return jsonObject.toString();
        }
        return "";
    }
    private JSONObject makeDocData() {
        try {
            JSONObject docData = new JSONObject();
            docData.put("text", mentions.getText());
            JSONArray entities = new JSONArray();
            JSONArray comments = new JSONArray();
            int i = 0;
            for (Mention match:mentions.getList())
            {
                JSONArray entityLine = new JSONArray();
                JSONArray commentLine = new JSONArray();

                //Id and type
                String name = "T"+(i+1);
                entityLine.put(name);
                entityLine.put("Name");

                commentLine.put(name);
                commentLine.put("HTML");

                //Start and end
                JSONArray buffervals = new JSONArray();

                buffervals.put(match.getStart());
                buffervals.put(match.getEnd());
                JSONArray wrapper = new JSONArray();
                wrapper.put(buffervals);
                entityLine.put(wrapper);
                String links = "";
                for(Candidate node: match.getCandidates()) {
                    links+=node.getCount()+" ";
                    links+=node.getLink();
                    links+="<br>";
                }
                commentLine.put(links);
                comments.put(commentLine);
                entities.put(entityLine);
                i++;
            }
            docData.put("entities",entities);
            docData.put("comments",comments);
            return docData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
