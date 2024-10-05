package ai.ilopezluna.smartscraper.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface Extractor {

	@UserMessage("""
			I will provide a text containing information about multiple Large Language Models.
			Your task is to normalize the text and extract the names, descriptions and parameters of each model.

			A hint for the parameters is that they are always a number followed by a 'B' or 'M'.
			A hint for the pulls is that they are always a number followed by a 'K'.

			Take a deep breath and work on this problem step-by-step.

			Text: {{text}}
			""")
	Models extract(@V("text") String text);

	record Models(List<Model> models) {
	}

	record Model(String name, String description, List<String> parameters, String pulls, String tags) {
	}

}
