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
package com.ixortalk.mailing.service.send;

import java.util.Map;

public class SendMailVO {

    private String toEmail;
    private String languageTag;
    private String subjectI18NKey;
    private String templateName;
    private Map<String, Object> additionalVariables;

    private SendMailVO() {}

    public SendMailVO(String toEmail, String languageTag, String subjectI18NKey, String templateName, Map<String, Object> additionalVariables) {
        this.toEmail = toEmail;
        this.languageTag = languageTag;
        this.subjectI18NKey = subjectI18NKey;
        this.templateName = templateName;
        this.additionalVariables = additionalVariables;
    }

    public String getToEmail() {
        return toEmail;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getSubjectI18NKey() {
        return subjectI18NKey;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getAdditionalVariables() {
        return additionalVariables;
    }
}
