/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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