package io.github.pmckeown.rest.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.github.pmckeown.rest.model.Bom;
import io.github.pmckeown.rest.model.Response;
import io.github.pmckeown.rest.ResourceConstants;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test Dependency Track client
 *
 * @author Paul McKeown
 */
public class DependencyTrackClientTest {

    static final String API_KEY = "api123";
    static final String HOST = "http://localhost:";
    static final String BASE_64_ENCODED_BOM = "blah";
    static final String PROJECT_VERSION = "1.0";
    static final String PROJECT_NAME = "testProject";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Test
    public void thatBomIsSentAsExpected() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        DependencyTrackClient client = new DependencyTrackClient(HOST + wireMockRule.port(), API_KEY);
        Response response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatHttpErrorsWhenUploadingBomsAreTranslatedIntoAResponse() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(status(418)));

        DependencyTrackClient client = new DependencyTrackClient(HOST + wireMockRule.port(), API_KEY);
        Response response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatConnectionErrorsWhenUploadingBomsAreTranslatedIntoAResponse() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM))
                .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

        DependencyTrackClient client = new DependencyTrackClient(HOST + wireMockRule.port(), API_KEY);
        Response response = client.uploadBom(aBom());

        assertThat(response.getStatus(), is(equalTo(-1)));

        verify(1, putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatProvidedBaseUrlsHaveTrailingSlashesRemoved() throws Exception {
        String url = "http://www.slashes-r-us.com/";

        DependencyTrackClient client = new DependencyTrackClient(url, "api123");

        assertThat(client.getHost(), is(equalTo("http://www.slashes-r-us.com")));
    }

    @Test
    public void thatProvidedBaseUrlsWithougTrailingSlashesAreNotModified() throws Exception {
        String url = "http://www.slashes-r-not-us.com";

        DependencyTrackClient client = new DependencyTrackClient(url, "api123");

        assertThat(client.getHost(), is(equalTo("http://www.slashes-r-not-us.com")));
    }

    /*
     * Helper methods
     */

    private Bom aBom() {
        return new Bom(PROJECT_NAME, PROJECT_VERSION, false, BASE_64_ENCODED_BOM);
    }

}