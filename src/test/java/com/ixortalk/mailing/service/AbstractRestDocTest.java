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
package com.ixortalk.mailing.service;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.restassured.operation.preprocess.UriModifyingOperationPreprocessor;

import static com.google.common.net.HttpHeaders.X_FORWARDED_HOST;
import static com.google.common.net.HttpHeaders.X_FORWARDED_PORT;
import static com.google.common.net.HttpHeaders.X_FORWARDED_PROTO;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;

public class AbstractRestDocTest extends AbstractSpringIntegrationTest {

    public static final String HOST_IXORTALK_COM = "www.ixortalk.com";
    public static final String HTTPS = "https";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    protected RequestSpecification spec;

    @Before
    public void initializeRestDocsSpec() {
        spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(this.restDocumentation))
                .addHeader(X_FORWARDED_PROTO, HTTPS)
                .addHeader(X_FORWARDED_HOST, HOST_IXORTALK_COM)
                .addHeader(X_FORWARDED_PORT, "")
                .build();
    }

    protected static UriModifyingOperationPreprocessor staticUris() {
        return modifyUris().scheme(HTTPS).host(HOST_IXORTALK_COM).removePort();
    }

    public static HeaderDescriptor describeAuthorizationTokenHeader() {
        return headerWithName("Authorization").description("The bearer token needed to authorize this request.");
    }
}
