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
import com.ixortalk.mailing.service.AbstractSpringIntegrationTest;
import com.ixortalk.mailing.service.send.SendMailVO;
import org.junit.Test;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
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
                .auth().preemptive().oauth2(adminToken().getValue())
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