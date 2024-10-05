package ai.ilopezluna.smartscraper;

import ai.ilopezluna.smartscraper.agents.DOMinator;
import ai.ilopezluna.smartscraper.agents.Evaluator;
import ai.ilopezluna.smartscraper.agents.Extractor;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class App {

	final static int CHUNK_SIZE = 5000;

	@SneakyThrows
	public static void main(String[] args) {
		String url = "https://ollama.com/library";
		String html = getHtml(url);

		DOMinator dominator = AiServices.create(DOMinator.class, modelBuilder("llama3.2", "3b").build());
		Evaluator evaluator = AiServices.create(Evaluator.class, modelBuilder("bespoke-minicheck", "7b").build());

		String xpath = dominator.getXpath(
				"Create a xpath expression to retrieve the list of models, each model contains a title a description and a list of tags: %s"
					.formatted(html.substring(0, CHUNK_SIZE)));

		if (!isValid(url, xpath)) {
			log.error("Invalid XPath: {}", xpath);
			return;
		}

		String candidate = extractText(url, xpath).substring(0, CHUNK_SIZE);
		String verify = evaluator.evaluate(candidate, "multiple large language models names, descriptions and tags");

		log.info("XPath: {}", xpath);
		log.info("Candidate: {}", candidate);
		log.info("Verify: {}", verify);

		if (verify.equalsIgnoreCase("no")) {
			return;
		}

		Extractor extractor = AiServices.create(Extractor.class, modelBuilder("llama3.2", "3b").format("json").build());
		extractor.extract(extractText(url, xpath)).models().forEach(m -> log.info(m.toString()));
	}

	private static OllamaChatModel.OllamaChatModelBuilder modelBuilder(String name, String tag) {
		var ollamaContainer = new OllamaContainer(DockerImageName.parse("ilopezluna/%s:0.3.12-%s".formatted(name, tag))
			.asCompatibleSubstituteFor("ollama/ollama")).withReuse(true);
		ollamaContainer.start();
		return OllamaChatModel.builder()
			.baseUrl(ollamaContainer.getEndpoint())
			.modelName("%s:%s".formatted(name, tag))
			.temperature(0d)
			.topP(0d)
			.topK(1)
			.seed(42)
			.logRequests(true)
			.logResponses(true);
	}

	public static boolean isValid(String url, String xpath) {
		try {
			Elements elements = Jsoup.connect(url).get().selectXpath(xpath);
			return !elements.isEmpty();
		}
		catch (Exception e) {
			return false;
		}
	}

	@SneakyThrows
	public static String extractText(String url, String xpath) {
		Elements elements = Jsoup.connect(url).get().selectXpath(xpath);
		return elements.text();
	}

	@SneakyThrows
	public static String getHtml(String url) {
		Document document = Jsoup.connect(url).get();
		document.getAllElements().forEach(Element::clearAttributes);
		document.select("script,style,meta").remove();
		return document.html().trim().replaceAll("\\r?\\n", "").replaceAll("\\s+", "");
	}

}
