/**
 *
 *  2016 (c) IxorTalk CVBA
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of IxorTalk CVBA
 *
 * The intellectual and technical concepts contained
 * herein are proprietary to IxorTalk CVBA
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 *
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from IxorTalk CVBA.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */
package com.ixortalk.mailing.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ixortalk.mailing.service.AbstractRestDocTest;
import com.ixortalk.mailing.service.send.SendMailVO;
import org.junit.Test;

import java.util.Map;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Collections.singletonMap;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class MailControllerRestDocTest extends AbstractRestDocTest {

    public static final Map<String, Object> ADDITIONAL_VARIABLES = singletonMap("someKey", "someValue");
    public static final String TO_EMAIL = "user@email.com";
    public static final String LANGUAGE_TAG_NL = "nl";
    public static final String TEST_MESSAGE_KEY = "test.message";
    public static final String TEST_TEMPLATE = "test-template";

    @Test
    public void success() throws JsonProcessingException {
        given(this.spec)
                .auth().preemptive().oauth2(adminToken().getValue())
                .filter(
                        document("mailing/send/success",
                                preprocessRequest(staticUris(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(describeAuthorizationTokenHeader()),
                                requestFields(
                                        fieldWithPath("toEmail").type(STRING).description("The receiver's emailaddress"),
                                        fieldWithPath("languageTag").type(STRING).description("The languageTag for the language to use (as specified by `java.util.Locale.forLanguageTag`)"),
                                        fieldWithPath("subjectI18NKey").type(STRING).description("The i18n message key to use as subject for the email, i18n messages are served through the config server"),
                                        fieldWithPath("templateName").type(STRING).description("The name of the template to use, templates are served through the config server"),
                                        fieldWithPath("additionalVariables").type(OBJECT).description("A map containing additional variables that can be used in the template")
                                )
                        )
                )
                .when()
                .body(objectMapper.writeValueAsString(new SendMailVO(TO_EMAIL, LANGUAGE_TAG_NL, TEST_MESSAGE_KEY, TEST_TEMPLATE, ADDITIONAL_VARIABLES)))
                .contentType(JSON)
                .post("/send")
                .then()
                .statusCode(HTTP_OK);
    }
}