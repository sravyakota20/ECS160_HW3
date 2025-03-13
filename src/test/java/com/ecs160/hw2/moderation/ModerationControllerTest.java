package com.ecs160.hw2.moderation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.ecs160.hw2.moderation.ModerationController.PostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class ModerationControllerTest {

	private ModerationController controller;
	private RestTemplate restTemplate;
	private MockRestServiceServer mockServer;

	@BeforeEach
	public void setup() {
		controller = new ModerationController();
		// Inject a RestTemplate into the controller
		restTemplate = new RestTemplate();
		// Using reflection or setter if your controller allows it, here we assume we can set it directly:
		controller.setRestTemplate(restTemplate);

		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	public void testModerationFailsDueToBannedWord() {
		PostRequest request = new PostRequest();
		request.setPostContent("This post contains hack which is banned.");

		ResponseEntity<String> response = controller.moderate(request);
		// Expect moderation to fail and return "[DELETED]"
		assertEquals("[DELETED]", response.getBody());
	}

	@Test
	public void testModerationPassesAndCallsHashtagService() {
		// Prepare a request without banned words
		PostRequest request = new PostRequest();
		request.setPostContent("This is a security related post.");

		// Set up the mock server to expect a call to the hashtagging service.
		String expectedHashtag = "#security";
		mockServer.expect(requestTo("http://localhost:30001/hashtag"))
				.andExpect(method(org.springframework.http.HttpMethod.POST))
				.andRespond(withSuccess("{\"hashtag\":\"" + expectedHashtag + "\"}", MediaType.APPLICATION_JSON));

		ResponseEntity<String> response = controller.moderate(request);
		// The moderated content should have the original content plus the returned hashtag.
		String expectedResponse = "This is a security related post. " + expectedHashtag;
		assertEquals(expectedResponse, response.getBody());

		mockServer.verify();
	}
}
