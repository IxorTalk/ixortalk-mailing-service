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

import com.ixortalk.mailing.service.config.IxortalkConfigProperties;
import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.MimeMessage;

import static java.util.Locale.forLanguageTag;

@Named
@EnableConfigurationProperties(IxortalkConfigProperties.class)
public class SendMailService {

    private final Logger log = LoggerFactory.getLogger(SendMailService.class);

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private IxortalkConfigProperties ixortalkConfigProperties;

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(ixortalkConfigProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendTemplateEmail(SendMailVO sendMailVO) {
        log.debug("Sending templated ({}) e-mail to '{}'", sendMailVO.getTemplateName(), sendMailVO.getToEmail());
        Context context = new Context(forLanguageTag(sendMailVO.getLanguageTag()));
        context.setVariables(sendMailVO.getAdditionalVariables());
        String content = templateEngine.process(sendMailVO.getTemplateName(), context);
        String subject = messageSource.getMessage(sendMailVO.getSubjectI18NKey(), null, forLanguageTag(sendMailVO.getLanguageTag()));
        sendEmail(sendMailVO.getToEmail(), subject, content, false, true);
    }
}

