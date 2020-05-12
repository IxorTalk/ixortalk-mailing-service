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
import com.ixortalk.mailing.service.AbstractSpringIntegrationTest;
import com.ixortalk.mailing.service.send.SendMailVO;
import org.junit.Test;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Collections.singletonMap;
import static javax.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;

public class MailControllerIntegrationTest extends AbstractSpringIntegrationTest {

    public static final Map<String, Object> ADDITIONAL_VARIABLES = singletonMap("someKey", "someValue");
    public static final String TO_EMAIL = "user@email.com";
    public static final String LANGUAGE_TAG_NL = "nl";
    public static final String TEST_MESSAGE_KEY = "test.message";
    public static final String TEST_TEMPLATE = "test-template";

    @Test
    public void success() throws MessagingException, JsonProcessingException {
        given()
                .auth().preemptive().oauth2(ADMIN_JWT_TOKEN)
                .when()
                .body(objectMapper.writeValueAsString(new SendMailVO(TO_EMAIL, LANGUAGE_TAG_NL, TEST_MESSAGE_KEY, TEST_TEMPLATE, ADDITIONAL_VARIABLES)))
                .contentType(JSON)
                .post("/send")
                .then()
                .statusCode(HTTP_OK);

        greenMail.waitForIncomingEmail(1);

        assertThat(greenMail.getReceivedMessages()).hasSize(1);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertThat(message.getRecipients(TO)).hasSize(1).extracting(Address::toString).containsOnly(TO_EMAIL);
        assertThat(message.getSubject()).isEqualTo("Test message nl");
    }
}