package se.lth.cs.hajenner.demo;


import net.codestory.http.Configuration;
import net.codestory.http.WebServer;
import net.codestory.http.payload.Payload;
import net.codestory.http.routes.Routes;
import net.codestory.http.templating.ModelAndView;
import se.lth.cs.hajenner.HERD;
import se.lth.cs.hajenner.Mentions;
import se.lth.cs.hajenner.lucene.LuceneCommunicator;
import se.lth.cs.hajenner.Resources;
import se.lth.cs.hajenner.pagerank.PageRank;
import se.lth.cs.hajenner.sunflower.Sunflower;


public class Main {
	private static PageRank pageRank;
	private static Sunflower sunflower;
	public static void main(String[] args) {
		pageRank = Resources.Instance.pageRank;
		sunflower = Resources.Instance.sunflower;
		new WebServer().configure(new WebConfiguration()).start();
	}

	public static class WebConfiguration implements Configuration {

		public void configure(Routes routes) {
			routes.get("/", ModelAndView.of("index"))
			.post("/",context -> {
				String text = context.query().get("taggable");
				String language = context.query().get("language");

				HERD herd = new HERD(language);
				LuceneCommunicator lucene =
						new LuceneCommunicator(Resources.luceneDir,language);
				Mentions mentions = herd.tag(text,lucene, sunflower, pageRank);

				BratTranslator bratTranslator = new BratTranslator(mentions);
				String docData = bratTranslator.getDocData();

				System.out.println(docData);

				return new Payload(ModelAndView.of("index","untagged",text,"docdata_inject","docData="+docData, language, "selected=true"));
			});
		}
	}
}
